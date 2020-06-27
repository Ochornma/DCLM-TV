package org.dclm.live.playback

import android.annotation.TargetApi
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_playback.*
import org.dclm.live.R
import org.dclm.live.presenter.MyMediaPresenter
import org.dclm.live.detail.DetailActivity
import org.dclm.live.error.ErrorFragment
import org.dclm.live.model.Category
import org.dclm.live.model.Message
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class PlaybackFragment : VideoSupportFragment() {
    private var mPlayerGlue: VideoPlayerGlue? = null
    private var mPlayerAdapter: LeanbackPlayerAdapter? = null
     var mPlayer: SimpleExoPlayer? = null
    private var mTrackSelector: TrackSelector? = null
   // private var mPlaylistActionListener: PlaylistActionListener? = null
    private var mVideo: Message? = null
    private var live = false
    private var high = false
    private var repeat = false
    private var mQueue: RequestQueue? = null
    val categories: MutableList<Category> = java.util.ArrayList<Category>()
    private var rowsAdapter1:ArrayObjectAdapter? = null

   // private var mPlaylist: Playlist? = null
  //  private var mVideoLoaderCallbacks: VideoLoaderCallbacks? = null
    private var mVideoCursorAdapter: CursorObjectAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mVideo = requireActivity().intent.getParcelableExtra("message")
        live = requireActivity().intent.getBooleanExtra("live", false)
        high = requireActivity().intent.getBooleanExtra("high", false)
        mQueue = Volley.newRequestQueue(requireActivity().applicationContext)
        rowsAdapter1 = ArrayObjectAdapter(context?.let { MyMediaPresenter(it) })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.playerView?.useArtwork = true
        activity?.playerView?.defaultArtwork = activity?.let { ContextCompat.getDrawable(it, R.drawable.radio_banner) }

    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || mPlayer == null) {
            initializePlayer()
        }
    }

    /** Pauses the player.  */
    @TargetApi(Build.VERSION_CODES.N)
    override fun onPause() {
        super.onPause()
        if (mPlayerGlue != null && mPlayerGlue!!.isPlaying) {
            mPlayerGlue!!.pause()
        }
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT> 23) {
            releasePlayer()
        }


        //Toast.makeText(context, "2", Toast.LENGTH_LONG).show()
    }

    private fun initializePlayer() {
        val audioAttributes = AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.CONTENT_TYPE_SPEECH).build()
        mPlayer = context?.let { SimpleExoPlayer.Builder(it).build() }
        mPlayer!!.setAudioAttributes(audioAttributes, true)
        mPlayer!!.addListener(object : Player.EventListener {
            override fun onPlayerError(error: ExoPlaybackException) {
                if (!repeat){
                    val mediaSource = ProgressiveMediaSource.Factory((if (live){
                        RtmpDataSourceFactory()
                    } else{
                        DefaultHttpDataSourceFactory(
                            Util.getUserAgent(context!!, getString(R.string.app_name)),
                            null /* listener */,
                            30 * 1000,
                            30 * 1000,
                            true )
                    })
                        /* */
                    ).createMediaSource(Uri.parse(mVideo?.video))
                    mPlayer!!.prepare(mediaSource)
                    repeat = true
                }else{
                    val errorFragment = ErrorFragment()
                    activity?.supportFragmentManager!!.beginTransaction().replace(R.id.playback_controls_fragment, errorFragment)
                        .addToBackStack(null).commit()
                }

            }

        })
        //mPlayer = ExoPlayerFactory.newSimpleInstance(activity!!, mTrackSelector)
        mPlayerAdapter = LeanbackPlayerAdapter(activity!!, mPlayer!!, UPDATE_DELAY)
        //mPlaylistActionListener = PlaylistActionListener(mPlaylist)
        mPlayerGlue = VideoPlayerGlue(activity, mPlayerAdapter, null)
        mPlayerGlue!!.host = VideoSupportFragmentGlueHost(this)
        mPlayerGlue!!.playWhenPrepared()
        activity?.playerView?.player = mPlayer
        mVideo?.let { play(it) }
        val mRowsAdapter = initializeRelatedVideosRow()
        adapter = mRowsAdapter
    }

    private fun releasePlayer() {
        if (mPlayer != null) {
            mPlayer!!.release()
            mPlayer = null
            mTrackSelector = null
            mPlayerGlue = null
            mPlayerAdapter = null
          //  mPlaylistActionListener = null
        }
    }

    private fun play(video: Message) {
        mPlayerGlue!!.title = video.title
        mPlayerGlue!!.subtitle = activity?.resources?.getString(R.string.earnestly)
        prepareMediaForPlaying(video)
        mPlayerGlue!!.play()
    }

    private fun prepareMediaForPlaying(video: Message) {
        val userAgent: String = context?.let { Util.getUserAgent(it, getString(R.string.app_name)) }!!

       val mediaSource = ProgressiveMediaSource.Factory((if (live){
            RtmpDataSourceFactory()
        } else{
            DefaultHttpDataSourceFactory(
                Util.getUserAgent(context!!, getString(R.string.app_name)),
                null /* listener */,
                30 * 1000,
                30 * 1000,
                true )
        })
            /* */
        ).createMediaSource((if (high){
           Uri.parse(video.audio)
       } else{
           Uri.parse(video.audio)
       }))
        mPlayer!!.prepare(mediaSource)
        mPlayer!!.seekTo(mVideo?.seekto!!.toLong())
        activity?.findViewById<ImageView>(R.id.exo_artwork)?.setImageResource(R.drawable.radio_banner)
    }

    private fun initializeRelatedVideosRow(): ArrayObjectAdapter {
        /*
         * To add a new row to the mPlayerAdapter and not lose the controls row that is provided by the
         * glue, we need to compose a new row with the controls row and our related videos row.
         *
         * We start by creating a new {@link ClassPresenterSelector}. Then add the controls row from
         * the media player glue, then add the related videos row.
         */
        val presenterSelector = ClassPresenterSelector()
        presenterSelector.addClassPresenter(
            mPlayerGlue!!.controlsRow.javaClass, mPlayerGlue!!.playbackRowPresenter
        )
        presenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())
        val rowsAdapter = ArrayObjectAdapter(presenterSelector)
        rowsAdapter.add(mPlayerGlue!!.controlsRow)
        categoryJson()
        val header = (if (live){
            HeaderItem(getString(R.string.recent))
        } else{
            HeaderItem(getString(R.string.related))
        })
       // val row = ListRow(header, mVideoCursorAdapter)
      //  val adapter = ArrayObjectAdapter(presenterSelector)
        rowsAdapter.add(ListRow(header, rowsAdapter1))
       setOnItemViewClickedListener(ItemViewClickedListener())
        return rowsAdapter
    }

    private fun categoryJson2() {
       /* lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val message1 = database?.noteDAO()?.getAllNotes() as MutableList<Message>

                withContext(Dispatchers.Main) {


                        IconHeaderItem(
                            3,
                            getString(R.string.media),
                            R.drawable.nlogo11
                        )
                    //val adapter4 = ArrayObjectAdapter(context?.let { MainFragment.MyMediaPresenter(it) })
                    for (i in 0 until message1.size) {
                        // adapter4.add(message1[i])
                        rowsAdapter1?.add(message1[i])
                    }
                }
            }

        }*/


    }

    fun jsonParseMessage() {
        //   val onDemands: MutableList<Message> = ArrayList<Message>()
        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, "put your url ", null, Response.Listener { response: JSONObject ->
            try {
                val `object` = response.getJSONObject("meta")
                var count = `object`.getString("count").toInt()
                for (i in 0..19) {
                    val object2 = response.getJSONObject("result")
                    val object3 = object2.getJSONObject("data")
                    try {
                        val object4 = object3.getJSONObject(count.toString())
                        val sermonTitle = object4.getString("sermonTitle")
                        val date = object4.getString("sermonDate")
                        val sermonLow = object4.getString("sermonLow")
                        val sermonHigh = object4.getString("sermonHigh")
                        val sermonAudio = object4.getString("sermonAudio")
                        val languageId = object4.getInt("languageId")
                        val category = object4.getInt("categoryId")

                        if (languageId == context?.resources?.getString(R.string.languageId)?.toInt()) {
                            //onDemands.add(Message(title = sermonTitle, audio = sermonAudio, video = sermonLow, preacher = context!!.resources?.getString(R.string.kumuyi)!!, duration = " ", image = R.drawable.blog_one))
                            rowsAdapter1!!.add(
                                Message(
                                    title = sermonTitle,
                                    audio = sermonAudio,
                                    video = sermonHigh,
                                    preacher = context!!.resources?.getString(R.string.kumuyi)!!,
                                    category = categories[category - 1].category,
                                    image = R.drawable.blog_one
                                )
                            )
                        }
                    } catch (E: JSONException) {
                        E.printStackTrace()
                    }
                    count--
                    //Log.i(" count", date);
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        },
            Response.ErrorListener {

            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> =
                    HashMap()
                headers[""] =
                    ""
                headers[""] = ""
                return headers
            }
        }
        mQueue!!.add(request)
    }

    fun categoryJson() {

        if (context != null) {
            val request: JsonArrayRequest = object : JsonArrayRequest(
                Method.GET, " ", null, Response.Listener { response: JSONArray ->
                    for (i in 0 until response.length()) {
                        try {
                            val `object` = response.getJSONObject(i)
                            val id = `object`.getInt("categoryId")
                            val category = `object`.getString("categoryName")
                            categories.add(Category(id, category))
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                    jsonParseMessage()
                },
                Response.ErrorListener {

                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers: MutableMap<String, String> =
                        HashMap()
                    headers[" "] =
                        " "
                    headers[" "] = " "
                    return headers
                }
            }
            mQueue!!.add(request)
        }
    }


    fun skipToNext() {
        mPlayerGlue!!.next()
    }

    fun skipToPrevious() {
        mPlayerGlue!!.previous()
    }

    fun rewind() {
        mPlayerGlue!!.rewind()
    }

    fun fastForward() {
        mPlayerGlue!!.fastForward()
    }

    fun reapeat(){
        mPlayerGlue!!.repeat()
    }

    /** Opens the video details page when a related video has been clicked.  */
    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(itemViewHolder: Presenter.ViewHolder, item: Any?, rowViewHolder: RowPresenter.ViewHolder?, row: Row?) {
            if (item is Message) {
                val intent = Intent(activity, DetailActivity::class.java)
                intent.putExtra("message", item)
                intent.putExtra("live", false)
                activity!!.startActivity(intent)
            }
        }
    }

    /** Loads a playlist with videos from a cursor and also updates the related videos cursor.  */


    companion object {
        private const val UPDATE_DELAY = 16
    }
}
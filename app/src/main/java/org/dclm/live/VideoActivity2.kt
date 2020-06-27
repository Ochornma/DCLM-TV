package org.dclm.live

import android.graphics.Color
import android.media.session.MediaSession
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.MediaController.MediaPlayerControl
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.media.session.MediaButtonReceiver
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_video2.*
import kotlinx.android.synthetic.main.exo_player_control_view.*
import org.dclm.live.model.Message
import org.dclm.live.playback.PlaybackFragment
import org.json.JSONException
import java.util.*


class VideoActivity2 : FragmentActivity(), MediaPlayerControl{
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private  var check = false
    var timer: Timer? = null
    var url:String? = null
    private var heading: String? = null
    private var subHeadind: String? = null
  private var mQueue: RequestQueue? = null
    lateinit var mediaSource: MediaSource
    private lateinit var player: SimpleExoPlayer
    private lateinit var link: String
    private var live = false
    private var mVideo: Message? = null

    private var radio = false
    private var mCurrentPlaybackState = PlaybackStateCompat.STATE_NONE
    private lateinit var mediaSession1: MediaSession
  //  private var mMediaSessionCallback: MediaSessionCallback? = null
    private var mPlaybackFragment: PlaybackFragment? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video2)
        link = intent.getStringExtra("link")!!
        url = intent.getStringExtra("api")
        live = intent.getBooleanExtra("live", false)
        heading = intent.getStringExtra("topic")
        subHeadind = intent.getStringExtra("preacher")
        radio = intent.getBooleanExtra("radio", false)
        topic.text = heading
        preacher.text = subHeadind
        playerView.controllerShowTimeoutMs = -1
        playerView.showController()
        playerView.controllerHideOnTouch = false
        playerView.defaultArtwork = ContextCompat.getDrawable(this, R.drawable.radio_banner)
        playerView.useArtwork = true
        exoplayback.setBackgroundColor(Color.BLACK)
        mVideo = intent.getParcelableExtra("message")

 /*       if (!live){
            topic.text = heading
            preacher.text = subHeadind
            if (radio){
                playerView.controllerShowTimeoutMs = -1
                playerView.showController()
                playerView.controllerHideOnTouch = false
                playerView.defaultArtwork = ContextCompat.getDrawable(this, R.drawable.blog_one)
                playerView.useArtwork = true
                exoplayback.setBackgroundColor(Color.BLACK)
            } else{
                exoplayback.setBackgroundColor(Color.parseColor("#56000000"))
                playerView.controllerShowTimeoutMs = 10000
                playerView.showController()
                playerView.controllerHideOnTouch = true
                playerView.defaultArtwork = ContextCompat.getDrawable(this, R.drawable.blog_one)
                playerView.useArtwork = true
            }
        } else{
            playerView.controllerShowTimeoutMs = 10000
            playerView.showController()
            playerView.controllerHideOnTouch = true
            playerView.defaultArtwork = ContextCompat.getDrawable(this, R.drawable.blog_one)
            playerView.useArtwork = true
        }*/
        playerView.focusable = View.FOCUSABLE_AUTO
       // Toast.makeText(this, link, Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()
        check = true
        if (live){
            Picasso.get().load(" ").networkPolicy(
                NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.nlogo).error(R.drawable.nlogo).fit().into(imageView)
        }

    mQueue = Volley.newRequestQueue(applicationContext)
        setRepeatingAsyncTask()

        val audioAttributes = AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.CONTENT_TYPE_SPEECH).build()
        player = SimpleExoPlayer.Builder(this).build()
        player.setAudioAttributes(audioAttributes, true)
        player.addListener(object : Player.EventListener {

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                mCurrentPlaybackState = if (isPlaying){

                    PlaybackStateCompat.STATE_PLAYING
                } else{


                    PlaybackStateCompat.STATE_PAUSED
                }
            }


            override fun onPlayerError(error: ExoPlaybackException) {
               Toast.makeText(this@VideoActivity2, getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
               topic.text = getString(R.string.no_connection)
              preacher.text = getString(R.string.no_connection)
               // Toast.makeText(this@VideoActivity2, "3", Toast.LENGTH_SHORT).show()
            }

        })
        mediaSource = ProgressiveMediaSource.Factory(
            DefaultHttpDataSourceFactory(
                Util.getUserAgent(this, getString(R.string.app_name)),
                null /* listener */,
                30 * 1000,
                30 * 1000,
                true )

        ).createMediaSource(Uri.parse(link))
        player.prepare(mediaSource)
        player.playWhenReady = true
       //playerView.setControlDispatcher(mPlaybackController)

        mediaSession = MediaSessionCompat(applicationContext, "dclmTv")
        mediaSessionConnector = MediaSessionConnector(mediaSession)
      //  mediaSessionConnector.setControlDispatcher(mPlaybackController)
        mediaSessionConnector.setPlayer(player)

        MediaButtonReceiver.handleIntent(mediaSession, intent)
        playerView.player = player
        playerView.setShutterBackgroundColor(Color.TRANSPARENT)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    //    mediaSession.setCallback(mMediaSessionCallback)
        mediaSession.setFlags(
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        )
        mediaSession.isActive = true
       // mediaController = MediaController(this, mediaSession.sessionToken)
        //createMediaSession()


    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BUTTON_R1) {
         //   mPlaybackFragment!!.skipToNext()
            player.playWhenReady = !player.isPlaying
            return true
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_L1) {
          //  mPlaybackFragment!!.skipToPrevious()
            player.seekTo(0)
            return true
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_L2) {

            player.seekTo(player.currentPosition - 10000)
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_R2) {

            player.seekTo(player.currentPosition + 10000)
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onStop() {
       // super.onStop()
        if (check) {
            mediaSession.release()
            mediaSessionConnector.setPlayer(null)
            player.release()
        }
        timer?.cancel()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


        super.onStop()
    }

    private fun setRepeatingAsyncTask() {
        if (live){
            val handler = Handler()
            timer = Timer()

            val task: TimerTask = object : TimerTask() {
                override fun run() {
                    handler.post { jsonParse() }
                }
            }

            timer?.schedule(task, 0, 60 * 1000.toLong()) // interval of one minute
        } else{
            topic.text = heading
            preacher.text = subHeadind
        }
    }

    private fun jsonParse() {

        val request =
            JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { response ->
                    try {
                        val nowPlaying = response.getJSONObject("now_playing")
                        val song = nowPlaying.getJSONObject("song")
                        val minister = song.getString("artist")
                        val topic1 = song.getString("title")
                        val listeners1 = response.getJSONObject("listeners")
                        val number = listeners1.getString("total")
                        val listining: String =
                            Objects.requireNonNull(applicationContext).resources
                                .getString(R.string.listning)
                        topic.text =topic1
                        preacher.text = minister
                         //topic.text = topic1
                        //preacher.text = minister
                        //listeners.text = StringBuilder().append(listining).append(" ").append(number).toString()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { error ->
                    error.printStackTrace()
                    topic.text = " "
                  preacher.text = " "
                    //preacher.text = applicationContext.resources.getString(R.string.message)
                    //topic.text = applicationContext.resources.getString(R.string.ministering)
                    // listeners.text = " "
                })
        mQueue!!.add(request)

    }


    private fun updatePlaybackState() {
        val stateBuilder = PlaybackStateCompat.Builder()
            .setActions(getAvailableActions())
        var state: Int = PlaybackStateCompat.STATE_PLAYING
        if (mCurrentPlaybackState == PlaybackStateCompat.STATE_PAUSED || mCurrentPlaybackState == PlaybackStateCompat.STATE_NONE) {
            state = PlaybackStateCompat.STATE_PAUSED
        }
        stateBuilder.setState(state, currentPosition.toLong(), 1.0f)
        mediaSession.setPlaybackState(stateBuilder.build())
    }

    private fun getAvailableActions(): Long {
        return PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_PLAY_PAUSE or
                PlaybackStateCompat.ACTION_REWIND or
                PlaybackStateCompat.ACTION_FAST_FORWARD or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
    }




    override fun isPlaying(): Boolean {
        return player.isPlaying
    }

    override fun canSeekForward(): Boolean {
            return true
    }

    override fun getDuration(): Int {
       return player.duration.toInt()
    }

    override fun pause() {
        if (player.isPlaying){
            player.playWhenReady = false
        }
    }

    override fun getBufferPercentage(): Int {
        return player.bufferedPercentage
    }

    override fun seekTo(pos: Int) {
        player.seekTo(pos.toLong())
        updatePlaybackState()
    }

    override fun getCurrentPosition(): Int {
        return player.currentPosition.toInt()
    }

    override fun canSeekBackward(): Boolean {
       return true
    }

    override fun start() {
        if (!player.isPlaying ){
            player.playWhenReady = true
        }
    }

    override fun getAudioSessionId(): Int {
        return player.audioSessionId
    }

    override fun canPause(): Boolean {
        return true
    }


}



package org.dclm.live

import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.view.KeyEvent
import androidx.fragment.app.FragmentActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.exoplayer2.util.Util
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_radio.*
import org.json.JSONException
import java.util.*

class RadioActivity : FragmentActivity() {
    var timer: Timer? = null
    var url:String? = null
    private var mQueue: RequestQueue? = null
    private var bound = false
    private var dclmRadioService: DCLMRadioService? = null
    private var stateOfPlay = false
    private lateinit var preferences: SharedPreferences
    val PREFRENCES = "org.dclm.radio"
    private lateinit var editor: SharedPreferences.Editor
    val LINK = "LINK"
    private var link:String? = null
    private var live = false
    private var title = " "
    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as DCLMRadioService.RadioLocalBinder
            dclmRadioService = binder.getService()
            getState()
            bound = true
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_radio)
        url = intent.getStringExtra("api")
        link = intent.getStringExtra("link")!!
        live = intent.getBooleanExtra("live", false)
        title = intent.getStringExtra("topic")!!
        mQueue = Volley.newRequestQueue(this)
        preferences = applicationContext.getSharedPreferences(PREFRENCES, Context.MODE_PRIVATE)
        editor = preferences.edit()
        editor.putString(LINK, link)
        editor.apply()

        play.setOnClickListener {
            if (stateOfPlay){
                dclmRadioService?.pausePlayer()
                play.setImageResource(R.drawable.ic_baseline_play_circle_outline_24)
            } else{
                val intent = Intent(this, DCLMRadioService::class.java)
                Util.startForegroundService(this, intent)
                dclmRadioService?.player?.playWhenReady = true
                play.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24)
                stateOfPlay
            }
            stateOfPlay = !stateOfPlay
        }

     /*   stop.setOnClickListener {
            Toast.makeText(this, "rrrrr", Toast.LENGTH_SHORT).show()
            stopService(Intent(this, DCLMRadioService::class.java))
           // super.onDestroy()
        }*/
        Util.startForegroundService(this, Intent(this, DCLMRadioService::class.java))
        dclmRadioService?.player?.playWhenReady = true

    }

    override fun onStart() {
        super.onStart()
        // link = getActivity().getResources().getString(R.string.radio_link);
        mQueue = Volley.newRequestQueue(applicationContext)
        if (live){
            setRepeatingAsyncTask()

            Picasso.get().load(" put yours").resize(520, 520).networkPolicy(
                NetworkPolicy.NO_CACHE
            ).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.nlogo).error(R.drawable.nlogo)
                .into(image_view)
        } else{
            topic.text = title
            preacher.text = getString(R.string.kumuyi)
            listeners.text = " "
        }

        val intent = Intent(this, DCLMRadioService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun getState(){

        dclmRadioService?.playState?.observe(this, androidx.lifecycle.Observer {
            stateOfPlay = if (it){
                play.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24)
                true
            } else{
                play.setImageResource(R.drawable.ic_baseline_play_circle_outline_24)
                false
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BUTTON_R1) {
            //   mPlaybackFragment!!.skipToNext()
            dclmRadioService?.player?.playWhenReady = true
           // player.playWhenReady = true
            return true
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_L1) {
            //  mPlaybackFragment!!.skipToPrevious()
           // player.playWhenReady = false
            dclmRadioService?.player?.playWhenReady = false
            return true
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_L2) {
            dclmRadioService?.player?.playWhenReady = false
          //  player.seekTo(player.currentPosition - 10000)
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_R2) {
            dclmRadioService?.player?.playWhenReady = true
            //player.seekTo(player.currentPosition + 10000)
        }
        return super.onKeyDown(keyCode, event)
    }
    private fun setRepeatingAsyncTask() {
        val handler = Handler()
        timer = Timer()

        val task: TimerTask = object : TimerTask() {
            override fun run() {
                handler.post { jsonParse() }
            }
        }

        timer?.schedule(task, 0, 60 * 1000.toLong()) // interval of one minute

    }

    override fun onStop() {
        super.onStop()
        timer?.cancel()
        bound = false
        unbindService(connection)
    }

    override fun onResume() {
        super.onResume()
        //  registry.currentState = Lifecycle.State.RESUMED
        if (dclmRadioService != null){
            if (dclmRadioService?.buttonState!!) {
                val handler1 = Handler()
                handler1.post {   play.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24) }
            } else {
                val handler2 = Handler()
                handler2.post { play.setImageResource(R.drawable.ic_baseline_play_circle_outline_24) }
            }
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
                           topic.text = topic1
                            preacher.text = minister
                            listeners.text = StringBuilder().append(listining).append(" ").append(number).toString()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }, Response.ErrorListener { error ->
                        error.printStackTrace()

                        preacher.text = applicationContext.resources.getString(R.string.message)
                        topic.text = applicationContext.resources.getString(R.string.ministering)
                       listeners.text = " "
                    })
            mQueue!!.add(request)

    }
    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, DCLMRadioService::class.java))
    }
}
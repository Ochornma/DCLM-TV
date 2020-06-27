package org.dclm.live.playback

import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_playback.*
import org.dclm.live.R


class PlaybackActivity : FragmentActivity() {
    private val GAMEPAD_TRIGGER_INTENSITY_ON = 0.5f

    // Off-condition slightly smaller for button debouncing.
    private val GAMEPAD_TRIGGER_INTENSITY_OFF = 0.45f
    private var gamepadTriggerPressed = false
    private var mPlaybackFragment: PlaybackFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)
        //texture_view.background = ContextCompat.getDrawable(this, R.drawable.radio_banner)
       /* val fragment: Fragment? = supportFragmentManager.findFragmentByTag("promise")
        if (fragment is PlaybackFragment) {
            mPlaybackFragment = fragment
        }
        playerView.player = mPlaybackFragment?.mPlayer*/
        playerView.useArtwork = true
        playerView.defaultArtwork = ContextCompat.getDrawable(this, R.drawable.radio_banner)
    }

    override fun onStop() {
        super.onStop()

        finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BUTTON_R1) {
            mPlaybackFragment!!.skipToNext()
            return true
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_L1) {
            mPlaybackFragment!!.skipToPrevious()
            return true
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_L2) {
            mPlaybackFragment!!.rewind()
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_R2) {
            mPlaybackFragment!!.fastForward()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        // This method will handle gamepad events.
        if (event.getAxisValue(MotionEvent.AXIS_LTRIGGER) > GAMEPAD_TRIGGER_INTENSITY_ON
            && !gamepadTriggerPressed
        ) {
            mPlaybackFragment!!.rewind()
            gamepadTriggerPressed = true
        } else if (event.getAxisValue(MotionEvent.AXIS_RTRIGGER) > GAMEPAD_TRIGGER_INTENSITY_ON
            && !gamepadTriggerPressed
        ) {
            mPlaybackFragment!!.fastForward()
            gamepadTriggerPressed = true
        } else if (event.getAxisValue(MotionEvent.AXIS_LTRIGGER) < GAMEPAD_TRIGGER_INTENSITY_OFF
            && event.getAxisValue(MotionEvent.AXIS_RTRIGGER) < GAMEPAD_TRIGGER_INTENSITY_OFF
        ) {
            gamepadTriggerPressed = false
        }
        return super.onGenericMotionEvent(event)
    }
}
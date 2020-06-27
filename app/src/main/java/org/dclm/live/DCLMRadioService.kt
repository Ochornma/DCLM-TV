package org.dclm.live

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.widget.MediaController
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.media.session.MediaButtonReceiver
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util

class DCLMRadioService : Service(), MediaController.MediaPlayerControl {
    private val binder = RadioLocalBinder()

    private lateinit var playerNotificationManager: PlayerNotificationManager
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSession1: MediaSession
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var context: Context
    private var check = false
    lateinit var mediaSource: MediaSource
    private lateinit var preferences: SharedPreferences
    val PREFRENCES = "org.dclm.radio"
    val destination = "destination"
    val title = "title"
    val describe = "describe"
    val LINK = "LINK"
    val TYPE = "TYPE"
    val IMAGE = "image"
    var position = 0
    var currentPosition: Long = 0
    var destroyed = false
    lateinit var player: SimpleExoPlayer
    val playState: MutableLiveData<Boolean> = MutableLiveData()
    var buttonState = false


    inner class RadioLocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods

        fun getService(): DCLMRadioService {
            return this@DCLMRadioService
        }

        fun getPlayer(): ExoPlayer? {
            // Return this instance of LocalService so clients can call public methods
            return player
        }

    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }


    override fun onCreate() {
        super.onCreate()
        destroyed = false
        context = this


        preferences = applicationContext.getSharedPreferences(PREFRENCES, Context.MODE_PRIVATE)
        val audioAttributes =
            AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.CONTENT_TYPE_SPEECH)
                .build()

        player = SimpleExoPlayer.Builder(this).build()


        player.setAudioAttributes(audioAttributes, true)
        player.addListener(object : Player.EventListener {

            @RequiresApi(api = Build.VERSION_CODES.N)
            override fun onPlayerStateChanged(
                playWhenReady: Boolean, playbackState: Int
            ) {
                buttonState = if (!playWhenReady) {
                    stopForeground(false)
                    playState.postValue(false)
                    false

                } else {
                    startForeground()

                    playState.postValue(true)
                    true
                }
            }

            override fun onPlayerError(error: ExoPlaybackException) {
                Toast.makeText(context, getString(R.string.no_connection), Toast.LENGTH_LONG).show()
                playState.postValue(false)
                buttonState = false
            }

            override fun onPositionDiscontinuity(reason: Int) {
                val latestWindowIndex = player.currentWindowIndex
                if (latestWindowIndex != position) {
                    // item selected in playlist has changed, handle here
                    position = latestWindowIndex
                }
            }
        })
        /* mediaSession1 = MediaSession(context, "MusicService").apply {
             setCallback(object : MediaSession.Callback() {
                 override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
                     return super.onMediaButtonEvent(mediaButtonIntent)
                 }

                 override fun onRewind() {
                     super.onRewind()
                 }

                 override fun onSeekTo(pos: Long) {
                     super.onSeekTo(pos)
                     player.seekTo(pos)
                 }

                 override fun onPause() {
                     player.playWhenReady = false
                 }

                 override fun onSkipToPrevious() {
                     super.onSkipToPrevious()
                 }

                 override fun onFastForward() {

                 }

                 override fun onPlay() {
                     player.playWhenReady = true
                 }

                 override fun onStop() {
                     stopSelf()
                 }

                 override fun onSkipToNext() {
                     super.onSkipToNext()
                 }

                 override fun onSetPlaybackSpeed(speed: Float) {
                     super.onSetPlaybackSpeed(speed)
                 }
             })
             setFlags(
                 MediaSession.FLAG_HANDLES_MEDIA_BUTTONS or MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS
             )
         }
       */

        // A small bitmap for the artwork is also recommended
        //putBitmap(MediaMetadata.METADATA_KEY_ART, myData.artBitmap)
        // Add any other fields you have for your data as well

        // mediaSession1.isActive = true
        val metadataBuilder = MediaMetadataCompat.Builder().apply {
            // To provide most control over how an item is displayed set the
            // display fields in the metadata
            putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, getString(R.string.app_name))
            putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, getString(R.string.kumuyi))
            // putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI, R.drawable.blog_one)
            // And at minimum the title and artist for legacy support
            putString(MediaMetadata.METADATA_KEY_TITLE, getString(R.string.app_name))
            putString(MediaMetadata.METADATA_KEY_ARTIST, getString(R.string.app_name))
            putBitmap(MediaMetadata.METADATA_KEY_ART, getBitmap(context, R.drawable.nlogo11))
        }.build()

        mediaSession = MediaSessionCompat(context, "dclm")
        mediaSession.isActive = true
        mediaSession.setMetadata(metadataBuilder)
        mediaSession.setCallback(object : MediaSessionCompat.Callback() {
            override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
                return super.onMediaButtonEvent(mediaButtonIntent)
            }

            override fun onRewind() {
                super.onRewind()
                //player.seekTo(player.currentPosition - 10)
            }

            override fun onSeekTo(pos: Long) {
                super.onSeekTo(pos)
               // player.seekTo(pos)
            }

            override fun onPause() {
                player.playWhenReady = false
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
            }

            override fun onFastForward() {

            }

            override fun onPlay() {
                player.playWhenReady = true
            }

            override fun onStop() {
                stopSelf()
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
            }

        })

        mediaSession.setMetadata(metadataBuilder)
        val pi: PendingIntent = Intent(context, RadioActivity::class.java).let { intent ->
            PendingIntent.getActivity(
                context, 99 /*request code*/,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        mediaSession.setSessionActivity(pi)
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        startForeground()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        check = true
        val type = preferences.getBoolean(TYPE, false)
        mediaSource = ProgressiveMediaSource.Factory(
            (if (type) {
                RtmpDataSourceFactory()

            } else {
                DefaultHttpDataSourceFactory(
                    Util.getUserAgent(context, getString(R.string.app_name)),
                    null /* listener */,
                    30 * 1000,
                    30 * 1000,
                    true
                ) //allowCrossProtocolRedirects
            })


        ).createMediaSource(Uri.parse(preferences.getString(LINK, "")))
        startForeground()
        player.prepare(mediaSource)
        player.seekTo(position, currentPosition)
        player.playWhenReady = true
        /* startForeground(preferences.getInt(destination, R.id.homeFragment),
             preferences.getString(title, getString(R.string.app_name))!!,
             preferences.getString(describe, getString(R.string.app_describe))!!, preferences.getInt(IMAGE, R.drawable.nlogo))*/
        MediaButtonReceiver.handleIntent(mediaSession, intent)

        return START_STICKY
    }

    private fun startForeground() {

        playerNotificationManager =
            PlayerNotificationManager.createWithNotificationChannel(context, "dclm_radio",
                R.string.app_name,
                R.string.app_describe,
                1,
                object : PlayerNotificationManager.MediaDescriptionAdapter {
                    override fun getCurrentContentTitle(player: Player): String {
                        return getString(R.string.app_name)
                    }

                    override fun createCurrentContentIntent(player: Player): PendingIntent? {
                        val intent = Intent(context, RadioActivity::class.java)
                        return PendingIntent.getActivity(
                            context,
                            1,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    }

                    override fun getCurrentContentText(player: Player): String? {
                        return context.getString(R.string.app_describe)
                    }

                    override fun getCurrentLargeIcon(
                        player: Player,
                        callback: PlayerNotificationManager.BitmapCallback
                    ): Bitmap? {
                        return getBitmap(context, R.drawable.blog_one)
                    }
                },
                object : PlayerNotificationManager.NotificationListener {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    override fun onNotificationCancelled(
                        notificationId: Int,
                        dismissedByUser: Boolean
                    ) {
                        if (dismissedByUser) {
                            stopSelf()
                        }
                        stopSelf()
                        // stopForeground(STOP_FOREGROUND_REMOVE);
                    }

                    override fun onNotificationPosted(
                        notificationId: Int,
                        notification: Notification,
                        ongoing: Boolean
                    ) {
                        if (ongoing) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startForeground(notificationId, notification)
                            }
                        } else {
                            stopForeground(false)
                        }
                    }
                })

        playerNotificationManager.setSmallIcon(R.drawable.nlogo1)
        playerNotificationManager.setUseStopAction(false)
        playerNotificationManager.setFastForwardIncrementMs(2)
        playerNotificationManager.setRewindIncrementMs(2)
        playerNotificationManager.setUseNavigationActions(true)
        playerNotificationManager.setPlayer(player)
        mediaSession.isActive = true
        //  mediaSession1.isActive = true
        playerNotificationManager.setMediaSessionToken(mediaSession.sessionToken)
        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlayer(player)
    }

    fun getBitmap(context: Context, @DrawableRes bitmapResource: Int): Bitmap? {
        //
        return (context.resources.getDrawable(bitmapResource, null) as BitmapDrawable).bitmap
    }

    override fun onDestroy() {
        destroyed = true
        position = player.currentWindowIndex
        currentPosition = player.contentPosition
        if (check) {
            mediaSession.release()
            mediaSessionConnector.setPlayer(null)
            playerNotificationManager.setPlayer(null)
            player.release()
        }

        super.onDestroy()
    }

    override fun isPlaying(): Boolean {
        return player.isPlaying
    }

    override fun canSeekForward(): Boolean {
        return false
    }

    override fun getDuration(): Int {
        return player.duration.toInt()
    }

    override fun pause() {
        pausePlayer()
    }

    override fun getBufferPercentage(): Int {
        return this.player.bufferedPercentage
    }

    override fun seekTo(pos: Int) {
        player.seekTo(pos.toLong())
    }

    override fun getCurrentPosition(): Int {
        return this.player.currentPosition.toInt()
    }

    override fun canSeekBackward(): Boolean {
        return false
    }

    override fun start() {
        startPlayer()
    }

    override fun getAudioSessionId(): Int {
        return this.player.audioSessionId
    }

    override fun canPause(): Boolean {
        return true
    }

    fun pausePlayer() {
        if (player.playWhenReady == player.isPlaying) {
            player.playWhenReady = false
        }

    }

    fun startPlayer() {
        if (player.playWhenReady != player.isPlaying) {
            player.playWhenReady = true
        }
    }


}
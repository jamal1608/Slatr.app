package com.tonespace.app.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompatCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.session.MediaButtonReceiver
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import androidx.media.MediaBrowserServiceCompat
import com.tonespace.app.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MediaPlaybackService : MediaBrowserServiceCompat() {

    private var mediaSession: MediaSessionCompat? = null
    private var player: ExoPlayer? = null
    private val binder = LocalBinder()

    inner class LocalBinder : android.os.Binder() {
        fun getService(): MediaPlaybackService = this@MediaPlaybackService
    }

    override fun onCreate() {
        super.onCreate()
        initNotificationChannel()
        player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSessionCompat(this, "ToneSpace").apply {
            isActive = true
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() { player?.play() }
                override fun onPause() { player?.pause() }
                override fun onSeekTo(pos: Long) { player?.seekTo(pos) }
                override fun onStop() {
                    player?.stop()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            })
        }

        val mediaSessionConnector = MediaSessionConnector(mediaSession!!)
        mediaSessionConnector.setPlayer(player)

        player?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updatePlaybackState(isPlaying)
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    updatePlaybackState(player?.isPlaying == true)
                }
            }
        })
    }

    fun playAudio(url: String, title: String, artist: String) {
        val mediaItem = MediaItem.fromUri(url)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
        showNotification(title, artist)
    }

    fun pause() { player?.pause() }
    fun resume() { player?.play() }
    fun seekTo(position: Long) { player?.seekTo(position) }
    fun stop() { player?.stop(); stopForeground(STOP_FOREGROUND_REMOVE) }
    fun isPlaying(): Boolean = player?.isPlaying == true
    fun currentPosition(): Long = player?.currentPosition ?: 0L
    fun duration(): Long = player?.duration?.takeIf { it > 0 } ?: 0L

    private fun updatePlaybackState(isPlaying: Boolean) {
        val state = if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
        val playbackState = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_SEEK_TO or
                PlaybackStateCompat.ACTION_STOP
            )
            .setState(state, player?.currentPosition ?: 0L, 1f)
            .build()
        mediaSession?.setPlaybackState(playbackState)
    }

    private fun initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "tonespace_playback",
                "Audio Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, artist: String) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )
        val notification = Notification.Builder(this, "tonespace_playback")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(title)
            .setContentText(artist)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
        startForeground(1, notification)
    }

    override fun onGetSession(controllerInfo: MediaSessionCompat.ControllerInfo): MediaSessionCompat? {
        return mediaSession
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompatCompat.MediaItem>>
    ) {
        result.sendResult(mutableListOf())
    }

    override fun onBind(intent: Intent?): android.os.IBinder? {
        super.onBind(intent)
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        mediaSession?.release()
    }
}
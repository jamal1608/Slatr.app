package com.tonespace.app.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.tonespace.app.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MediaPlaybackService : Service() {

    private var player: ExoPlayer? = null
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): MediaPlaybackService = this@MediaPlaybackService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        initNotificationChannel()
        player = ExoPlayer.Builder(this).build()
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
    fun stopPlayback() { player?.stop(); stopForeground(STOP_FOREGROUND_REMOVE); stopSelf() }
    fun isPlaying(): Boolean = player?.isPlaying == true
    fun currentPosition(): Long = player?.currentPosition ?: 0L
    fun duration(): Long = player?.duration?.takeIf { it > 0 } ?: 0L
    fun addListener(listener: com.google.android.exoplayer2.Player.Listener) { player?.addListener(listener) }

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

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}
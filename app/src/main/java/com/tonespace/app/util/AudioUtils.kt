package com.tonespace.app.util

import android.content.ContentValues
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File

object AudioUtils {

    fun setAsRingtone(context: Context, filePath: String, title: String): Boolean {
        return setRingtone(context, filePath, title, RingtoneManager.TYPE_RINGTONE)
    }

    fun setAsNotification(context: Context, filePath: String, title: String): Boolean {
        return setRingtone(context, filePath, title, RingtoneManager.TYPE_NOTIFICATION)
    }

    fun setAsAlarm(context: Context, filePath: String, title: String): Boolean {
        return setRingtone(context, filePath, title, RingtoneManager.TYPE_ALARM)
    }

    private fun setRingtone(context: Context, filePath: String, title: String, type: Int): Boolean {
        return try {
            val file = File(filePath)
            if (!file.exists()) return false

            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DATA, file.absolutePath)
                put(MediaStore.MediaColumns.TITLE, title)
                put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg")
                put(MediaStore.MediaColumns.SIZE, file.length())
            }

            val contentUri = when (type) {
                RingtoneManager.TYPE_RINGTONE -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                RingtoneManager.TYPE_NOTIFICATION -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                else -> RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            }

            val uri = context.contentResolver.insert(contentUri, values)
            uri?.let {
                RingtoneManager.setActualDefaultRingtoneUri(context, type, it)
                true
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun formatDuration(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return "%d:%02d".format(mins, secs)
    }

    fun getAudioFilePath(context: Context, fileName: String): String {
        val dir = File(context.filesDir, "audio")
        if (!dir.exists()) dir.mkdirs()
        return File(dir, fileName).absolutePath
    }
}
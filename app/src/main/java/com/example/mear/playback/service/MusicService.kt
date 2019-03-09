package com.example.mear.playback.service

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.R
import android.R.string.cancel
import android.widget.Toast


import com.example.mear.models.Track
import com.example.mear.management.MusicFiles
import com.example.mear.management.TrackManager
import com.example.mear.repositories.TrackRepository


class MusicService: Service() {

    private var trackPlayer: MediaPlayer? = null
    private val mBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        internal val service: MusicService
            get() = this@MusicService
    }

    override fun onCreate() {
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {

        Toast.makeText(this, "Music Service Stopped", Toast.LENGTH_SHORT).show()
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }
}

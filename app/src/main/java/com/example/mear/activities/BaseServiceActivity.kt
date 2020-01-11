package com.example.mear.activities

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Environment
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.example.mear.R

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import com.example.mear.playback.service.MusicService

open class BaseServiceActivity: AppCompatActivity() {
    protected var musicService: MusicService? = null


    protected open fun resetControls() {
        val d = "l"
    }
    protected open fun initializeChangeListeners() {
        val d = "l"
    }
    protected open fun updateTrackProgress() {
        val d = "l"
    }
    protected open fun configureTrackDisplay() {
        val d = "l"
    }


    protected fun appDirectory(): String {
        return Environment.getDataDirectory().toString() + "/data/" +
                resources.getString(R.string.app_relative_path)
    }


    protected fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
    protected val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            resetControls()

            runBlocking {
                val demo = launch {
                    musicService = (service as MusicService.LocalBinder).service
                    initializeChangeListeners()
                    updateTrackProgress()
                }
                demo.start()
            }
            if (musicService != null) {
                if (musicService!!.isPlaying()) {
                    configureTrackDisplay()
                }
            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            resetControls()
            Toast.makeText(
                this@BaseServiceActivity, "Music Service Stopped",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    protected fun doBindService() {
        val intent = Intent(this, MusicService::class.java)
        if (isServiceRunning(MusicService::class.java)) {
            val suc = "Service is already running"
        }
        else {
            intent.putExtra("appPath", appDirectory())
            startService(intent)
        }

        val result = bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
    }
}
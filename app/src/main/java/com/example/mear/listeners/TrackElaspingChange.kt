package com.example.mear.listeners

import android.os.Handler
import android.widget.SeekBar

import java.lang.Exception
import java.util.concurrent.TimeUnit

import com.example.mear.playback.service.MusicService
import com.example.mear.util.ConvertTrackPosition


class TrackElaspingChange(var seekBar: SeekBar?): SeekBar.OnSeekBarChangeListener {

    var musicService: MusicService? = null
    var mHandler: Handler? = Handler()

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        this.seekBar = seekBar
    }
    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        this.seekBar = seekBar
        val newPositionPercentage = this.seekBar!!.progress
        var convertPosition =  ConvertTrackPosition(newPositionPercentage)
        val newPosition = convertPosition.newPosition(musicService!!.durationOfTrack())
        musicService!!.goToPosition(newPosition)
        this.seekBar!!.progress = newPosition
    }
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        this.seekBar = seekBar

        if (fromUser ){

            mHandler!!.postDelayed( musicTrackTimeUpdateTask, 250)
        }
    }

    fun initialize() {
        mHandler!!.postDelayed(musicTrackTimeUpdateTask, 250)
    }

    private var musicTrackTimeUpdateTask = object: Runnable {
        override fun run() {
            try {
                var newPosition = 0
                val currentPosition = musicService!!.currentPositionOfTrack() / 1000
                val totalDuration = musicService!!.durationOfTrack() / 1000
                newPosition = (((currentPosition).toDouble() / totalDuration) * 100).toInt()
                val dur = String.format(
                    "%02d:%02d", TimeUnit.SECONDS.toMinutes(currentPosition.toLong()),
                    (currentPosition % 60)
                )

                seekBar!!.progress = newPosition

                mHandler!!.postDelayed(this, 250)
            }
            catch (ex: Exception) {
                val exMsg = ex.message
            }
        }
    }
}
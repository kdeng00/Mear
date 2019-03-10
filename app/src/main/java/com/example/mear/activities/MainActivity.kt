package com.example.mear.activities

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

import java.lang.Exception
import java.lang.Runnable
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.io.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_play_controls.*
import kotlinx.android.synthetic.main.fragment_track_cover.*
import kotlinx.android.synthetic.main.fragment_track_details.*
import kotlinx.android.synthetic.main.fragment_track_elapsing.*
import kotlinx.android.synthetic.main.fragment_track_flow.*

import org.jetbrains.anko.image
import org.jetbrains.anko.imageBitmap

import com.example.mear.listeners.TrackElaspingChange
import com.example.mear.playback.service.MusicService
import com.example.mear.models.PlayControls
import com.example.mear.R


class MainActivity : AppCompatActivity() {

    private var musicService: MusicService? = null
    private var serviceBinded: Boolean? = false
    private var repeatOn: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        initialize()
    }
    override fun onStop() {
        super.onStop()
        try {
            if (serviceBinded == null || serviceBinded!! == false) {
                unbindService(mConnection)
                serviceBinded = true
            }
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (serviceBinded!!.equals(null) || serviceBinded!! == false  ) {
                unbindService(mConnection)
                serviceBinded = true
            }
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }


    private fun initialize() {
        TrackElapsing.progress = 0
        TrackElapsing.max = 100

        try {
            initializeServices()
            initializeClickListeners()
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }
    private fun initializeChangeListeners() {
        val newListener = TrackElaspingChange(TrackElapsing)
        if (musicService != null) {
            newListener.musicService = musicService
            newListener.initialize()
            TrackElapsing.setOnSeekBarChangeListener(newListener)
        }
    }
    private fun initializeClickListeners() {
        PlayTrack.setOnClickListener {
            playSongTrack()
        }
        NextTrack.setOnClickListener {
            playNextSongTrack()
        }
        PreviousTrack.setOnClickListener {
            playPreviousSongTrack()
        }
        ShuffleTracks.setOnClickListener {
            toggleShuffle()
        }
        RepeatTrack.setOnClickListener {
            toggleRepeat()
        }
        SettingsLink.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            try {
                startActivity(intent)
            }
            catch (ex: Exception) {
                val exMsg = ex.message
                println(exMsg)
            }
        }
    }
    private fun initializeServices() {
        doBindService()
    }

    private fun toggleShuffle() {
        val shuffleText = ShuffleTracks.text.toString()
        val on = getString(R.string.shuffle_on)
        val off = getString(R.string.shuffle_off)
        when (shuffleText) {
            on -> {
                shuffleOn = false
                ShuffleTracks.setText(R.string.shuffle_off)
            }
            off -> {
                shuffleOn = true
                ShuffleTracks.setText(R.string.shuffle_on)
            }
            else -> {
                shuffleOn = false
                ShuffleTracks.setText(R.string.shuffle_off)
            }
        }
    }
    private fun toggleRepeat() {
        val repeatText = RepeatTrack.text.toString()
        val on = resources.getString(R.string.repeat_on)
        val off = resources.getString(R.string.repeat_off)
        when (repeatText) {
            on -> {
                repeatOn = false
                RepeatTrack.text = off
            }
            off -> {
                repeatOn = true
                RepeatTrack.text = on
            }
            else -> {
                repeatOn = false
                RepeatTrack.text = off
            }
        }
    }

    private fun playSongTrack() {
        PlayTrack.isEnabled = false
        try {
            if (!musicService!!.isPlaying()) {
                musicService!!.playSongTrack()
            } else {
                musicService!!.pauseSongTrack()
            }
            configureTrackDisplay()
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
        PlayTrack.isEnabled = true
    }
    private fun playNextSongTrack() {
        NextTrack.isEnabled = false
        try {
            val controls = PlayControls(shuffleOn!!, repeatOn!!)
            musicService!!.playNextTrack(controls)

            configureTrackDisplay()
        }
        catch (ex: Exception) {
            val exMsg = ex.message
            print(exMsg)
        }
        NextTrack.isEnabled = true
    }
    private fun playPreviousSongTrack() {
        PreviousTrack.isEnabled = false
        try {
            val controls = PlayControls(shuffleOn!!, repeatOn!!)
            musicService!!.playPreviousTrack(controls)

            configureTrackDisplay()
        }
        catch (ex: Exception) {
            val exMsg = ex.message
            print(exMsg)
        }
        PreviousTrack.isEnabled = true
    }
    private fun configureTrackDisplay() {
        try {
            runOnUiThread {
                configurePlayControlsDisplay()
                val currTrack = musicService!!.getCurrentTrack()
                val trackTitle = currTrack.title
                val artistTitle = currTrack.artist
                val albumTitle = currTrack.album
                val trackDuration = currTrack.length
                var trackCover: ByteArray? = null
                val dur = String.format(
                    "%02d:%02d", TimeUnit.SECONDS.toMinutes(trackDuration.toLong()),
                    (trackDuration % 60)
                )

                val mmr = MediaMetadataRetriever()
                mmr.setDataSource(currTrack.songPath)

                if (mmr.embeddedPicture != null) {
                    trackCover = mmr.embeddedPicture
                }
                updateTrackProgress()

                resetControls()


                TrackTitle.text = trackTitle
                ArtistTitle.text = artistTitle
                AlbumTitle.text = albumTitle
                TrackDuration.text = dur
                if (trackCover != null) {
                    val songImage = BitmapFactory
                        .decodeByteArray(trackCover, 0, trackCover.size)
                    TrackCover.imageBitmap = songImage
                }
            }
        }
        catch (ex: Exception) {
            val msg = ex.message
        }
    }
    private fun resetControls() {
        TrackTitle!!.text = null
        ArtistTitle!!.text = null
        AlbumTitle!!.text = null
        CurrentPosition!!.text = null
        TrackCover!!.imageBitmap = null
    }
    private fun updateTrackProgress() {
        musicHandler!!.postDelayed(musicTrackTimeUpdateTask, 250)
    }
    private fun configurePlayControlsDisplay() {
        PlayTrack.background = null
        PlayTrack.colorFilter = null

        if (!musicService!!.isPlaying()) {
            PlayTrack.setImageResource(android.R.drawable.ic_media_pause)
            PlayTrack.setColorFilter(Color.RED)
        }
        else {
            PlayTrack.setImageResource(android.R.drawable.ic_media_play)
            PlayTrack.setColorFilter(Color.GREEN)
        }
    }


    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private var musicTrackTimeUpdateTask = object: Runnable {
       override fun run() {
           try {
               val currentPosition = musicService!!.currentPositionOfTrack() / 1000
               val dur = String.format( "%02d:%02d",
                                                                TimeUnit.SECONDS.toMinutes(currentPosition.toLong()),
                                                                (currentPosition % 60) )

               CurrentPosition.text = dur

               if (TrackCover.image  == null && musicService!!.isPlaying()) {
                   configureTrackDisplay()
               }

               musicHandler!!.postDelayed(this, 250)
           }
           catch (ex: Exception) {
                   val exMsg = ex.message
           }
       }
    }

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            resetControls()

            runBlocking {
                val demo = launch {
                    musicService = (service as MusicService.LocalBinder).service
                    initializeChangeListeners()
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
                this@MainActivity, "Music Service Stopped",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    fun doBindService() {
        val intent = Intent(this, MusicService::class.java)
        if (isServiceRunning(MusicService::class.java)) {
            val suc = "Service is already running"
        }
        else {
            startService(intent)
        }

        var result = bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        if (result) {
        }
        else {
        }
    }



    private var musicHandler: Handler? = Handler()
    private var shuffleOn: Boolean? = false
}

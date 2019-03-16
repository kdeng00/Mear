package com.example.mear.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler

import java.lang.Exception
import java.lang.Runnable
import java.util.concurrent.TimeUnit
import kotlin.io.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_play_controls.*
import kotlinx.android.synthetic.main.fragment_track_cover.*
import kotlinx.android.synthetic.main.fragment_track_details.*
import kotlinx.android.synthetic.main.fragment_track_elapsing.*
import kotlinx.android.synthetic.main.fragment_track_flow.*

import org.jetbrains.anko.imageBitmap

import com.example.mear.constants.ControlTypes
import com.example.mear.listeners.TrackElaspingChange
import com.example.mear.models.PlayControls
import com.example.mear.R
import com.example.mear.repositories.RepeatRepository
import com.example.mear.repositories.ShuffleRepository
import com.example.mear.util.ConvertByteArray
import com.example.mear.util.ExtractCover


class MainActivity : BaseServiceActivity() {

    private var musicHandler: Handler? = Handler()
    private var serviceBinded: Boolean? = false
    private var repeatOn: Boolean? = false
    private var shuffleOn: Boolean? = false

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
            window.statusBarColor = resources.getColor(R.color.track_seek)
            initializeShuffle()
            initializeRepeat()
            initializeServices()
            initializeClickListeners()
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }
    override fun initializeChangeListeners() {
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
        SongView.setOnClickListener {
            startActivity(Intent(this, SongViewActivity::class.java))
        }
        SettingsLink.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
    private fun initializeServices() {
        doBindService()
    }
    private fun initializeShuffle() {
        val shuffleMode = ShuffleRepository(this).getShuffleMode()
        when (shuffleMode) {
            ControlTypes.SHUFFLE_ON -> {
                ShuffleTracks.setText(R.string.shuffle_on)
            }
            ControlTypes.SHUFFLE_OFF -> {
                ShuffleTracks.setText(R.string.shuffle_off)
            }
        }
    }
    private fun initializeRepeat() {
        val repeatMode = RepeatRepository(this).getRepeatMode()
        when (repeatMode) {
            ControlTypes.REPEAT_ON -> {
                RepeatTrack.text = resources.getText(R.string.repeat_on)
            }
            ControlTypes.REPEAT_OFF -> {
                RepeatTrack.text = resources.getText(R.string.repeat_off)
            }
        }
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
        val playC  =  PlayControls(shuffleOn!!, null)
        ShuffleRepository(this).updateShuffleMode(playC)
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
        val playC = PlayControls(null, repeatOn)
        RepeatRepository(this).updateRepeatMode(playC)
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
            musicService!!.playNextTrack()

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
            musicService!!.playPreviousTrack()

            configureTrackDisplay()
        }
        catch (ex: Exception) {
            val exMsg = ex.message
            print(exMsg)
        }
        PreviousTrack.isEnabled = true
    }
    override fun configureTrackDisplay() {
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

                var coverExt = ExtractCover(currTrack.songPath)

                if (coverExt.hasCover()) {
                    trackCover = coverExt.retrieveCover()
                }
               /**
                val mmr = MediaMetadataRetriever()
                mmr.setDataSource(currTrack.songPath)

                if (mmr.embeddedPicture != null) {
                    trackCover = mmr.embeddedPicture
                }
                */

                resetControls()

                TrackTitle.text = trackTitle
                ArtistTitle.text = artistTitle
                AlbumTitle.text = albumTitle
                TrackDuration.text = dur
                if (trackCover != null) {
                    val convertToBmp =  ConvertByteArray(trackCover!!)
                    var songImage = convertToBmp.convertToBmp()
                    TrackCover.imageBitmap = songImage
                }
            }
        }
        catch (ex: Exception) {
            val msg = ex.message
        }
    }
    override fun resetControls() {
        TrackTitle!!.text = null
        ArtistTitle!!.text = null
        AlbumTitle!!.text = null
        CurrentPosition!!.text = null
        TrackCover!!.imageBitmap = null
    }
    override fun updateTrackProgress() {
        musicHandler!!.postDelayed(musicTrackTimeUpdateTask, 100)
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


    private var musicTrackTimeUpdateTask = object: Runnable {
       override fun run() {
           try {
               val currentPosition = musicService!!.currentPositionOfTrack() / 1000
               val dur = String.format( "%02d:%02d",
                                                                TimeUnit.SECONDS.toMinutes(currentPosition.toLong()),
                                                                (currentPosition % 60) )

               CurrentPosition.text = dur

               if (!(TrackTitle.text.equals(musicService!!.getCurrentTrack().title))) {
                   configureTrackDisplay()
               }

               musicHandler!!.postDelayed(this, 100)
           }
           catch (ex: Exception) {
                   val exMsg = ex.message
           }
       }
    }
}

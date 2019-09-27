package com.example.mear.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.R as RDroid
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast

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
import com.example.mear.models.PlayCount
import com.example.mear.repositories.PlayCountRepository
import com.example.mear.repositories.RepeatRepository
import com.example.mear.repositories.ShuffleRepository
import com.example.mear.repositories.TrackRepository
import com.example.mear.util.ConvertByteArray
import com.example.mear.util.ExtractCover
import kotlinx.coroutines.delay


class MainActivity : BaseServiceActivity() {

    private val ctx: Context? = this
    private var coverArtHandler: Handler? = Handler()
    private var musicHandler: Handler? = Handler()
    private var updateLibraryHandler: Handler? = Handler()
    private var playCountUpdated: Boolean? = false
    private var serviceBinded: Boolean? = false
    private var repeatOn: Boolean? = false
    private var shuffleOn: Boolean? = false

    external fun test()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        test()

        permissionPrompt()

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

    override fun onResume() {
        super.onResume()

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
        val clickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.SettingsLink-> {
                    showPopup(view)
                }
            }
        }
        SettingsLink.setOnClickListener (clickListener  )
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
        playCountUpdated = false
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
        playCountUpdated = false
    }
    override fun configureTrackDisplay() {
        try {
            runOnUiThread {
                playCountUpdated = false
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
        coverArtHandler!!.postDelayed(updateCoverArt, 100)
        updateLibraryHandler!!.postDelayed(updateLibrary, 1000)
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

    private fun permissionPrompt() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            }
            else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            }
        }
    }

    private fun showPopup(view: View) {
        try {
            var popup = PopupMenu(this, view)
            popup.inflate(R.menu.popup_menu)

            popup.setOnMenuItemClickListener{ item: MenuItem? ->
                when (item!!.itemId) {
                    R.id.action_settings-> {
                        startActivity(Intent(this, SettingsActivity::class.java))
                    }
                    R.id.action_song_view -> {
                        startActivity(Intent(this, SongViewActivity::class.java))
                    }
                    R.id.action_song_play_count-> {
                        val trk = musicService!!.getCurrentTrack()
                        val pc = PlayCountRepository(this).getPlayCount(trk.id)
                        val playCount = pc.playCount
                        Toast.makeText(this, "Song played $playCount times", Toast.LENGTH_LONG).show()
                    }
                }
                true
            }

            popup.show()
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }

    private var musicTrackTimeUpdateTask = object: Runnable {
       override fun run() {
           try {
               val currentPosition = musicService!!.currentPositionOfTrack() / 1000
               val dur = String.format( "%02d:%02d", TimeUnit.SECONDS.toMinutes(currentPosition.toLong()),
                                     (currentPosition % 60) )

               CurrentPosition.text = dur

               musicHandler!!.postDelayed(this, 100)
           }
           catch (ex: Exception) {
                   val exMsg = ex.message
           }
       }
    }
    private var updateCoverArt = object: Runnable {
        override fun run() {
            try {
                val trackTitle = musicService!!.getCurrentTrack().title

                if (!(TrackTitle.text == trackTitle)) {
                    configureTrackDisplay()
                }

                coverArtHandler!!.postDelayed(this, 100)
            }
            catch (ex: Exception) {
                val exMsg = ex.message
            }
        }
    }
    private var updateLibrary = object: Runnable {
        override fun run() {
            try {
                musicService!!.updateLibrary()

                updateLibraryHandler!!.postDelayed(this, 1000)
            }
            catch (ex: Exception) {
                val exMsg = ex.message
            }
        }
    }

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }
}

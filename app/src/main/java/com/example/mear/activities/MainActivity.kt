package com.example.mear.activities

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

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import org.jetbrains.anko.imageBitmap

import com.example.mear.listeners.TrackElaspingChange
import com.example.mear.R
import com.example.mear.repositories.*
import com.example.mear.repositories.RepeatRepository.RepeatTypes
import com.example.mear.repositories.ShuffleRepository.ShuffleTypes
import com.example.mear.util.ConvertByteArray
import org.jetbrains.anko.image


class MainActivity : BaseServiceActivity() {

    private val ctx: Context? = this
    private var coverArtHandler: Handler? = Handler()
    private var musicHandler: Handler? = Handler()
    private var updateLibraryHandler: Handler? = Handler()
    private var playCountUpdated: Boolean? = false
    private var serviceBinded: Boolean? = false
    private var metadataInitialized: Boolean = false
    private var repeatOn: Boolean? = false
    private var shuffleOn: Boolean? = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)
            setSupportActionBar(toolbar)

            initialize()
        }
        catch (ex: Exception) {
            val msg = ex.message
        }
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
            configurePlayControlsDisplay()
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
        val shuffleRepo = ShuffleRepository(null)
        val shuffleMode = shuffleRepo.shuffleMode(appDirectory())
        when (shuffleMode) {
            ShuffleTypes.ShuffleOff -> {
                ShuffleTracks.setText(R.string.shuffle_off)
            }
            ShuffleTypes.ShuffleOn -> {
                ShuffleTracks.setText(R.string.shuffle_on)
            }
        }
    }
    private fun initializeRepeat() {
        val repeatRepo = RepeatRepository(null)
        val repeatMode = repeatRepo.repeatMode(appDirectory())
        when (repeatMode) {
            RepeatTypes.RepeatSong -> {
                RepeatTrack.text = resources.getText(R.string.repeat_on)
            }
            RepeatTypes.RepeatOff -> {
                RepeatTrack.text = resources.getText(R.string.repeat_off)
            }
        }
    }

    private fun toggleShuffle() {
        val shuffleRepo = ShuffleRepository(null)
        shuffleRepo.alterShuffleMode(appDirectory())
        val shuffleMode = shuffleRepo.shuffleMode(appDirectory())

        when (shuffleMode) {
            ShuffleTypes.ShuffleOff -> {
                shuffleOn = false
                ShuffleTracks.text = getString(R.string.shuffle_off)
            }
            ShuffleTypes.ShuffleOn -> {
                shuffleOn = true
                ShuffleTracks.text = getString(R.string.shuffle_on)
            }
        }
    }

    private fun toggleRepeat() {
        val repeatRepo = RepeatRepository(null)

        val appPath = appDirectory()
        repeatRepo.alterRepeatMode(appPath)

        when (repeatRepo.repeatMode(appPath)) {
            RepeatTypes.RepeatOff -> {
                repeatOn = false
                RepeatTrack.text = resources.getText(R.string.repeat_off)
            }
            RepeatTypes.RepeatSong -> {
                repeatOn = true
                RepeatTrack.text = resources.getText(R.string.repeat_on)
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
                val currSong= musicService!!.getCurrentSong()
                val dur = String.format(
                    "%02d:%02d", TimeUnit.SECONDS.toMinutes(currSong.duration.toLong()),
                    (currSong.duration % 60)
                )

                val coverArtRepo = CoverArtRepository()
                val apiRepo = APIRepository()
                val tokenRepo = TokenRepository()
                val path = appDirectory()
                val apiUri = apiRepo.retrieveRecord(path)
                val myToken = tokenRepo.retrieveToken(path)
                val coverArt = CoverArtRepository.CoverArt(currSong.coverArtId, currSong.title)
                val imgData = coverArtRepo.fetchCoverArtImage(myToken, coverArt, apiUri.uri)
                val ImageConvByte = ConvertByteArray(imgData)
                val convertedImage = ImageConvByte.convertToBmp(imgData)


                resetControls()

                TrackTitle.text = currSong.title
                ArtistTitle.text = currSong.artist
                AlbumTitle.text = currSong.album
                TrackDuration.text = dur
                TrackCover.imageBitmap = convertedImage
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

        if ((musicService == null || !musicService!!.isPlaying())) {
            PlayTrack.setImageResource(android.R.drawable.ic_media_play)
            PlayTrack.setColorFilter(Color.GREEN)
        }
        else {
            PlayTrack.setImageResource(android.R.drawable.ic_media_pause)
            PlayTrack.setColorFilter(Color.RED)
        }
    }

    private fun songMetadataLoaded(): Boolean {
        if (TrackTitle.text.isEmpty()) {
            return false
        }
        if (ArtistTitle.text.isEmpty()) {
            return false
        }
        if (AlbumTitle.text.isEmpty()) {
            return false
        }
        if (TrackCover.image == null) {
            return false
        }

        val currSong = musicService!!.getCurrentSong()

        if (!TrackTitle.text.equals(currSong.title)) {
            return false
        }
        if ((ArtistTitle.text != currSong.artist)) {
            return false
        }

        return true
    }

    // TODO: Might need this down the road for playing songs off an external
    // storage
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
            if (musicService!!.getCurrentSong().downloaded) {
                popup.inflate(R.menu.popup_menu_song_downloaded)
            }
            else {
                popup.inflate(R.menu.popup_menu)
            }

            popup.setOnMenuItemClickListener{ item: MenuItem? ->
                when (item!!.itemId) {
                    R.id.action_settings-> {
                        startActivity(Intent(this, SettingsActivity::class.java))
                    }
                    R.id.action_song_view -> {
                        startActivity(Intent(this, IcarusSongActivity::class.java))
                    }
                    R.id.action_song_delete -> {
                        // TODO: handle song deletion
                        val ss = true
                        val appPath = appDirectory()
                        val trackRepo = TrackRepository()
                        // The method I am calling has not been implemented on the C++
                        // side
                        trackRepo.delete(musicService!!.getCurrentSong(), appPath)
                    }
                    R.id.action_song_download -> {
                        val appPath = appDirectory()
                        val apiRepo = APIRepository()
                        val tokenRepo = TokenRepository()
                        val trackRepo = TrackRepository()
                        var song = musicService!!.getCurrentSong()

                        val token = tokenRepo.retrieveToken(appPath)
                        val apiInfo = apiRepo.retrieveRecord(appPath)
                        song = trackRepo.download(token, song, appPath)
                        musicService!!.changeSongDownloadStatus()
                        // TODO: implement something to include the downloaded song into the songQueue
                        // essentially replacing the song that already exists, that way one can
                        // actually access the file without having to restart the app
                    }
                    R.id.action_song_play_count-> {
                        val trk = musicService!!.getCurrentSong()
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
               if (!songMetadataLoaded()) {
                   configureTrackDisplay()
               }
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
}

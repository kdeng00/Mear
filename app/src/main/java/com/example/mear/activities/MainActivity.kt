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

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import org.jetbrains.anko.image
import org.jetbrains.anko.imageBitmap

import com.example.mear.listeners.TrackElaspingChange
import com.example.mear.R
import com.example.mear.repositories.*
import com.example.mear.repositories.RepeatRepository.RepeatTypes
import com.example.mear.repositories.ShuffleRepository.ShuffleTypes
import com.example.mear.util.ConvertByteArray
import com.example.mear.workers.IcarusSyncManager


class MainActivity : BaseServiceActivity() {

    private var musicHandler: Handler? = Handler()
    private var playCountUpdated: Boolean? = false
    private var serviceBinded: Boolean? = false
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
                        val appPath = appDirectory()
                        val trackRepo = TrackRepository()

                        var currSong = musicService!!.getCurrentSong()
                        Toast.makeText(this, "Deleting song", Toast.LENGTH_SHORT).show()
                        val result = trackRepo.delete(currSong, appPath)
                        if (result) {
                            musicService!!.removeSongDownloadStatus(currSong)
                        }
                    }
                    R.id.action_song_download -> {
                        val appPath = appDirectory()
                        var song = musicService!!.getCurrentSong()

                        Toast.makeText(this, "Downloading song", Toast.LENGTH_SHORT).show()

                        val constraints = androidx.work.Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED).build()

                        val data = Data.Builder()
                        data.putString("appPath", appPath)

                        data.putInt("songId", song.id)
                        data.putString("songTitle", song.title)
                        data.putString("songArtist", song.artist)
                        data.putString("songAlbum", song.album)
                        data.putString("songAlbumArtist", song.albumArtist)
                        data.putString("songGenre", song.genre)
                        data.putInt("songYear", song.year)
                        data.putInt("songDuration", song.duration)
                        data.putInt("songCoverArtId", song.coverArtId)
                        data.putBoolean("songDownloaded", false)
                        data.putInt("songDisc", song.disc)
                        data.putInt("songTrack", song.track)

                        val task = OneTimeWorkRequest.Builder(IcarusSyncManager::class.java)
                            .setInputData(data.build())
                            .setConstraints(constraints)
                            .build()
                        WorkManager.getInstance().enqueue(task)

                        WorkManager.getInstance(this).getWorkInfoByIdLiveData(task.id)
                            .observe(this, Observer { info ->
                                if (info != null && info.state.isFinished) {
                                    song.path = info.outputData.getString("songPath")!!
                                    song.filename = info.outputData.getString("songFilename")!!
                                    song.downloaded = info.outputData.getBoolean("songDownloaded", true)!!
                                    musicService!!.changeSongDownloadStatus(song)
                                }
                            })
                    }
                    /**
                    R.id.action_song_play_count-> {
                        // TODO: not implemented
                        val trk = musicService!!.getCurrentSong()
                        Toast.makeText(this, "Song played $playCount times", Toast.LENGTH_LONG).show()
                    }
                    */
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

package com.example.mear.playback.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.widget.Toast

import java.lang.Exception
import kotlin.random.Random

import com.example.mear.management.DatabaseManager
import com.example.mear.management.MusicFiles
import com.example.mear.management.TrackManager
import com.example.mear.models.PlayControls
import com.example.mear.models.Track
import com.example.mear.repositories.TrackRepository


class MusicService: Service() {

    private var trackPlayer: MediaPlayer? = null
    private  var currentSongIndex: Int? = null
    private var shuffleOn: Boolean? = false
    private val mBinder = LocalBinder()
    private val seconds = 4


    inner class LocalBinder : Binder() {
        internal val service: MusicService
            get() = this@MusicService
    }


    private enum class PlayTypes {
        PlayNextSong, PlaySong, PlayPreviousSong
    }


    override fun onCreate() {
        initializeMediaPlayer()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onDestroy() {

        val intent = Intent(this, mBinder.javaClass)
        onUnbind(intent)

        Toast.makeText(this, "Music Service Stopped", Toast.LENGTH_SHORT).show()
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }



    fun playSongTrack() {
        try {
            trackPlayer!!.start()
        }
        catch (ex: Exception ) {
            val exMsg = ex.message
        }
    }
    fun pauseSongTrack() {
        try {
            trackPlayer!!.pause()
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }
    fun playPreviousTrack() {
        try {

        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }
    fun playNextTrack() {
        try {
            trackPlayer!!.reset()
            var nextTrack = fetchSongIndex(PlayTypes.PlayNextSong)
            val track = TrackRepository(this).getTrack(nextTrack)
            trackPlayer!!.setDataSource(track.songPath)
            trackPlayer!!.prepare()
            trackPlayer!!.start()
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }
    fun playPreviousTrack(controls: PlayControls) {
        try {
            configureControl(controls)
            val duration = trackPlayer!!.currentPosition / 1000
            if (duration  > seconds) {


                trackPlayer!!.reset()
                trackPlayer!!.setDataSource(TrackRepository(this).getTrack(currentSongIndex!!).songPath)
                trackPlayer!!.prepare()
                trackPlayer!!.start()
            }
            else {


                var previousTrack = fetchSongIndex(PlayTypes.PlayPreviousSong)
                val track = TrackRepository(this).getTrack(previousTrack)
                trackPlayer!!.reset()
                trackPlayer!!.setDataSource(track.songPath)
                trackPlayer!!.prepare()
                trackPlayer!!.start()
            }
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }
    fun configureControl(controls: PlayControls) {
        shuffleOn = controls.shuffleOn
    }
    fun playNextTrack(controls: PlayControls) {
        try {
            configureControl(controls)
            val nextTrack = fetchSongIndex(PlayTypes.PlayNextSong)

            val track = TrackRepository(this).getTrack(nextTrack)
            trackPlayer!!.reset()
            trackPlayer!!.setDataSource(track.songPath)
            trackPlayer!!.prepare()
            trackPlayer!!.start()
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }

    fun getCurrentTrack(): Track {
        var track: Track? = null

        track = TrackRepository(this).getTrack(currentSongIndex!!)

        return track!!
    }


    private fun fetchSongIndex(playType: PlayTypes): Int {
        var songIndex: Int? = currentSongIndex
        val songCount = retrieveSongCount()

        try {
            when (playType) {
                PlayTypes.PlayPreviousSong -> {
                    if (currentSongIndex!! != 0) {
                        if (!shuffleOn!!) {
                            currentSongIndex = currentSongIndex!!.dec()
                            songIndex = currentSongIndex!!
                        }
                        else {
                            currentSongIndex = Random.nextInt(0, songCount!!)
                            songIndex = currentSongIndex
                        }
                    }
                }
                PlayTypes.PlaySong -> {
                    if (shuffleOn!!) {
                        songIndex = Random.nextInt(0, songCount!!)
                    }
                    else {
                        songIndex = 0
                    }
                }
                PlayTypes.PlayNextSong -> {
                    if (currentSongIndex!! != songCount!!) {
                        if (!shuffleOn!!) {
                            currentSongIndex = currentSongIndex!!.inc()
                            songIndex = currentSongIndex!!
                        }
                        else {
                            currentSongIndex = Random.nextInt(0, songCount!!)
                            songIndex = currentSongIndex!!
                        }
                    }
                }
            }
        }
        catch (ex: Exception) {
            val exMsg = ex.message
            println(exMsg)
        }

        return songIndex!!
    }
    fun currentPositionOfTrack(): Int {
        return trackPlayer!!.currentPosition
    }
    fun durationOfTrack(): Int {
        return trackPlayer!!.duration
    }

    fun isPlaying(): Boolean {
        return trackPlayer!!.isPlaying
    }


    private fun initializeMediaPlayer() {
        if (trackPlayer == null) {
            trackPlayer = MediaPlayer()
        }
        if (trackRepositoryEmpty()!!) {
            populateTrackRepository()
        }

        currentSongIndex = Random.nextInt(0, TrackRepository(this).getSongCount())
        val trackToPlay = TrackRepository(this).getTrack(currentSongIndex!!)
        trackPlayer!!.setDataSource(trackToPlay.songPath)
        trackPlayer!!.prepareAsync()
        trackPlayer!!.setOnCompletionListener {
            playNextTrack()
        }
    }
    private  fun populateTrackRepository() {
        var mp3Paths = MusicFiles(android.os.Environment.getExternalStorageDirectory())
        mp3Paths.searchForMp3Songs()
        val paths = mp3Paths.allSongs
        val trackMgr = TrackManager(paths!!)
        trackMgr.configureTracks(this)
    }


    private fun trackRepositoryEmpty(): Boolean? {
        val trackCount = retrieveSongCount()

        when (trackCount) {
            null -> {
                return true
            }
            0 -> {
                return true
            }
        }

        return false
    }

    private fun retrieveSongCount(): Int? {

        try {
            var db  = DatabaseManager(this)
            var trackDb = TrackRepository(this)
            val count = trackDb.getSongCount()

            return count
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }

        return 0
    }
}

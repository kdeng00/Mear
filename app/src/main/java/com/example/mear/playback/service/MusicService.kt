package com.example.mear.playback.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.widget.Toast

import java.lang.Exception
import kotlin.random.Random

import com.example.mear.constants.ControlTypes
import com.example.mear.management.MusicFiles
import com.example.mear.management.TrackManager
import com.example.mear.models.PlayControls
import com.example.mear.models.Track
import com.example.mear.repositories.RepeatRepository
import com.example.mear.repositories.ShuffleRepository
import com.example.mear.repositories.TrackRepository


class MusicService: Service() {

    companion object {
        //fun curSongIndex(): Int = currentSongIndex
    }

    private var trackPlayer: MediaPlayer? = null
    private  var currentSongIndex: Int? = null
    private var shuffleOn: Boolean? = null
    private var repeatOn: Boolean? = null
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

    fun playTrack(id: Int) {
        currentSongIndex = id
        trackPlayer!!.reset()
        trackPlayer!!.setDataSource(TrackRepository(this).getTrack(id).songPath)
        trackPlayer!!.prepare()
        trackPlayer!!.start()
    }

    fun goToPosition(progress: Int) {
        trackPlayer!!.seekTo(progress)
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
            val duration = trackPlayer!!.currentPosition / 1000
            shuffleOn = retrieveShuffleMode()
            trackPlayer!!.reset()
            if (duration > seconds) {
                trackPlayer!!.setDataSource(TrackRepository(this).getTrack(currentSongIndex!!).songPath)
                trackPlayer!!.prepare()
                trackPlayer!!.start()
            }
            else {
                val previousTrackNumber = fetchSongIndex(PlayTypes.PlayPreviousSong)
                val track = TrackRepository(this).getTrack(previousTrackNumber)
                trackPlayer!!.setDataSource(track.songPath)
                trackPlayer!!.prepare()
                trackPlayer!!.start()
            }
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }
    fun playNextTrack() {
        try {
            trackPlayer!!.reset()
            shuffleOn = retrieveShuffleMode()
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
        repeatOn = controls.repeatOn
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
        val info = trackPlayer!!.trackInfo

        if (trackPlayer!!.isPlaying) {
            val res = "songs are being played"
        }

        return track!!
    }


    private fun fetchSongIndex(playType: PlayTypes): Int {
        var songIndex: Int? = currentSongIndex
        repeatOn = retrieveRepeatMode()
        if (repeatOn!!) {
            return songIndex!!
        }
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
        try {
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
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }
    private  fun populateTrackRepository() {
        var mp3Paths = MusicFiles(android.os.Environment.getExternalStorageDirectory())
        mp3Paths.searchForMp3Songs()
        val paths = mp3Paths.allSongs
        val trackMgr = TrackManager(paths!!)
        trackMgr.addTracks(this)
        initializeShuffleMode()
        initializeRepeatMode()
    }
    private fun initializeShuffleMode() {
        try {
            val shuffleMode = ShuffleRepository(this).getShuffleMode()
            when (shuffleMode) {
                ControlTypes.SHUFFLE_ON -> {
                    shuffleOn = true
                }
                ControlTypes.SHUFFLE_OFF -> {
                    shuffleOn = false
                }
                null -> {
                    shuffleOn = false
                }
            }
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }
    private fun initializeRepeatMode() {
        try {
            val repeatMode = RepeatRepository(this).getRepeatMode()
            when (repeatMode) {
                ControlTypes.REPEAT_ON -> {
                    repeatOn = true
                }
                ControlTypes.REPEAT_OFF -> {
                    repeatOn = false
                }
                null -> {
                    repeatOn = false
                }
            }
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }


    private fun retrieveShuffleMode(): Boolean? {
        val shuffleMode = ShuffleRepository(this).getShuffleMode()
        when (shuffleMode) {
            ControlTypes.SHUFFLE_ON -> {
                return  true
            }
            ControlTypes.SHUFFLE_OFF -> {
                return false
            }
            null -> {
                return null
            }
            else -> {
                return false
            }
        }
    }
    private fun retrieveRepeatMode(): Boolean? {
        val repeatMode = RepeatRepository(this).getRepeatMode()
        when (repeatMode) {
            ControlTypes.REPEAT_ON -> {
                return true
            }
            ControlTypes.REPEAT_OFF -> {
                return false
            }
            null -> {
                return null
            }
            else -> {
                return false
            }
        }
    }

    private fun trackRepositoryEmpty(): Boolean? {
        val trackCount = retrieveSongCount()

        if (trackCount!! < 1) {
            return true
        }

        return false
    }

    private fun retrieveSongCount(): Int? {

        try {
            val count = TrackRepository(this).getSongCount()
            TrackRepository(this).delete()

            // TODO: Replace this with the local count varialbe when issue #43 is resolved
            // as well as remove the call to delete the Track table
            return 0
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }

        return 0
    }
}

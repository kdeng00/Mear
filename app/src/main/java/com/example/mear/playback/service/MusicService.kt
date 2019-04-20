package com.example.mear.playback.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Environment
import android.os.IBinder
import android.widget.Toast

import java.lang.Exception
import kotlin.random.Random

import com.example.mear.constants.ControlTypes
import com.example.mear.constants.Interval
import com.example.mear.management.MusicFiles
import com.example.mear.management.TrackManager
import com.example.mear.models.PlayControls
import com.example.mear.models.Track
import com.example.mear.repositories.RepeatRepository
import com.example.mear.repositories.ShuffleRepository
import com.example.mear.repositories.TrackRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MusicService: Service() {

    companion object {
        //fun curSongIndex(): Int = currentSongIndex
    }

    private var trackPlayer: MediaPlayer? = null
    private var trackMgr: TrackManager? = null
    private var currentTrack = Track()
    private var currentSongIndex: Int? = null
    private var addingMusic: Boolean? = false
    private var shuffleOn: Boolean? = null
    private var repeatOn: Boolean? = null
    private val mBinder = LocalBinder()
    private val seconds = Interval.SONG_REWIND


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
        try {
            currentSongIndex = id
            trackPlayer!!.reset()
            currentTrack = TrackRepository(this).getTrack(id)
            trackPlayer!!.setDataSource(currentTrack.songPath)
            trackPlayer!!.prepare()
            trackPlayer!!.start()
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
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
                currentTrack = TrackRepository(this).getTrack(currentSongIndex!!)
                trackPlayer!!.setDataSource(currentTrack.songPath)
                trackPlayer!!.prepare()
                trackPlayer!!.start()
            }
            else {
                val previousTrackNumber = fetchSongIndex(PlayTypes.PlayPreviousSong)
                currentTrack = TrackRepository(this).getTrack(previousTrackNumber)
                trackPlayer!!.setDataSource(currentTrack.songPath)
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
            currentTrack = TrackRepository(this).getTrack(nextTrack)
            trackPlayer!!.setDataSource(currentTrack.songPath)
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

        return currentTrack
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


    fun updateLibrary() {
        checkForMusicChange()
    }

    private fun initializeMediaPlayer() {
        try {
            if (trackPlayer == null) {
                trackPlayer = MediaPlayer()
            }
            if (trackRepositoryEmpty()!!) {
                populateTrackRepository()
            }

            currentSongIndex = Random.nextInt(0, TrackRepository(this).getAll().count())
            currentTrack = TrackRepository(this).getTrack(currentSongIndex!!)
            trackPlayer!!.setDataSource(currentTrack.songPath)
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
        var mp3Paths = MusicFiles(Environment.getExternalStorageDirectory())
        mp3Paths.initialMp3Search()
        val paths = mp3Paths.allSongs

        trackMgr = TrackManager(paths!!)
        trackMgr!!.initializeContext(this)
        trackMgr!!.initializeLibrary()

        initializeShuffleMode()
        initializeRepeatMode()
    }
    private fun checkForMusicChange() {

        if (addingMusic!!) {
            return
        }
        addingMusic = true
        val ctx = this
        val mp3Paths = MusicFiles(Environment.getExternalStorageDirectory())
        GlobalScope.launch {
            mp3Paths.searchForMp3Songs()
            val paths = mp3Paths.allSongs
            val allTracks = TrackRepository(ctx).getAll()
            val trackCount = allTracks.count()
            val tracksToDelete = musicToDelete(allTracks!!, paths!!)
            val trackPathsToAdd = musicToAdd(allTracks!!, paths!!)

            if (tracksToDelete!!.any()) {
                TrackRepository(ctx).deleteTracks(tracksToDelete!!)
            }
            if (trackPathsToAdd!!.any()) {
                val trkMgr = TrackManager(null)
                trkMgr.initializeContext(ctx)
                trkMgr.addnewSongs(trackPathsToAdd!!, trackCount)
                addingMusic = false
            }
        }

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
            }
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }

    private fun musicToDelete(allTracks: List<Track>?, paths: MutableList<String>): List<Track>? {
        try {
            var missingTracks = mutableListOf<Track>()
            allTracks!!.iterator().forEach {
                val songExists = paths.contains(it.songPath)
                if (!songExists) {
                    missingTracks.add(it)
                }
            }

            return missingTracks
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }

        return null
    }
    private fun musicToAdd(allTracks: List<Track>?, paths: MutableList<String>): List<String>? {
        try {
            var newTracks = mutableListOf<String>()

            val trackPaths = retrieveTrackPaths(allTracks)
            paths.iterator().forEach {
                val songExists = trackPaths!!.contains(it)
                if (!songExists) {
                    newTracks.add(it)
                }
            }

            return newTracks
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }

        return null
    }
    private fun retrieveTrackPaths(allTracks: List<Track>?): List<String>? {
        try {
            val trackPaths = mutableListOf<String>()
            for (i in allTracks!!) {
                trackPaths.add(i.songPath)
            }
            return trackPaths
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }

        return null
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
            return TrackRepository(this).getAll().count()
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }

        return 0
    }
}

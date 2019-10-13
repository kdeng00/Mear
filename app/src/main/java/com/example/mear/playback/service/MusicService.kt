package com.example.mear.playback.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
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
import com.example.mear.models.APIInfo
import com.example.mear.models.Song
import com.example.mear.models.Token
import com.example.mear.models.Track
import com.example.mear.repositories.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MusicService(var appPath: String = ""): Service() {

    companion object {
        //fun curSongIndex(): Int = currentSongIndex
    }

    private var trackPlayer: MediaPlayer? = null
    private var trackMgr: TrackManager? = null
    private var songQueue = mutableListOf<Song>()
    private var currentTrack = Track()
    private var currentSong = Song()
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
        val b = intent.extras
        appPath = b!!.get("appPath") as String
        initializeMediaPlayer()
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


    fun icarusPlaySong(token: Token, song: Song, apiInfo: APIInfo) {
        val uri = APIRepository.retrieveSongStreamUri(apiInfo, song)
        val hddr = APIRepository.retrieveSongStreamHeader(token)

        try {
            trackPlayer!!.reset()
            trackPlayer!!.setDataSource(this, uri, hddr)
            trackPlayer!!.prepare()
            trackPlayer!!.start()
        }
        catch (ex: Exception) {
            val msg = ex.message
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
            val apiRepo = APIRepository()
            val tokenRepo = TokenRepository()

            val token = tokenRepo.retrieveToken(appPath)
            val apiInfo = apiRepo.retrieveRecord(appPath)
            currentSong = songQueue[Random.nextInt(0, songQueue.size - 1)]

            trackPlayer!!.reset()
            if (duration > seconds) {
                trackPlayer!!.setDataSource(this,
                    APIRepository.retrieveSongStreamUri(apiInfo, currentSong),
                    APIRepository.retrieveSongStreamHeader(token))
                trackPlayer!!.prepare()
                trackPlayer!!.start()

            }
            else {
                trackPlayer!!.setDataSource(this,
                    APIRepository.retrieveSongStreamUri(apiInfo, currentSong),
                    APIRepository.retrieveSongStreamHeader(token))
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
            val apiRepo = APIRepository()
            val tokenRepo = TokenRepository()

            val token = tokenRepo.retrieveToken(appPath)
            val apiInfo = apiRepo.retrieveRecord(appPath)
            currentSong = songQueue[Random.nextInt(0, songQueue.size - 1)]

            trackPlayer!!.setDataSource(this,
                APIRepository.retrieveSongStreamUri(apiInfo, currentSong),
                APIRepository.retrieveSongStreamHeader(token))
            trackPlayer!!.prepare()
            trackPlayer!!.start()
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }

    fun configureControl(controls: PlayControls) {
        shuffleOn = controls.shuffleOn
        repeatOn = controls.repeatOn
    }

    fun getCurrentSong(): Song {
        return currentSong
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
            if (appPath.isEmpty()) {
                return
            }

            val trackRepo = TrackRepository()
            val apiRepo = APIRepository()
            val tokenRepo = TokenRepository()

            val token = tokenRepo.retrieveToken(appPath)
            val apiInfo = apiRepo.retrieveRecord(appPath)
            val songs = trackRepo.fetchSongs(token, apiInfo.uri)
            songQueue = songs.toMutableList()

            currentSong = songQueue[Random.nextInt(0, songs.size - 1)]
            trackPlayer!!.setDataSource(this,
                APIRepository.retrieveSongStreamUri(apiInfo, currentSong),
                APIRepository.retrieveSongStreamHeader(token))
            trackPlayer!!.prepareAsync()
            trackPlayer!!.setOnCompletionListener {
                playNextTrack()
            }
        }
        catch (ex: Exception) {
            val exMsg = ex.message
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
}

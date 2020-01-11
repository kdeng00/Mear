package com.example.mear.playback.service

import java.lang.Exception
import kotlin.random.Random

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.widget.Toast

import com.example.mear.constants.Interval
import com.example.mear.models.APIInfo
import com.example.mear.models.Song
import com.example.mear.models.Token
import com.example.mear.repositories.*
import com.example.mear.repositories.RepeatRepository.RepeatTypes
import com.example.mear.repositories.ShuffleRepository.ShuffleTypes


class MusicService(var appPath: String = ""): Service() {

    private var trackPlayer: MediaPlayer? = null
    private var songQueue = mutableListOf<Song>()
    private var currentSong = Song()
    private var currentSongIndex: Int? = null
    private var shuffleOn: Boolean? = null
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


    fun downloadSong(token: Token, song: Song, appPath: String) {
        val trackRepo = TrackRepository()
        val s = trackRepo.download(token, currentSong, appPath)
        changeSongDownloadStatus(s)
    }
    fun icarusPlaySong(token: Token, song: Song, apiInfo: APIInfo) {
        if (song.downloaded) {
            offlinePlaySong(song)
            return
        }

        val uri = APIRepository.retrieveSongStreamUri(apiInfo, song)
        val hddr = APIRepository.retrieveSongStreamHeader(token)
        currentSong = song
        currentSongIndex = songQueue.indexOfFirst { it.id == currentSong.id }

        try {
            trackPlayer!!.reset()
            trackPlayer!!.setDataSource(this, uri, hddr)
            trackPlayer!!.prepareAsync()
            trackPlayer!!.start()
        }
        catch (ex: Exception) {
            val msg = ex.message
        }
    }

    fun changeSongDownloadStatus(song: Song) {
        if (!song.downloaded) {
            song.downloaded = true
        }
        currentSong = song
        currentSongIndex = songQueue.indexOfFirst { it.id == currentSong.id }
        songQueue[currentSongIndex!!] = currentSong
        /**
        val curPosition = currentPositionOfTrack()
        trackPlayer!!.reset()
        trackPlayer!!.setDataSource(currentSong.path)
        trackPlayer!!.prepareAsync()
        trackPlayer!!.seekTo(curPosition)
        trackPlayer!!.start()
        */
    }

    fun removeSongDownloadStatus(song: Song) {
        if (song.downloaded) {
            song.downloaded = false
        }
        currentSong = song
        currentSongIndex = songQueue.indexOfFirst { it.id == currentSong.id }
        songQueue[currentSongIndex!!] = currentSong
    }

    fun goToPosition(progress: Int) { trackPlayer!!.seekTo(progress) }

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
            val repeatRepo = RepeatRepository(null)
            val tokenRepo = TokenRepository()

            val repeatMode = repeatRepo.repeatMode(appPath)
            val token = tokenRepo.retrieveToken(appPath)
            val apiInfo = apiRepo.retrieveRecord(appPath)
            if (parseRepeatMode(repeatMode) || duration > seconds) {

                trackPlayer!!.pause()
                trackPlayer!!.seekTo(0)
                trackPlayer!!.start()

            }
            else {
                if (retrieveShuffleMode()!! && !parseRepeatMode(repeatMode)) {
                    currentSongIndex = Random.nextInt(0, songQueue.size - 1)
                } else if (!parseRepeatMode(repeatMode)) {
                    currentSongIndex = if (currentSongIndex == 0)
                        songQueue.size - 1 else currentSongIndex!! - 1
                }

                currentSong = songQueue[currentSongIndex!!]
                trackPlayer!!.reset()
                if (currentSong.downloaded) {
                    trackPlayer!!.setDataSource(currentSong.path)
                }
                else {
                    val uri = APIRepository.retrieveSongStreamUri(apiInfo, currentSong)
                    val hddr = APIRepository.retrieveSongStreamHeader(token)
                    trackPlayer!!.setDataSource(this,
                        uri, hddr)
                }

                trackPlayer!!.prepareAsync()
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
            val repeatRepo = RepeatRepository(null)
            val tokenRepo = TokenRepository()

            val token = tokenRepo.retrieveToken(appPath)
            val repeatMode = repeatRepo.repeatMode(appPath)
            val apiInfo = apiRepo.retrieveRecord(appPath)

            if (retrieveShuffleMode()!! && !parseRepeatMode(repeatMode)) {
                currentSongIndex = Random.nextInt(0, songQueue.size - 1)
            } else if (!parseRepeatMode(repeatMode)) {
                currentSongIndex = if ((currentSongIndex!! + 1) == songQueue.size)
                    0 else currentSongIndex!! + 1
            }

            currentSong = songQueue[currentSongIndex!!]

            if (currentSong.downloaded) {
                trackPlayer!!.setDataSource(currentSong.path)
            }
            else {
                trackPlayer!!.setDataSource(this,
                    APIRepository.retrieveSongStreamUri(apiInfo, currentSong),
                    APIRepository.retrieveSongStreamHeader(token))
            }
            trackPlayer!!.prepareAsync()
            trackPlayer!!.start()
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }

    fun getCurrentSong(): Song { return currentSong }


    fun currentPositionOfTrack(): Int { return trackPlayer!!.currentPosition }

    fun durationOfTrack(): Int { return trackPlayer!!.duration }


    fun isPlaying(): Boolean { return trackPlayer!!.isPlaying }


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
            val songs = trackRepo.fetchSongsIncludingDownloaded(token, apiInfo.uri, appPath)
            songQueue = songs.toMutableList()

            currentSongIndex = if (retrieveShuffleMode()!!)
                Random.nextInt(0, songQueue.size - 1) else 0

            currentSong = songQueue[currentSongIndex!!]
            if (currentSong.downloaded) {
                trackPlayer!!.setDataSource(currentSong.path)
            }
            else {
                trackPlayer!!.setDataSource(this,
                    APIRepository.retrieveSongStreamUri(apiInfo, currentSong),
                    APIRepository.retrieveSongStreamHeader(token))
            }

            trackPlayer!!.prepareAsync()
            trackPlayer!!.setOnCompletionListener {
                playNextTrack()
            }
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }


    private fun retrieveShuffleMode(): Boolean? {
        val shuffleRepo = ShuffleRepository(null)

        return when (shuffleRepo.shuffleMode(appPath)) {
            ShuffleTypes.ShuffleOn -> true
            ShuffleTypes.ShuffleOff -> false
            else -> false
        }
    }

    private fun parseRepeatMode(repeatMode: RepeatTypes): Boolean {
        return when (repeatMode) {
            RepeatTypes.RepeatOff -> false
            RepeatTypes.RepeatSong -> true
        }
    }

    private fun offlinePlaySong(song: Song) {
        try {
            currentSong = song
            currentSongIndex = songQueue.indexOfFirst { it.id == song.id }
            songQueue[currentSongIndex!!] = currentSong

            trackPlayer!!.reset()
            trackPlayer!!.setDataSource(song.path)
            trackPlayer!!.prepareAsync()
            trackPlayer!!.start()
        }
        catch (ex: Exception) {
            var msg = ex.message
        }
    }
}


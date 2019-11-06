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

    companion object {
        //fun curSongIndex(): Int = currentSongIndex
    }

    private var trackPlayer: MediaPlayer? = null
    //private var trackMgr: TrackManager? = null
    private var songQueue = mutableListOf<Song>()
    //private var currentTrack = Track()
    private var currentSong = Song()
    private var currentSongIndex: Int? = null
    //private var addingMusic: Boolean? = false
    private var shuffleOn: Boolean? = null
    //private var repeatOn: Boolean? = null
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
        currentSong = song

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
                    if (currentSongIndex == 0) {
                        currentSongIndex = songQueue.size - 1
                    } else {
                        currentSongIndex = currentSongIndex!! - 1
                    }
                }

                currentSong = songQueue[currentSongIndex!!]
                val uri = APIRepository.retrieveSongStreamUri(apiInfo, currentSong)
                val hddr = APIRepository.retrieveSongStreamHeader(token)
                trackPlayer!!.reset()
                trackPlayer!!.setDataSource(this,
                    uri, hddr)

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
            val repeatRepo = RepeatRepository(null)
            val tokenRepo = TokenRepository()

            val token = tokenRepo.retrieveToken(appPath)
            val repeatMode = repeatRepo.repeatMode(appPath)
            val apiInfo = apiRepo.retrieveRecord(appPath)

            if (retrieveShuffleMode()!! && !parseRepeatMode(repeatMode)) {
                currentSongIndex = Random.nextInt(0, songQueue.size - 1)
            } else if (!parseRepeatMode(repeatMode)) {
                if ((currentSongIndex!! + 1) == songQueue.size) {
                    currentSongIndex = 0
                }
                else {
                    currentSongIndex = currentSongIndex!! + 1
                }
            }

            currentSong = songQueue[currentSongIndex!!]

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
            val songs = trackRepo.fetchSongs(token, apiInfo.uri)
            songQueue = songs.toMutableList()

            if (retrieveShuffleMode()!!) {
                currentSongIndex = Random.nextInt(0, songs.size - 1)
            } else {
                currentSongIndex = 0
            }

            currentSong = songQueue[currentSongIndex!!]
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


    private fun retrieveShuffleMode(): Boolean? {
        val shuffleRepo = ShuffleRepository(null)
        val shuffleMode = shuffleRepo.shuffleMode(appPath)
        var shuffleOn = false

        when (shuffleMode) {
            ShuffleTypes.ShuffleOn -> shuffleOn = true
            ShuffleTypes.ShuffleOff -> shuffleOn = false
        }

        return shuffleOn
    }

    private fun parseRepeatMode(repeatMode: RepeatTypes): Boolean {
        var repeatOn: Boolean? = null
        when (repeatMode) {
            RepeatTypes.RepeatOff -> repeatOn = false
            RepeatTypes.RepeatSong -> repeatOn = true
        }

        return repeatOn!!
    }
}

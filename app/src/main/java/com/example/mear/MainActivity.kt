package com.example.mear

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity

import java.lang.Exception
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlin.io.*
import kotlin.random.Random

import com.example.mear.management.MusicFiles
import com.example.mear.management.TrackManager
import com.example.mear.repositories.TrackRepository

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //loadTracks(loadSongPaths())

        initialize()
    }


    private fun initialize() {
        songCount = TrackRepository(this).getSongCount()
        currentSong = fetchSongIndex(PlayTypes.PlaySong)
        initializeMediaPlayer()

        initializeClickListeners()
        initializeCompletionListener()
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
    }
    private fun initializeCompletionListener() {
        trackPlayer!!.setOnCompletionListener {
            playNextSongTrack()
        }
    }
    private fun initializeMediaPlayer() {
        if (trackPlayer == null) {
            trackPlayer = MediaPlayer()
            playerInitialized = true
            val tr = TrackRepository(this).getTrack(currentSong!!)
            trackPlayer!!.setDataSource(tr.songPath)
            trackPlayer!!.prepare()
        }
    }

    private fun playSongTrack() {
        PlayTrack.isEnabled = true
        try {
            if (!trackPlayer!!.isPlaying) {
                trackPlayer!!.start()
                configureTrackDisplay()
            } else {
                trackPlayer!!.pause()
            }
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
        PlayTrack.isEnabled = true
    }
    private fun playNextSongTrack() {
        NextTrack.isEnabled = false
        try {
            currentSong = fetchSongIndex(PlayTypes.PlayNextSong)

            if (trackPlayer!!.isPlaying) {
                trackPlayer!!.stop()
            }

            trackPlayer!!.reset()
            trackPlayer!!.setDataSource(TrackRepository(this).getTrack(currentSong!!).songPath)
            trackPlayer!!.prepare()
            trackPlayer!!.start()
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
            currentSong = fetchSongIndex(PlayTypes.PlayPreviousSong)

            if (trackPlayer!!.isPlaying) {
                trackPlayer!!.stop()
            }

            trackPlayer!!.reset()
            trackPlayer!!.setDataSource(TrackRepository(this).getTrack(currentSong!!).songPath)
            trackPlayer!!.prepare()
            trackPlayer!!.start()
            configureTrackDisplay()
        }
        catch (ex: Exception) {
            val exMsg = ex.message
            print(exMsg)
        }
        PreviousTrack.isEnabled = true
    }
    private fun configureTrackDisplay() {
        val currTrack = TrackRepository(this).getTrack(currentSong!!)
        val trackTitle = currTrack.title
        val albumTitle = currTrack.album
        var trackCover: ByteArray? = null

        var mmr = MediaMetadataRetriever()
        mmr.setDataSource(currTrack.songPath)

        if (mmr.embeddedPicture != null) {
            trackCover = mmr.embeddedPicture
        }

        TrackTitle.text.clear()
        AlbumTitle.text.clear()
        TrackCover.setImageBitmap(null)

        TrackTitle.setText(trackTitle)
        AlbumTitle.setText(albumTitle)
        if (trackCover != null) {
            val songImage = BitmapFactory
                .decodeByteArray(trackCover, 0, trackCover.size)
            TrackCover.setImageBitmap(songImage)
        }
    }

    private fun fetchSongIndex(playType: PlayTypes): Int {
        var songIndex: Int? = null

        try {
            when (playType) {
                PlayTypes.PlayPreviousSong -> {
                    songIndex = songCount
                    if (currentSong!! != 0) {
                        songIndex = currentSong!!.dec()
                    }
                }
                PlayTypes.PlaySong -> {
                    songIndex = Random.nextInt(0, songCount!!)
                }
                PlayTypes.PlayNextSong -> {
                    songIndex = 0
                    if (currentSong!! != songCount!!) {
                        songIndex = currentSong!!.inc()
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

    private fun loadSongPaths(): MutableList<String> {
        val demoPath = Environment.getExternalStorageDirectory()
        val mfPaths =  MusicFiles(demoPath)
        mfPaths.loadAllMusicPaths()
        val allSongs = mfPaths.allSongs

        return allSongs!!
    }
    private fun loadTracks(allSongs: MutableList<String>) {
        try {
        val trackMgr = TrackManager(allSongs)
        songCount = trackMgr.configureTracks(this)

        songCount = TrackRepository(this).getSongCount()

        currentSong = fetchSongIndex(PlayTypes.PlaySong)
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }


    private var trackPlayer: MediaPlayer? = null
    private var currentSong: Int? = null
    private var playerInitialized: Boolean? = null
    private var songCount: Int? = null

    private enum class PlayTypes {
        PlayNextSong, PlaySong, PlayPreviousSong
    }
}

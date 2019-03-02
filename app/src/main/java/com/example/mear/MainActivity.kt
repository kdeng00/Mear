package com.example.mear

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;

import java.lang.Exception
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlin.io.*
import kotlin.random.Random

import org.jetbrains.anko.*

import com.example.mear.management.MusicFiles
import com.example.mear.management.TrackManager
import com.example.mear.models.Track

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        loadTracks(loadSongPaths())

        initializeMediaPlayer()

        PlayTrack.setOnClickListener {
            playSongTrack()
        }
        NextTrack.setOnClickListener {
            playNextSongTrack()
        }
        PreviousTrack.setOnClickListener {
            playPreviousSongTrack()
        }
        TrackPayer!!.setOnCompletionListener {
            playNextSongTrack()
        }
    }


    private fun initializeMediaPlayer() {
        if (TrackPayer == null) {
            TrackPayer = MediaPlayer()
            playerInitialized = true
            TrackPayer!!.setDataSource(allTracks!![currentSong!!].songPath)
            TrackPayer!!.prepare()
        }
    }

    private fun playSongTrack() {

        try {
            if (!TrackPayer!!.isPlaying) {
                TrackPayer!!.start()
                configureTrackDisplay()


                println("ss")
            } else {
                TrackPayer!!.pause()
            }
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }
    private fun playNextSongTrack() {
        try {
            currentSong = fetchSongIndex(PlayTypes.PlayNextSong)

            if (TrackPayer!!.isPlaying) {
                TrackPayer!!.stop()
            }

            TrackPayer!!.reset()
            TrackPayer!!.setDataSource(allTracks!![currentSong!!].songPath)
            TrackPayer!!.prepare()
            TrackPayer!!.start()
            configureTrackDisplay()
        }
        catch (ex: Exception) {
            val exMsg = ex.message
            print(exMsg)
        }
    }
    private fun playPreviousSongTrack() {
        try {
            currentSong = fetchSongIndex(PlayTypes.PlayPreviousSong)

            if (TrackPayer!!.isPlaying) {
                TrackPayer!!.stop()
            }

            TrackPayer!!.reset()
            TrackPayer!!.setDataSource(allTracks!![currentSong!!].songPath)
            TrackPayer!!.prepare()
            TrackPayer!!.start()
            configureTrackDisplay()
        }
        catch (ex: Exception) {
            val exMsg = ex.message
            print(exMsg)
        }
    }
    private fun configureTrackDisplay() {
        val trackTitle = allTracks!![currentSong!!].title
        val albumTitle = allTracks!![currentSong!!].album
        var trackCover: ByteArray? = null

        if (!(allTracks!![currentSong!!].TrackCover == null)) {
            trackCover = allTracks!![currentSong!!].TrackCover
        }

        TrackTitle.text.clear()
        AlbumTitle.text.clear()
        TrackCover.setImageBitmap(null)

        TrackTitle.setText(trackTitle)
        AlbumTitle.setText(albumTitle)
        if (trackCover != null) {
            var songImage = BitmapFactory
                .decodeByteArray(trackCover, 0, trackCover!!.size)
            TrackCover.setImageBitmap(songImage)
        }
    }

    private fun fetchSongIndex(playType: PlayTypes): Int {
        var songIndex: Int? = null

        try {
            when (playType) {
                PlayTypes.PlayPreviousSong -> {
                    if (currentSong!! == 0) {
                        songIndex = allTracks!!.size - 1
                    } else {
                        songIndex = currentSong!!.dec()
                    }
                }
                PlayTypes.PlaySong -> {
                    songIndex = Random.nextInt(0, allTracks!!.size - 1)
                }
                PlayTypes.PlayNextSong -> {
                    if (currentSong!! == (allTracks!!.size - 1)) {
                        songIndex = 0
                    } else {
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
        val trackMgr = TrackManager(allSongs!!)
        trackMgr.configureTracks()
        allTracks = trackMgr.allTracks

        currentSong = fetchSongIndex(PlayTypes.PlaySong)
    }

    private var TrackPayer: MediaPlayer? = null
    private var allTracks: MutableList<Track>? = null
    private var currentSong: Int? = null
    private var playerInitialized: Boolean? = null

    private enum class PlayTypes {
        PlayNextSong, PlaySong, PlayPreviousSong
    }
}

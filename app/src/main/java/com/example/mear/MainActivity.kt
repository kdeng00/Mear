package com.example.mear

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQuery
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity;

import java.lang.Exception
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.selects.select
import kotlin.io.*
import kotlin.random.Random

import org.jetbrains.anko.db.*

import com.example.mear.management.TrackRepository
import com.example.mear.management.DatabaseManager
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
            val tr = TrackRepository(this).getTrack(currentSong!!)
            TrackPayer!!.setDataSource(tr.songPath)
            TrackPayer!!.prepare()
        }
    }

    private fun playSongTrack() {

        try {
            if (!TrackPayer!!.isPlaying) {
                TrackPayer!!.start()
                configureTrackDisplay()
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
            TrackPayer!!.setDataSource(TrackRepository(this).getTrack(currentSong!!).songPath)
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
            TrackPayer!!.setDataSource(TrackRepository(this).getTrack(currentSong!!).songPath)
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
        val currTrack = TrackRepository(this).getTrack(currentSong!!)
        val trackTitle = currTrack.title
        val albumTitle = currTrack.album
        var trackCover: ByteArray? = null

        if (!(currTrack.TrackCover == null)) {
            trackCover = currTrack.TrackCover
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
                        songIndex = songCount
                    } else {
                        songIndex = currentSong!!.dec()
                    }
                }
                PlayTypes.PlaySong -> {
                    songIndex = Random.nextInt(0, songCount!!)
                }
                PlayTypes.PlayNextSong -> {
                    if (currentSong!! == songCount!!) {
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
        try {

        //var trackMgr = TrackManager(allSongs!!)
        //trackMgr.deleteTable(this)
            //songCount = trackMgr.configureTracks(this)
        //songCount = trackMgr.configureTracks(this)
        //TrackRepository(this).createSongCount(songCount!!)
        //var sC = TrackRepository(this).getSongCount()
        //trackMgr.dumpToDatabase(this)
        //trackMgr.dumpToDatabase()
        //val allTracks = trackMgr.allTracks
        //databaseInit(allTracks!!)
        //databaseInit(trackMgr.allTracks!!)
        //trackMgr = TrackManager( mutableListOf())

        //val obk = TrackRepository(this).getAll()
        //TrackRepository(this).songCount = TrackRepository(this).getAll().size
            songCount = TrackRepository(this).getSongCount()

        currentSong = fetchSongIndex(PlayTypes.PlaySong)
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }

    private fun databaseInit(allTracks: MutableList<Track>) {

        TrackRepository(this).delete()
        for (songData in allTracks) {
            TrackRepository(this).create(songData)
        }
    }

    private var TrackPayer: MediaPlayer? = null
    private var currentSong: Int? = null
    private var playerInitialized: Boolean? = null
    private var songCount: Int? = null

    private enum class PlayTypes {
        PlayNextSong, PlaySong, PlayPreviousSong
    }
}

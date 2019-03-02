package com.example.mear

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.lang.Exception
import java.nio.file.Files
import kotlin.io.*

import com.example.mear.management.MusicFiles
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        loadSongs()

        initializeMediaPlayer()

        PlayTrack.setOnClickListener {
            playSongTrack()
        }
    }


    fun configureSongs() {

    }
    fun configureDemoSong() {
        val sdC = "sdcard0/"
        var musicPath = "music/"
        var artistPath = "Flueric/"
        var albumPath = "New Death of a Phoenix/"
        var songPath = "Qualm.mp3"
        val pa = demoPath!!.absoluteFile.toString() + "/" + musicPath + artistPath + albumPath +
                songPath
        try {
            var fl = File(pa)
            if (fl.exists()) {
                TrackPayer!!.setDataSource(pa)
            }
        }
        catch (ex: Exception) {
            var exMsg = ex.message
        }
    }


    fun initializeMediaPlayer() {
        if (TrackPayer == null) {
            TrackPayer = MediaPlayer()
            playerInitialized = true
            TrackPayer!!.setDataSource(allSongs!![currentSong!!])
            TrackPayer!!.prepare()
        }
    }

    fun playSongTrack() {

        try {
            if (!TrackPayer!!.isPlaying) {
                TrackPayer!!.start()
                var musicData = TrackPayer!!.trackInfo
                println("ss")
            } else {
                TrackPayer!!.pause()
            }
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }


    private fun loadSongs() {
        val mfPaths =  MusicFiles(this.demoPath!!)
        mfPaths.loadAllMusicPaths()
        allSongs = mfPaths.allSongs
        val songIndex = Random.nextInt(0, allSongs!!.size - 1)
        currentSong = songIndex
    }

    private var TrackPayer: MediaPlayer? = null
    private var demoPath: File? =   Environment .getExternalStorageDirectory()
    private var allSongs: MutableList<String>? = null
    private var currentSong: Int? = null
    private var playerInitialized: Boolean? = null
}

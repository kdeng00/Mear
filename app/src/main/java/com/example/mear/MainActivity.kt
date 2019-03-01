package com.example.mear

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import kotlin.io.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initializeMediaPlayer()

        PlayTrack.setOnClickListener {
            playSongTrack()
        }
    }


    fun configureSongs() {

    }

    fun initializeMediaPlayer() {
        if (TrackPayer == null) {
            TrackPayer = MediaPlayer()
        }
        File("/").walkTopDown().forEach { print(it) }
    }

    fun playSongTrack() {

    }


    var TrackPayer: MediaPlayer? = null
    var sysPath = Environment.getRootDirectory()
}

package com.example.mear.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.LinearLayout

import kotlinx.android.synthetic.main.activity_song_view.*
import kotlinx.android.synthetic.main.content_song_view.*

import com.example.mear.R
import com.example.mear.repositories.TrackRepository

class SongViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_view)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}

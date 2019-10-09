package com.example.mear.activities

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView

import kotlinx.android.synthetic.main.activity_icarus_song.*
import kotlinx.android.synthetic.main.content_song_view.*

import com.example.mear.activities.BaseServiceActivity
import com.example.mear.adapters.RecyclerAdapter
import com.example.mear.models.TrackItems
import com.example.mear.models.Song
import com.example.mear.R
import com.example.mear.repositories.TrackRepository


class IcarusSongActivity : BaseServiceActivity() {

    private lateinit var adapter: RecyclerAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private var songs: ArrayList<Song>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_icarus_song)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        try {
            val colr = R.color.track_seek
            window.statusBarColor = resources.getColor(R.color.track_seek)
            doBindService()
            initializeAdapter()
            //initializeSongSearchListener()
        }
        catch (ex: Exception) {
            val msg = ex.message
        }
    }


    private fun initializeAdapter() {
        try {
            linearLayoutManager = LinearLayoutManager(this)
            trackList.layoutManager = linearLayoutManager

            songs!!.sortedWith(compareBy{it.title})
        }
        catch (ex: Exception) {
            val msg = ex.message
        }
    }
}

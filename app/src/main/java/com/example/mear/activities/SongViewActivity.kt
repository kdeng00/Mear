package com.example.mear.activities

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.widget.LinearLayoutManager
import android.view.View

import java.lang.Exception
import kotlinx.android.synthetic.main.activity_song_view.*
import kotlinx.android.synthetic.main.content_song_view.*

import com.example.mear.adapters.RecyclerAdapter
import com.example.mear.models.TrackItems
import com.example.mear.R
import com.example.mear.repositories.TrackRepository

class SongViewActivity : BaseServiceActivity() {

    private var trackListItems = arrayListOf<TrackItems>()
    private lateinit var adapter: RecyclerAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_view)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        try {
            window.statusBarColor = resources.getColor(R.color.track_seek)
            doBindService()
            initializeAdapter()
        }
        catch (ex: Exception) {
            var exMsg = ex.message
        }
    }

    override fun onStart() {
        super.onStart()
        if (trackListItems.size == 0) {
            val df = ""
        }
    }



    @RequiresApi(Build.VERSION_CODES.N)
    private fun initializeAdapter() {
        try {
            linearLayoutManager = LinearLayoutManager(this)
            trackList.layoutManager = linearLayoutManager

            trackListItems = retrieveTrackItems()

            adapter = RecyclerAdapter({trackItem: TrackItems -> playTrack(trackItem)}, trackListItems)
            adapter.configureActivity(this)
            trackList.adapter = adapter
            trackList.setOnClickListener {
                val temp = "dddd"
            }
            trackList.setHasFixedSize(true)
            trackList.setItemViewCacheSize(20);
            trackList.setDrawingCacheEnabled(true);
            trackList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun retrieveTrackItems(): ArrayList<TrackItems> {
        var trackItems: ArrayList<TrackItems>? = null
        try {
            trackItems = arrayListOf()

            val tracks = TrackRepository(this).getAll()

            for (track in tracks) {
                var trackCover = ByteArray(0)
                val trackItem = TrackItems(track.id, track.title, track.artist, trackCover!!)
                trackItems.add(trackItem)
            }
            return trackItems
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }

        return trackItems!!
    }

    fun playTrack(trackItems: TrackItems) {
        val id = trackItems.id
        musicService!!.playTrack(id)
    }
}

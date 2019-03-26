package com.example.mear.activities

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager

import java.lang.Exception
import kotlinx.android.synthetic.main.activity_song_view.*
import kotlinx.android.synthetic.main.content_song_view.*

import com.example.mear.activities.BaseServiceActivity
import com.example.mear.adapters.RecyclerAdapter
import com.example.mear.models.TrackItems
import com.example.mear.R
import com.example.mear.repositories.TrackRepository
import com.example.mear.util.ExtractCover

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

            adapter = RecyclerAdapter(trackListItems, this)
            adapter.configureActivity(this)
            trackList.adapter = adapter
            trackList.setHasFixedSize(true)
            trackList.setItemViewCacheSize(20);
            //trackList.setDrawingCacheEnabled(true);
            //trackList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
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

            var index = 0
            val max = 200
            var done = false
            /**
            tracks.parallelStream().map {track ->
                val cover = ExtractCover(track.songPath)
                var trackCover = ByteArray(0)
                if (cover.hasCover() && !done) {
                    if (index < max ) {
                        trackCover = cover.retrieveCover()
                    }
                    else {
                        done = true
                    }
                    index++
                }
                if (done) {
                    //return trackItems
                }

                val trackItem = TrackItems(track.id, track.title, track.artist, trackCover!!)
                trackItems.add(trackItem)

            }
            */
            for (track in tracks) {
                //val cover = ExtractCover(track.songPath)
                var trackCover = ByteArray(0)
                /**
                if (cover.hasCover() && !done) {
                    if (index < max ) {
                        trackCover = cover.retrieveCover()
                    }
                    else {
                        done = true
                    }
                    index++
                }
                if (done) {
                    return trackItems
                }
                */
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
}

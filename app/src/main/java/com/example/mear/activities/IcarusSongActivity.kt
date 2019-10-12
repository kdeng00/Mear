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
import com.example.mear.adapters.SongAdapter
import com.example.mear.models.TrackItems
import com.example.mear.models.APIInfo
import com.example.mear.models.Song
import com.example.mear.models.Token
import com.example.mear.R
import com.example.mear.repositories.*


class IcarusSongActivity : BaseServiceActivity() {

    private lateinit var adapter: SongAdapter

    private lateinit var linearLayoutManager: LinearLayoutManager

    private var songs: ArrayList<Song>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
        setContentView(R.layout.activity_icarus_song)
        //setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

            val colr = R.color.track_seek
            window.statusBarColor = resources.getColor(R.color.track_seek)
            doBindService()
            initializeAdapter()
            initializeSongSearchListener()
        }
        catch (ex: Exception) {
            val msg = ex.message
        }
    }


    fun playSong(song: Song, token: Token, apiInfo: APIInfo) {
        musicService!!.icarusPlaySong(token, song, apiInfo)
    }


    private fun initializeAdapter() {
        try {
            linearLayoutManager = LinearLayoutManager(this)
            trackList.layoutManager = linearLayoutManager

            val pa = appDirectory()
            val trackRepo = TrackRepository()
            val tokenRepo = TokenRepository()
            val apiRepo = APIRepository()
            val token = tokenRepo.retrieveToken(pa)
            val apiInfo = apiRepo.retrieveRecord(pa)
            val fetchedSongs = trackRepo.fetchSongs(token, apiInfo.uri).toCollection(ArrayList())
            songs = fetchedSongs

            songs!!.sortedWith(compareBy{it.title})

            adapter = SongAdapter({songItems: Song -> playSong(songItems, token, apiInfo)}, songs!!)

            trackList.adapter = adapter
            trackList.setHasFixedSize(true)
            trackList.setItemViewCacheSize(20)
        }
        catch (ex: Exception) {
            val msg = ex.message
        }
    }

    private fun initializeSongSearchListener() {
        songSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                try {
                    adapter.filter.filter(p0)
                }
                catch (ex: Exception) {
                    val msg = ex.message
                }

                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                try {
                    adapter.filter.filter(p0)
                }
                catch (ex: Exception) {
                    val msg = ex.message
                }

                return false
            }
        })
    }
}

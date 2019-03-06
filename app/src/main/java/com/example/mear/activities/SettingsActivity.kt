package com.example.mear.activities

import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity;

import java.lang.Exception
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.content_settings.*

import com.example.mear.R
import com.example.mear.management.MusicFiles
import com.example.mear.management.TrackManager
import com.example.mear.repositories.TrackRepository



class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)

        RefreshLibrary.setOnClickListener {
            updateLibrary()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
            TrackRepository(this).delete()
            val trackMgr = TrackManager(allSongs)
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }



    private fun updateLibrary() {
        loadTracks(loadSongPaths())
    }
}

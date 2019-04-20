package com.example.mear.activities

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast

import java.lang.Exception
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.content_settings.*

import com.example.mear.adapters.SettingsAdapter
import com.example.mear.R
import com.example.mear.management.MusicFiles
import com.example.mear.management.TrackManager
import com.example.mear.ui.popups.AboutPopup
import com.example.mear.repositories.TrackRepository
import kotlinx.android.synthetic.main.popup_layout.*


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)

        var aListener = AboutListener(aboutApp, this)
        About.setOnClickListener(aListener)

        /**
        About.setOnClickListener {
            val i =0
            var j = i.plus(500)
        }
        */

        initialize()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    private fun initialize() {
        window.statusBarColor = resources.getColor(R.color.track_seek)
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
            trackMgr.configureTracks(this)
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }



    private fun updateLibrary() {
        loadTracks(loadSongPaths())
    }

    class AboutListener(var layout: LinearLayout?, val ctx: Context): View.OnClickListener {
        override fun onClick(v: View?) {
            val ss = 10
            var ob = ss.plus(500)
            try {
                val popup = AboutPopup(ctx)
                popup?.showPopupFromScreenCenter(R.layout.activity_settings)
            }
            catch (ex: Exception) {
                val exMsg = ex.message
            }
            Toast.makeText(ctx, "About Setting pressed", Toast.LENGTH_LONG).show()
        }

    }
}

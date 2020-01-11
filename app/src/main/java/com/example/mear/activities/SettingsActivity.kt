package com.example.mear.activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity;
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast

import java.lang.Exception
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.content_settings.*

import com.example.mear.R
import com.example.mear.ui.popups.AboutPopup
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

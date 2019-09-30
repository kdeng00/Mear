package com.example.mear.activities

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.example.mear.R

import kotlinx.android.synthetic.main.activity_demo_stream.*
import kotlinx.android.synthetic.main.content_demo_stream.*

class DemoStreamActivity : AppCompatActivity() {

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }


    external fun test();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_stream)
        setSupportActionBar(toolbar)

        demoStream.setOnClickListener {
            test()
        }

        fab.setOnClickListener {view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

}

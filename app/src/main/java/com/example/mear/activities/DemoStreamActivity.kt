package com.example.mear.activities

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.example.mear.R

import kotlinx.android.synthetic.main.activity_demo_stream.*
import kotlinx.android.synthetic.main.content_demo_stream.*
import org.jetbrains.anko.toast

import com.example.mear.models.Song

class DemoStreamActivity : AppCompatActivity() {

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }


    private var token: String? = null


    private external fun logUser(usr: String, pass: String, api: String): String
    private external fun retrieveSong(tok: String): Song


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_stream)
        setSupportActionBar(toolbar)

        demoStream.setOnClickListener {
            toast("vacant").show()
        }

        login.setOnClickListener {
            loginButton()
        }

        fab.setOnClickListener {view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun loginButton() {
        if (!validFields()) {
            toast("Fields are invalid").show()
            return
        }

        var usernameStr = username.text.toString()
        var passwordStr = password.text.toString()
        var apiUriStr = apiUri.text.toString()

        token = logUser(usernameStr, passwordStr, apiUriStr)
        toast(token!!).show()
        var s = retrieveSong(token!!);
    }

    private fun validFields(): Boolean {
        if (username.text.isEmpty()) {
            return false
        }
        if (password.text.isEmpty()) {
            return false
        }
        if (password.text.isEmpty()) {
            return false
        }

        return true
    }

}

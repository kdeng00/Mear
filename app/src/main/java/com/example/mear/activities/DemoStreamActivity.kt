package com.example.mear.activities

import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.example.mear.R

import kotlinx.android.synthetic.main.activity_demo_stream.*
import kotlinx.android.synthetic.main.content_demo_stream.*
import org.jetbrains.anko.toast

import com.example.mear.models.Song

class DemoStreamActivity : BaseServiceActivity() {

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }


    private var token: String? = null


    // TODO: add external call to retrieve User credentials
    private external fun logUser(usr: String, pass: String, api: String): String
    private external fun retrieveSong(tok: String, api: String): Song
    //private external fun testSongStream(tok: String, id: Int)
    private external fun pathIteratorDemo(path: String)
    private external fun saveUserCredentials(username: String, password: String, appDir: String)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_stream)
        setSupportActionBar(toolbar)

        doBindService()

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

        val saveCred = saveUserCred.isChecked

        val usernameStr = username.text.toString()
        val passwordStr = password.text.toString()
        var apiUriStr = apiUri.text.toString()

        if (apiUriStr.isEmpty()) {
            apiUriStr = ""
        }

        token = logUser(usernameStr, passwordStr, apiUriStr)

        try {

            val s = retrieveSong(token!!, apiUriStr)
            musicService!!.icarusPlaySong(this.applicationContext, token!!, apiUriStr, s)
            val dir = Environment.getExternalStorageDirectory().absolutePath + "/music"
            val appRelPath: String = resources.getString(R.string.app_relative_path)
            var inStoragePath = Environment.getDataDirectory().toString()
            inStoragePath += "/data/$appRelPath"

            if (saveCred) {
                saveUserCredentials(usernameStr, passwordStr, inStoragePath)
            }
        }
        catch (ex: Exception) {
            val msg = ex.message
        }
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

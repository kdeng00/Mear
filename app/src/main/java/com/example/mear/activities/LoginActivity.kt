package com.example.mear.activities

import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import com.example.mear.R

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.content_login.*
import org.jetbrains.anko.toast

import com.example.mear.models.Song
import com.example.mear.models.User

class LoginActivity : BaseServiceActivity() {

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }


    private external fun retrieveSong(tok: String, api: String, songId: Int): Song

    private external fun retrieveUserCredentials(path: String): User

    private external fun logUser(usr: String, pass: String, api: String): String

    private external fun doesDatabaseExist(path: String): Boolean
    private external fun isUserTableEmpty(path: String): Boolean

    private external fun pathIteratorDemo(path: String)
    private external fun saveUserCredentials(username: String, password: String, appDir: String)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)

        val pa = appDirectory()
        if (doesDatabaseExist(pa) && !isUserTableEmpty(pa)) {
            val usr = retrieveUserCredentials(pa)
            username.setText(usr.username)
            password.setText(usr.password)
        }

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

        val token = logUser(usernameStr, passwordStr, apiUriStr)

        try {
            val songId = 4
            val song = retrieveSong(token, apiUriStr, songId)
            val pa = appDirectory()
            if (saveCred && isUserTableEmpty(pa)) {
                saveUserCredentials(usernameStr, passwordStr, pa)
            }
        }
        catch (ex: Exception) {
            val msg = ex.message
        }
    }


    private fun appDirectory(): String {
        return Environment.getDataDirectory().toString() + "/data/" +
                resources.getString(R.string.app_relative_path)
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

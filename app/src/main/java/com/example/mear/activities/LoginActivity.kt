package com.example.mear.activities

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import com.example.mear.R
import com.example.mear.models.*

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.content_login.*
import org.jetbrains.anko.toast

import com.example.mear.repositories.*
import mear.com.example.mear.repositories.APIRepository

//import com.example.mear.repositories.TrackRepository
//import com.example.mear.repositories.UserRepository

class LoginActivity : BaseServiceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)

        loadElements()

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
        var apiInfo = APIInfo(apiUri.text.toString(), 1)
        //var apiUriStr = apiUri.text.toString()
        val usr = User(username.text.toString(), password.text.toString())

        if (apiInfo.uri.isEmpty()) {
            apiInfo.uri = ""
        }

        val usrRepo = UserRepository()
        val trackRepo = TrackRepository()
        val myToken = usrRepo.fetchToken(usr, apiInfo.uri)

        try {
            val pa = appDirectory()
            val so = Song(5)
            val song = trackRepo.fetchSong(myToken, so, apiInfo.uri)
            if (saveCred && usrRepo.isTableEmpty(pa)) {
                val api = APIRepository()
                if (api.isTableEmpty(pa)) {
                    api.SaveRecord(apiInfo, pa)
                }
                usrRepo.saveCredentials(usr, pa)
            }
            //startActivity(Intent(this, IcarusSongActivity::class.java))
        }
        catch (ex: Exception) {
            val msg = ex.message
        }
    }

    private fun loadElements() {
        val pa = appDirectory()
        val usrRepo = UserRepository()
        val apiRepo = APIRepository()
        if (!usrRepo.databaseExist(pa)) {
            return
        }
        if (!usrRepo.isTableEmpty(pa)) {
            val usr = usrRepo.retrieveCredentials(pa)
            username.setText(usr.username)
            password.setText(usr.password)
        }
        if (!apiRepo.isTableEmpty(pa)) {
            val api = apiRepo.retrieveRecord(pa)
            val s: String = "${api.uri}" + "${api.version} ${api.endpoint}"
            apiUri.setText(s)
        }

        doBindService()
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

package com.example.mear.activities

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.content_login.*

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import org.jetbrains.anko.toast

import com.example.mear.models.*
import com.example.mear.R
import com.example.mear.repositories.*

class LoginActivity : BaseServiceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = resources.getColor(R.color.track_seek)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)

        loadElements()

        login.setOnClickListener {
            loginButton()
        }
    }


    private fun loginButton() {
        if (!validFields()) {
            toast("Fields are invalid").show()
            return
        }

        val saveCred = saveUserCred.isChecked
        val apiInfo = APIInfo(apiUri.text.toString(), 1)
        val usr = User(username.text.toString(), password.text.toString())

        val usrRepo = UserRepository()
        val tokenRepo = TokenRepository()
        val myToken = usrRepo.fetchToken(usr, apiInfo.uri)
        if (myToken.accessToken == "failure") {
            Toast.makeText(this, "login failed, try again", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val pa = appDirectory()

            if (saveCred && usrRepo.isTableEmpty(pa)) {
                val api = APIRepository()
                if (api.isTableEmpty(pa)) {
                    api.saveRecord(apiInfo, pa)
                }
                usrRepo.saveCredentials(usr, pa)
            }
            tokenRepo.saveToken(myToken, pa)
            startActivity(Intent(this, MainActivity::class.java))
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
            apiUri.setText(api.uri)
        }

        doBindService()
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

package com.example.mear.activities

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.content_login.*

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import org.jetbrains.anko.toast

import com.example.mear.models.*
import com.example.mear.R
import com.example.mear.repositories.*

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
        val usr = User(username.text.toString(), password.text.toString())

        val usrRepo = UserRepository()
        val tokenRepo = TokenRepository()
        val myToken = usrRepo.fetchToken(usr, apiInfo.uri)

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

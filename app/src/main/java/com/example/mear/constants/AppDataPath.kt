package com.example.mear.constants

import android.os.Bundle
import android.content.res.Resources
import android.os.Environment
import com.example.mear.R

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.content_login.*

object AppDataPath {
    val APP_DATA_PATH: String = Environment.getDataDirectory().toString()
}
package com.example.mear

import android.content.Context

import com.example.mear.management.DatabaseManager

val Context.database:  DatabaseManager
    get() = DatabaseManager.getInstance(applicationContext)

package com.example.mear

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.mear.management.DatabaseManager

val Context.database:  DatabaseManager
    get() = DatabaseManager.getInstance(applicationContext)

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}
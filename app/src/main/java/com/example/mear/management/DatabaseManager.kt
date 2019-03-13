package com.example.mear.management

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.mear.constants.ControlTypes

import java.lang.Exception

import org.jetbrains.anko.db.*
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper

import com.example.mear.models.PlayControls
import com.example.mear.repositories.ShuffleRepository

class DatabaseManager(ctx: Context): ManagedSQLiteOpenHelper(ctx, "Mear",
    null, 1) {
    companion object {
        private var instance: DatabaseManager? = null

        @Synchronized
        fun getInstance(ctx: Context): DatabaseManager {
            if (instance == null) {
                instance = DatabaseManager(ctx.applicationContext)
            }
            return instance!!
        }
    }


    override fun onCreate(db: SQLiteDatabase?) {
        try {
            db!!.createTable(
                "Track", true,
                "Id" to INTEGER + PRIMARY_KEY + UNIQUE,
                "Title" to TEXT, "Album" to TEXT, "Artist" to TEXT,
                "Duration" to INTEGER, "FilePath" to TEXT
            )
            db!!.createTable(
                "TrackCount", true,
                "Id" to INTEGER + PRIMARY_KEY + UNIQUE,
                "TotalSongs" to INTEGER
            )
            db!!.createTable(
                "PlayCount", true,
                "Id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                "PlayCount" to INTEGER,
                "TrackId" to INTEGER
            )
            db!!.createTable(
                "Settings", true,
                "Id" to INTEGER + PRIMARY_KEY + UNIQUE,
                "DarkTheme" to org.jetbrains.anko.db.REAL
            )
            db!!.createTable(
                "Shuffle", true,
                "Id" to INTEGER + PRIMARY_KEY + UNIQUE,
                "Mode" to TEXT
            )
            db!!.createTable(
                "Repeat", true,
                "Id" to INTEGER + PRIMARY_KEY + UNIQUE,
                "Mode" to TEXT)
        }
        catch (ex: Exception) {
        }
        initializeShuffle(db)
        initializeRepeat(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        try {
            db!!.dropTable("Track")
            db!!.dropTable("TrackCount")
            db!!.dropTable("PlayCount")
            db!!.dropTable("Settings")
            db!!.dropTable("Shuffle")
            db!!.dropTable("Repeat")
        }
        catch (ex: Exception) {
        }
    }


    private fun initializeShuffle(db: SQLiteDatabase?) {
        db!!.insert("Shuffle",
            "Mode" to ControlTypes.SHUFFLE_OFF)
    }
    private fun initializeRepeat(db: SQLiteDatabase?) {
        db!!.insert("Repeat",
            "Mode" to ControlTypes.REPEAT_OFF)
    }
}


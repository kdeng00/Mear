package com.example.mear.management

import android.content.Context
import android.database.sqlite.SQLiteDatabase

import org.jetbrains.anko.db.*
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper

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
        //db!!.dropTable("Track")
        db!!.createTable("Track", true,
            "Id" to INTEGER + PRIMARY_KEY + UNIQUE,
            "Title" to TEXT, "Album" to TEXT, "Artist" to TEXT,
            "Cover" to BLOB, "Duration" to INTEGER,
            "FilePath" to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.dropTable("Track")
    }


    private fun createSettingsTable() {
    }
}


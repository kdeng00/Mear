package com.example.mear.repositories

open class BaseRepository {

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }


    private external fun doesDatabaseExist(path: String): Boolean

    fun databaseExist(path: String): Boolean {
        return doesDatabaseExist(path)
    }
}
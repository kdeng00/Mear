package com.example.mear.repositories

import android.content.Context

import com.example.mear.constants.CPPLib

class RepeatRepository(var context: Context?) {

    companion object {
        init {
            System.loadLibrary(CPPLib.NATIVE_LIB)
        }
    }


    private external fun retrieveRepeatMode(path: String): Int

    private external fun updateRepeatMode(path: String)


    fun repeatMode(path: String): RepeatTypes {
        val repeatType = RepeatTypes.valueOf(retrieveRepeatMode(path))

        return repeatType!!
    }


    fun alterRepeatMode(path: String) {
        updateRepeatMode(path)
    }


    enum class RepeatTypes(val value: Int) {
        RepeatSong(0),
        RepeatOff(1);

        companion object {
            fun valueOf(value: Int) = values().find { it.value == value }
        }
    }
}
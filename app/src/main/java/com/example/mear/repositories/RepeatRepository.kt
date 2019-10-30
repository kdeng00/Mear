package com.example.mear.repositories

import android.content.Context

import org.jetbrains.anko.db.*

import com.example.mear.constants.ControlTypes
import com.example.mear.constants.CPPLib
import com.example.mear.database
import com.example.mear.models.PlayControls

class RepeatRepository(var context: Context?) {

    companion object {
        init {
            System.loadLibrary(CPPLib.NATIVE_LIB)
        }
    }


    private external fun retrieveRepeatMode(path: String): Int

    private external fun updateRepeatMode(path: String)


    /**
    fun getRepeatMode(): String = context!!.database.use {
        select("Repeat").limit(1)
            .parseSingle(object: MapRowParser<String>{
                override fun parseRow(columns: Map<String, Any?>): String {

                    return columns.getValue("Mode").toString()
                }
            })
    }

    */

    fun repeatMode(path: String): RepeatTypes {
        val repeatType = RepeatTypes.valueOf(retrieveRepeatMode(path))

        return repeatType!!
    }


    fun alterRepeatMode(path: String) {
        updateRepeatMode(path)
    }


    /**
    fun updateRepeatMode(playControls: PlayControls?) = context!!.database.use {
        var repeatMode = ControlTypes.REPEAT_OFF
        if (playControls!!.repeatOn!!) {
            repeatMode = ControlTypes.REPEAT_ON
        }
        update("Repeat",
            "Mode" to repeatMode).exec()
    }
    */

    enum class RepeatTypes(val value: Int) {
        RepeatSong(0),
        RepeatOff(1);

        companion object {
            fun valueOf(value: Int) = values().find { it.value == value }
        }
    }
}
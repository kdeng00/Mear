package com.example.mear.repositories

import android.content.Context

import org.jetbrains.anko.db.*

import com.example.mear.constants.ControlTypes
import com.example.mear.database
import com.example.mear.models.PlayControls

class RepeatRepository {
   var context: Context? = null


    constructor(context: Context) {
        this.context = context
    }


    fun getRepeatMode(): String = context!!.database.use {
        select("Repeat").limit(1)
            .parseSingle(object: MapRowParser<String>{
                override fun parseRow(columns: Map<String, Any?>): String {

                    return columns.getValue("Mode").toString()
                }
            })
    }

    fun updateRepeatMode(playControls: PlayControls?) = context!!.database.use {
        var repeatMode = ControlTypes.REPEAT_OFF
        if (playControls!!.repeatOn!!) {
            repeatMode = ControlTypes.REPEAT_ON
        }
        update("Repeat",
            "Mode" to repeatMode).exec()
    }
}
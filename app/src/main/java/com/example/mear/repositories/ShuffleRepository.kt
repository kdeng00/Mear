package com.example.mear.repositories

import android.content.Context

import org.jetbrains.anko.db.*

import com.example.mear.constants.ControlTypes
import com.example.mear.database
import com.example.mear.models.PlayControls

class ShuffleRepository {
    private  var context: Context? = null


    constructor(context: Context) {
        this.context = context
    }


    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }
    fun getShuffleMode(): String = context!!.database.use {
        select("Shuffle").limit(1)
            .parseSingle(object : MapRowParser<String> {
                override fun parseRow(columns: Map<String, Any?>): String {

                    return columns.getValue("Mode").toString()
                }
            })
    }

    fun insertShuffleMode(shuffleControls: PlayControls) = context!!.database.use {
        var shuffleMode: String? = ControlTypes.SHUFFLE_OFF
        if (shuffleControls.shuffleOn!!) {
            shuffleMode = ControlTypes.SHUFFLE_ON
        }
        insert("Shuffle",
            "Mode" to shuffleMode)
    }

    fun updateShuffleMode(shuffleControls: PlayControls) = context!!.database.use {
        var shuffleMode: String? = ControlTypes.SHUFFLE_OFF
        if (shuffleControls.shuffleOn!!) {
            shuffleMode = ControlTypes.SHUFFLE_ON
        }
        update("Shuffle",
                        "Mode" to shuffleMode).exec()
    }
}
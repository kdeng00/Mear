package com.example.mear.repositories

import android.content.Context

import org.jetbrains.anko.db.*

import com.example.mear.database
import com.example.mear.models.PlayCount
import com.example.mear.models.Track

class PlayCountRepository(val context: Context) {
    fun getAll(): List<PlayCount> = context.database.use {
        val playCounts = mutableListOf<PlayCount>()

        select("PlayCount" )
            .parseList(object : MapRowParser<PlayCount>{
                override fun parseRow(columns: Map<String, Any?>): PlayCount {
                    val id = columns.getValue("Id").toString().toInt()
                    val songPlayedAmount = columns.getValue("PlayCount").toString().toInt()
                    val trackId = columns.getValue("TrackId").toString().toInt()

                    val playCount = PlayCount(id, songPlayedAmount, trackId)
                    playCounts.add(playCount)

                    return playCount
                }
            })
        playCounts
    }
    fun getPlayCount(id: Int): PlayCount = context.database.use {
        select("PlayCount").where("Id = $id")
            .parseSingle(object: MapRowParser<PlayCount>{
                override fun parseRow(columns: Map<String, Any?>): PlayCount {
                    val id = columns.getValue("Id").toString().toInt()
                    val songPlayedAmount = columns.getValue("PlayCount").toString().toInt()
                    val trackId = columns.getValue("TrackId").toString().toInt()

                    val playCount = PlayCount(id, songPlayedAmount, trackId)

                    return playCount
                }
            })
    }

    fun insertPlayCounts(tracks: List<Track>) = context.database.use {
        transaction {
            var i = 0
            for (track in tracks) {
                insert("PlayCount", "Id" to i++,
                     "PlayCount" to 0, "TrackId" to track.id)
            }
        }
    }

    fun insertPlayCount(track: Track) = context.database.use {
        insert("PlayCount",
            "Id" to track.id,
            "PlayCount" to 0,
            "TrackId" to track.id)
    }
    fun updatePlayCount(playCount: PlayCount) = context.database.use {
        update("PlayCount",
            "PlayCount" to playCount.playCount + 1)
            .where("Id = {playCountId}", "playCountId" to playCount.id).exec()
    }

    fun delete(table: PlayCount?) = context.database.use {
        delete("PlayCount", whereClause = "id = {$table.id}")
    }
    fun delete() = context.database.use {
        delete("PlayCount")
    }
}
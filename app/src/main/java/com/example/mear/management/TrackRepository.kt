package com.example.mear.management

import android.content.Context
import com.example.mear.database

import java.util.ArrayList

import org.jetbrains.anko.db.*

import com.example.mear.management.*
import com.example.mear.models.Track

class TrackRepository(val context: Context) {

    fun getAll(): List<Track> = context.database.use {
        val tracks = mutableListOf<Track>()

        select("Track", "id", "Title")
            .parseList(object : MapRowParser<Track>{
                override fun parseRow(columns: Map<String, Any?>): Track {
                    val id = columns.getValue("Id")
                    val title = columns.getValue("Title")

                    val track = Track(id.toString().toInt(), title.toString(),"","",0,
                        ByteArray(0), "")
                    tracks.add(track)

                    return track
                }
            })
        tracks
    }

    fun create(track: Track) = context.database.use {
        insert("Track",
            "Id" to track.id,
            "Title" to track.title,
            "Album" to track.album,
            "Artist" to track.artist,
            "Duration" to track.length,
            "SongPath" to track.songPath,
            "Cover" to track.TrackCover)
    }

    fun delete(track: Track) = context.database.use {
        delete("Track", whereClause = "id = {$track.id}")
    }
    fun delete() = context.database.use {
        delete("Track")
    }
}
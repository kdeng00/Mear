package com.example.mear.management

import android.content.Context

import org.jetbrains.anko.db.*

import com.example.mear.database
import com.example.mear.models.Track

class TrackRepository(val context: Context) {

    fun getAll(): List<Track> = context.database.use {
        val tracks = mutableListOf<Track>()

        select("Track" )
            .parseList(object : MapRowParser<Track>{
                override fun parseRow(columns: Map<String, Any?>): Track {
                    val id = columns.getValue("Id")
                    val title = columns.getValue("Title")
                    val artist = columns.getValue("Artist").toString()
                    val album = columns.getValue("Album").toString()
                    val duration = columns.getValue("Duration").toString().toInt()
                    val cover = columns.getValue("Cover").toString().toByteArray()
                    val songPath = columns.getValue("FilePath").toString()

                    val track = Track(id.toString().toInt(), title.toString(),artist,album,
                        duration,cover,songPath)
                    tracks.add(track)

                    return track
                }
            })
        sonC = tracks.count()
        tracks
    }
    fun getTrack(id: Int): Track = context.database.use {
        select("Track").where("Id = $id")
            .parseSingle(object: MapRowParser<Track>{
                override fun parseRow(columns: Map<String, Any?>): Track {
                    val id = columns.getValue("Id").toString().toInt()
                    val title = columns.getValue("Title").toString()
                    val artist = columns.getValue("Artist").toString()
                    var album = columns.getValue("Album").toString()
                    val duration = columns.getValue("Duration").toString().toInt()
                    var cover = columns.getValue("Cover").toString().toByteArray()
                    var filePath = columns.getValue("FilePath").toString()

                    val track = Track(id, title, artist, album, duration,
                        cover, filePath)

                    return track
                }
            })
    }
    fun getSongCount(): Int = context.database.use {
        select("TrackCount").limit(1)
            .parseSingle(object : MapRowParser<Int>{
                override fun parseRow(columns: Map<String, Any?>): Int {

                    return columns.getValue("TotalSongs").toString().toInt()
                }
            })
    }

    fun create(track: Track) = context.database.use {
        insert("Track",
            "Id" to track.id,
            "Title" to track.title,
            "Album" to track.album,
            "Artist" to track.artist,
            "Duration" to track.length,
            "FilePath" to track.songPath,
            "Cover" to track.TrackCover)
    }
    fun createSongCount(songCount: Int) = context.database.use {
        insert("TrackCount",
            "Id" to 0,
            "TotalSongs" to songCount)
    }

    fun delete(track: Track) = context.database.use {
        delete("Track", whereClause = "id = {$track.id}")
    }
    fun delete() = context.database.use {
        delete("Track")
    }


    var songCount: Int? = null

    companion object {
        var sonC: Int = 0
    }
}
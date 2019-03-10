package com.example.mear.repositories

import android.content.Context

import org.jetbrains.anko.db.*

import com.example.mear.database
import com.example.mear.models.Track

class TrackRepository {
    private var context: Context? = null

    constructor(context: Context)
    {
        this.context = context
    }

    fun getAll(): List<Track> = context!!.database.use {
        val tracks = mutableListOf<Track>()

        select("Track" )
            .parseList(object : MapRowParser<Track>{
                override fun parseRow(columns: Map<String, Any?>): Track {
                    val id = columns.getValue("Id")
                    val title = columns.getValue("Title")
                    val artist = columns.getValue("Artist").toString()
                    val album = columns.getValue("Album").toString()
                    val duration = columns.getValue("Duration").toString().toInt()
                    val songPath = columns.getValue("FilePath").toString()

                    val track = Track(id.toString().toInt(), title.toString(),artist,album,
                        duration, ByteArray(0),songPath)
                    tracks.add(track)

                    return track
                }
            })
        tracks
    }
    fun getTrack(id: Int): Track = context!!.database.use {
        select("Track").where("Id = $id")
            .parseSingle(object: MapRowParser<Track>{
                override fun parseRow(columns: Map<String, Any?>): Track {
                    val id = columns.getValue("Id").toString().toInt()
                    val title = columns.getValue("Title").toString()
                    val artist = columns.getValue("Artist").toString()
                    var album = columns.getValue("Album").toString()
                    val duration = columns.getValue("Duration").toString().toInt()
                    var filePath = columns.getValue("FilePath").toString()

                    val track = Track(id, title, artist, album, duration,
                        ByteArray(0), filePath)

                    return track
                }
            })
    }
    fun getSongCount(): Int = context!!.database.use {
        select("TrackCount").limit(1)
            .parseSingle(object : MapRowParser<Int>{
                override fun parseRow(columns: Map<String, Any?>): Int {

                    return columns.getValue("TotalSongs").toString().toInt()
                }
            })
    }

    fun insertTrack(track: Track) = context!!.database.use {
        insert("Track",
            "Id" to track.id,
            "Title" to track.title,
            "Album" to track.album,
            "Artist" to track.artist,
            "Duration" to track.length,
            "FilePath" to track.songPath)
    }
    fun createSongCount(songCount: Int) = context!!.database.use {
        delete("TrackCount")
        insert("TrackCount",
            "Id" to 0,
            "TotalSongs" to songCount)
    }
    fun delete() = context!!.database.use {
        delete("Track")
    }

    fun getLibraryCount(): Int? {
        context!!.database.use {
            select("TrackCount").limit(1)
                .parseList (object : MapRowParser<Int> {
                    override fun parseRow(columns: Map<String, Any?>): Int {
                        return columns.getValue("TotalSongs").toString().toInt()
                    }
                })
        }
        return 0
    }
}
package com.example.mear.repositories

import android.content.Context

import org.jetbrains.anko.db.*

import com.example.mear.database
import com.example.mear.models.Song
import com.example.mear.models.Token
import com.example.mear.models.Track

class TrackRepository(var context: Context? = null) {

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }


    private external fun retrieveSongs(token: Token, uri: String): Array<Song>

    private external fun retrieveSong(token: Token, song: Song, uri: String): Song


    fun fetchSongs(token: Token, uri: String): Array<Song> {
        return retrieveSongs(token, uri)
    }


    fun fetchSong(token: Token, song: Song, uri: String): Song {
        return retrieveSong(token, song, uri)
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

    fun insertTracks(tracks: List<Track>) = context!!.database.use {
        transaction {
            for (track in tracks) {
                insert("Track",
                     "Id" to track.id, "Title" to track.title, "Album" to track.album,
                    "Artist" to track.artist, "Duration" to track.length,
                    "FilePath" to track.songPath)
            }
        }
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
    fun deleteTracks(tracks: List<Track>) = context!!.database.use {
        tracks.iterator().forEach {
            transaction {
                delete("Track", "id = {track_id}", "track_id" to it.id)
            }
        }
    }
    fun deleteTracksByPaths(trackPaths: List<String>) = context!!.database.use {
        trackPaths.iterator().forEach {
            transaction {
                delete("Track", "FilePath = {file_path}", "file_path" to it)
            }
        }
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
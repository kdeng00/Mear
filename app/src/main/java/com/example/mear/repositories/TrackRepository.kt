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
    private external fun downloadSong(token: Token, song: Song, uri: String): Song


    fun fetchSongs(token: Token, uri: String): Array<Song> {
        return retrieveSongs(token, uri)
    }


    fun fetchSong(token: Token, song: Song, uri: String): Song {
        return retrieveSong(token, song, uri)
    }

    fun fetchSongFile(token: Token, song: Song, uri: String): Song {
        val downloadedSong = downloadSong(token, song, uri)

        return downloadedSong
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
}
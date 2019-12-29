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
    private external fun retrieveSongsIncludingDownloaded(token: Token, uri: String, path: String): Array<Song>

    private external fun retrieveSong(token: Token, song: Song, uri: String): Song

    private external fun downloadSong(token: Token, song: Song, path: String)


    fun fetchSongs(token: Token, uri: String): Array<Song> {
        return retrieveSongs(token, uri)
    }

    fun fetchSongsIncludingDownloaded(token: Token, uri: String, path: String): Array<Song> {
        return retrieveSongsIncludingDownloaded(token, uri, path)
    }


    fun fetchSong(token: Token, song: Song, uri: String): Song {
        return retrieveSong(token, song, uri)
    }

    fun fetchSongFile(token: Token, song: Song, uri: String) {
        downloadSong(token, song, uri)
    }

    fun download(token: Token, song: Song, path: String) {
        downloadSong(token, song, path)
        val s = 4
    }
}
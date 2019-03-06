package com.example.mear.management

import android.content.Context
import android.media.MediaMetadataRetriever

import java.lang.Exception

import com.example.mear.models.Track
import com.example.mear.repositories.PlayCountRepository
import com.example.mear.repositories.TrackRepository

class TrackManager(var allSongPath: MutableList<String>) {


    fun configureTracks(ctx: Context): Int {
        var id = 0
        try {
            allTracks = mutableListOf()
            for (musicPath in allSongPath) {
                var mmr = MediaMetadataRetriever()
                mmr.setDataSource(musicPath)
                val trackTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                val trackArtist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                val trackAlbum = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                var trackLength: Int? = null
                val trackLenghStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                trackLength = (trackLenghStr.toInt()/1000)

                if (trackTitle == null || trackArtist == null || trackAlbum == null ||
                        trackLenghStr == null) {
                }

                val track = Track(id, trackTitle, trackArtist, trackAlbum, trackLength,
                    ByteArray(0), musicPath)
                dumpToDatabase(ctx, track)
                id++
            }
            TrackRepository(ctx).createSongCount((id -1))
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
        return id.dec()
    }
    private fun dumpToDatabase(ctx: Context, track: Track) {
        TrackRepository(ctx).insertTrack(track)
        PlayCountRepository(ctx).insertPlayCount(track)
    }

    var allTracks: MutableList<Track>? = null
}
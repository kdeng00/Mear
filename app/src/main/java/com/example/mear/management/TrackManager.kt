package com.example.mear.management

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever

import java.lang.Exception

import com.example.mear.models.Track

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

                var art: ByteArray? = null
                if (mmr.embeddedPicture==null) {
                    art = ByteArray(0)
                }
                else {
                    art = mmr.embeddedPicture
                }
                if (trackTitle == null || trackArtist == null || trackAlbum == null ||
                        trackLenghStr == null) {
                    println("dd")
                }

                var track = Track(id, trackTitle, trackArtist, trackAlbum, trackLength,
                    art!!, musicPath)
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
    fun dumpToDatabase(ctx: Context) {
        TrackRepository(ctx).delete()
        for (songData in allTracks!!) {
            TrackRepository(ctx).create(songData)
            allTracks!!.removeAt(songData.id  )
        }
    }
    fun deleteTable(ctx: Context) {
        TrackRepository(ctx).delete()
    }
    private fun dumpToDatabase(ctx: Context, track: Track) {
        TrackRepository(ctx).create(track)
    }

    var allTracks: MutableList<Track>? = null
}
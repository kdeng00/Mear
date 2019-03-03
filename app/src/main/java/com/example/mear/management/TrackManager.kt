package com.example.mear.management

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever

import java.lang.Exception

import com.example.mear.models.Track

class TrackManager(val allSongPath: List<String>) {


    fun configureTracks() {
        try {
            var id = 0
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
                    var songImage = BitmapFactory
                        .decodeByteArray(art, 0, art!!.size)
                }

                var track = Track(id, trackTitle, trackArtist, trackAlbum, trackLength,
                    art!!, musicPath)
                id++
                allTracks!!.add(track)
            }
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }

    var allTracks: MutableList<Track>? = null
}
package com.example.mear.management

import android.content.Context
import android.media.MediaMetadataRetriever

import java.lang.Exception

import com.example.mear.constants.Filenames
import com.example.mear.models.Track
import com.example.mear.repositories.PlayCountRepository
import com.example.mear.repositories.TrackRepository
import com.example.mear.util.ConvertByteArray

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
                if (mmr.embeddedPicture!=null) {
                    saveTrackCoverToDisk(ctx, id, mmr.embeddedPicture)
                }
                id++
            }
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
        TrackRepository(ctx).createSongCount((id ))
        return id.dec()
    }
    private fun dumpToDatabase(ctx: Context, track: Track) {
        TrackRepository(ctx).insertTrack(track)
        PlayCountRepository(ctx).insertPlayCount(track)
    }
    private fun saveTrackCoverToDisk(context: Context, id: Int, trackCover: ByteArray) {
        val filename = "${Filenames.TRACK_COVERS}$id.bmp"
        val fileContents = trackCover
        var img = ConvertByteArray(trackCover).convertToBmp()

        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(fileContents)
        }
    }

    var allTracks: MutableList<Track>? = null
}
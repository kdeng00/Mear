package com.example.mear.util

import android.media.MediaMetadataRetriever

import java.io.FileInputStream

import com.example.mear.models.Track
import java.lang.Exception

class ExtractCover(private val trackPath: String?) {

    fun retrieveCover(): ByteArray {
        var trackData = MediaMetadataRetriever()
        trackData.setDataSource(trackPath)

        return trackData.embeddedPicture
    }
    fun retrieveCover(track: Track): ByteArray? {

        var coverArt: ByteArray? = null

        try {
            if (!hasCover(track)) {
                return null
            }

            val songPath = track.songPath

            val metaData = MediaMetadataRetriever()
            val fp = FileInputStream(songPath)
            metaData.setDataSource(fp.fd)

            coverArt = metaData.embeddedPicture

            fp.close()
            metaData.release()
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }

        return coverArt
    }
    fun retrieveCover(songPath: String): ByteArray? {

        var coverArt: ByteArray? = null

        try {
            if (!hasCover(songPath)) {
                return null
            }


            val metaData = MediaMetadataRetriever()
            val fp = FileInputStream(songPath)
            metaData.setDataSource(fp.fd)

            coverArt = metaData.embeddedPicture

            fp.close()
            metaData.release()
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }

        return coverArt
    }

    fun hasCover(): Boolean {
        var trackData = MediaMetadataRetriever()
        trackData.setDataSource(trackPath)

        if (trackData.embeddedPicture == null) {
            return false
        }

        return true
    }
    fun hasCover(track: Track): Boolean {
        try {
            val trackData = MediaMetadataRetriever()
            val songPath = track.songPath
            val songStream = FileInputStream(songPath)
            trackData.setDataSource(songStream.fd)

            if (trackData.embeddedPicture == null) {
                return false
            }
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }

        return true
    }
    fun hasCover(songPath: String): Boolean {
        try {
            val trackData = MediaMetadataRetriever()
            val songPath = songPath
            val songStream = FileInputStream(songPath)
            trackData.setDataSource(songStream.fd)

            if (trackData.embeddedPicture == null) {
                return false
            }
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }

        return true
    }
}
package com.example.mear.util

import android.media.MediaMetadataRetriever

class ExtractCover(private val trackPath: String) {

    fun retrieveCover(): ByteArray {
        var trackData = MediaMetadataRetriever()
        trackData.setDataSource(trackPath)

        return trackData.embeddedPicture
    }

    fun hasCover(): Boolean {
        var trackData = MediaMetadataRetriever()
        trackData.setDataSource(trackPath)

        if (trackData.embeddedPicture == null) {
            return false
        }

        return true
    }
}
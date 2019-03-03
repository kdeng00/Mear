package com.example.mear.models

data class Track (val id: Int, val title: String, val artist: String, val album: String,
             val length: Int, val TrackCover: ByteArray, val songPath: String) {
    companion object {
        //val Track.COLUMN_ID = "id"
        val TABLE_NAME = "Track"
        val COLUMN_TITLE = "Title"
        val COLUMN_ARTIST = "Artist"
        val COLUMN_ALBUM = "Album"
        val COLUMN_DURATION = "Duration"
        val COLUMN_COVER = "Cover"
        val COLUMN_SONGPATH = "SongPath"
    }
}
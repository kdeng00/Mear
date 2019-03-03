package com.example.mear.models

data class Track (val id: Int, val title: String, val artist: String, val album: String,
             val length: Int, val TrackCover: ByteArray, val songPath: String) {
}
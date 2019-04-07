package com.example.mear.models

data class Track (val id: Int = Int.MAX_VALUE, val title: String = "UNTITLED", val artist: String = "UNTITLED",
                  val album: String = "UNTITLED", val length: Int = Int.MAX_VALUE,
                  val TrackCover: ByteArray = ByteArray(0), val songPath: String = "UNTITLED") {
}
package com.example.mear.management

import java.io.File
import java.lang.Exception

class MusicFiles (private val demoPath:  File) {


    fun loadAllMusicPaths() {
        try {
            allSongs = mutableListOf()
            val demoPath = this.demoPath.absoluteFile.toString() + "/music/"
            val f = File(demoPath)
            var count = 0

            val folder = f
            val listOfFiles = folder.listFiles()

            for (i in listOfFiles) {
                for (j in i.listFiles()) {
                    if (j.absolutePath.toString().contains("zip")) {
                    }
                    for (k in j.listFiles()) {
                        if (k.absolutePath.toString().contains("mp3")) {
                            if (count < musicSongLimit) {
                                allSongs!!.add(k.absolutePath.toString())
                                count++
                            }
                            else
                            {
                                break
                            }
                        }
                    }
                }
            }
        }
        catch (ex: Exception) {
            var exMsg = ex.message
        }
    }


    var allSongs: MutableList<String>?= null
    val musicSongLimit = Int.MAX_VALUE
}
package com.example.mear.management

import android.os.Environment
import java.io.File
import java.lang.Exception
import kotlin.io.*

class MusicFiles (val demoPath:  File) {


    fun loadAllMusicPaths() {
        try {
            allSongs = mutableListOf()
            val demoPath = this.demoPath.absoluteFile.toString() +"/music/"
            val f = File(demoPath)
            var count = 0

            val folder = f
            val listOfFiles = folder.listFiles()

            for (i in listOfFiles) {
                for (j in i.listFiles()) {
                    if (j.absolutePath.toString().contains("zip")) {
                        println("zip file")
                    }
                    for (k in j.listFiles()) {
                        println("What's good?")
                        if (k.absolutePath.toString().contains("mp3")) {
                            allSongs!!.add(k.absolutePath.toString())
                            count++
                        }
                        else {
                            println("Stranger")
                        }
                    }
                }
            }
            songCount = count
        }
        catch (ex: Exception) {
            var exMsg = ex.message
        }
    }
    fun configureDemoSong() {
        val sdC = "sdcard0/"
        var musicPath = "music/"
        var artistPath = "Flueric/"
        var albumPath = "New Death of a Phoenix/"
        var songPath = "Qualm.mp3"
        val pa = demoPath.absoluteFile.toString() + "/" + musicPath + artistPath + albumPath +
                songPath
        try {
            var fl = File(pa)
            if (fl.exists()) {
            }
        }
        catch (ex: Exception) {
            var exMsg = ex.message
        }
    }


    var allSongs: MutableList<String>?= null
    var songCount: Int? = null
}
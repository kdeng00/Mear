package com.example.mear.management

import android.os.Environment
import com.example.mear.R
import java.io.File
import java.lang.Exception

import com.example.mear.constants.DirectoryIgnore
import com.example.mear.constants.FileTypes
import com.example.mear.constants.SongSearch

class MusicFiles (private val demoPath:  File) {

    var allSongs: MutableList<String>?= null

    private val musicSongLimit = Int.MAX_VALUE


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

    fun initialMp3Search() {
        try {
            var pathList: MutableList<String>?
            pathList = mutableListOf()
            var songsAdded = 0

            var fileSearch = File(demoPath.absolutePath)
            fileSearch.walkTopDown().forEach {
                println(it.absolutePath)
                if (!ignoreThisDirectory(it.absolutePath)) {
                    if (it.isFile) {
                        if (it.extension == (FileTypes.Mp3)) {
                            if (songsAdded >= SongSearch.INITIAL_SEARCH_AMOUNT) {
                                return
                            }
                            pathList.add(it.absolutePath)
                            allSongs = pathList
                            songsAdded = songsAdded.inc()
                        }
                    }
                }
            }
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }
    fun searchForMp3Songs() {
        try {
            var pathList: MutableList<String>?
            pathList = mutableListOf()

            var fileSearch = File(demoPath.absolutePath)
            fileSearch.walkTopDown().forEach {
                println(it.absolutePath)
                if (!ignoreThisDirectory(it.absolutePath)) {
                    if (it.isFile) {
                        if (it.extension == (FileTypes.Mp3)) {
                            pathList.add(it.absolutePath)
                        }
                    }
                }
            }

            allSongs = pathList
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }

    private fun ignoreThisDirectory(path: String): Boolean {
        var ignoreDirectory = false
        try {
            if (path.contains(DirectoryIgnore.Android_Root)) {
                ignoreDirectory = true
            }
            if (path.contains(DirectoryIgnore.Notifications)) {
                ignoreDirectory = true
            }
            if (path.contains(DirectoryIgnore.Ringtones)) {
                ignoreDirectory = true
            }
            return ignoreDirectory
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
        return ignoreDirectory
    }
}
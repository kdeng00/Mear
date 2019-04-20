package com.example.mear.management

import android.content.Context
import android.media.MediaMetadataRetriever
import android.widget.Toast

import java.io.FileInputStream
import kotlinx.coroutines.*

import com.example.mear.constants.Filenames
import com.example.mear.constants.SongSearch
import com.example.mear.models.Track
import com.example.mear.repositories.PlayCountRepository
import com.example.mear.repositories.TrackRepository
import com.example.mear.util.ConvertByteArray
import org.jetbrains.anko.custom.async
import kotlin.Exception

class TrackManager(var allSongPath: MutableList<String>?) {

    private var allTracks: MutableList<Track>? = null
    private var ctx: Context? = null
    private var songCount: Int? = null
    private var songIndex: Int? = null
    private var moreSongs = true
    var allTracksAdded = false

    fun initializeContext(context: Context) {
        ctx = context
    }

    fun configureTracks(ctx: Context): Int {
        var id = 0
        try {
            allTracks = mutableListOf()
            for (musicPath in allSongPath!!) {
                var mmr = MediaMetadataRetriever()
                mmr.setDataSource(musicPath)
                val trackTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                val trackArtist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                val trackAlbum = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                var trackLength: Int?
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
        TrackRepository(ctx).createSongCount(id)
        return id.dec()
    }
    fun initializeLibrary() {
        var initTrack = initialTracks()
        addToDatabase(initTrack)
    }
    fun addSongs() {
        var initTracks = initialTracks()
        addToDatabase(initTracks)

        if (moreSongs) {
            GlobalScope.launch {
                val allSongCount = allSongPath!!.size -1
                for (i in songIndex!! .. (allSongCount)) {
                    val songPath = allSongPath!![i]
                    val track = configureTrack(songPath, i)
                    songCount = songCount!!.inc()
                    addToDatabase(track)
                }
            }
        }
    }
    fun addnewSongs(trackPaths: List<String>, resumeIndex: Int) {
        var resumeIndex = resumeIndex

        try {
            //launchB
                for (tPath in trackPaths) {
                    val track = configureTrack(tPath, resumeIndex)
                    addToDatabase(track)
                    resumeIndex = resumeIndex.inc()
                }
            //}
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }
    fun addTracks() {
        var initTracks = initialTracks()
        addToDatabase(initTracks)

        if (moreSongs) {
            GlobalScope.launch {
                val remainingTracks = remainingTracks()
                addToDatabase(remainingTracks)
                allTracksAdded = true
            }
        }
    }
    fun addTracksKx() {
        var initTracks = initialTracks()
        addToDatabase(initTracks)

        if (moreSongs) {
            GlobalScope.launch {
                addRemainingTracks()
            }
        }
    }
    fun addRemainingTracks() = runBlocking {
        val remainder = allSongPath!!.size - songCount!!

        if (remainder > 10) {
            val rangeAmount = remainder / 10
            val rg = addRanges(rangeAmount)

            val remainingT = remainingTracks(rg)
            addToDatabase(remainingT)
        }
    }

    private fun addRanges(rangeAmount: Int): MutableList<AddMusicRange> {
        var ranges = mutableListOf<AddMusicRange>()
        try {
            var rangeCounter = 0
            val songAmount = allSongPath!!.size
            while (songIndex!! < songAmount) {
                val startIndex = songIndex
                var endIndex  = songIndex!! + rangeAmount
                if ((songIndex!! + rangeAmount) > songAmount ) {
                    endIndex = (songAmount - songIndex!!) + songIndex!!
                }

                val range = AddMusicRange(startIndex!!, endIndex)
                ranges.add(range)

                songIndex = endIndex + 1
            }
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }

        return ranges
    }
    private fun initialTracks(): MutableList<Track> {
        var tracks = mutableListOf<Track>()

        try {
            var initSongSearch = SongSearch.INITIAL_SEARCH_AMOUNT -1

            if (allSongPath!!!!.size < SongSearch.INITIAL_SEARCH_AMOUNT) {
                initSongSearch = allSongPath!!.size
                moreSongs = false
            }

            for (i in 0.. initSongSearch) {
                var songPath = allSongPath!![i]
                val track = configureTrack(songPath, i)
                tracks.add(track)
            }

            songCount = tracks.size
            songIndex = songCount!!
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }

        return tracks
    }
    private fun remainingTracks(): MutableList<Track> {
        val tracks = mutableListOf<Track>()
        try {
            for (i in songIndex!! .. (allSongPath!!.size - 1)) {
                val songPath = allSongPath!![i]
                val track = configureTrack(songPath, i)
                if (track.id != Int.MAX_VALUE) {
                    tracks.add(track)
                }
            }

            songCount = songCount!!.plus(tracks.size)
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }

        return tracks
    }
    private fun remainingTracks(rangeobjects: AddMusicRange): MutableList<Track> {
        val tracks  = mutableListOf<Track>()
        try {
            val startIndex = rangeobjects.startIndex
            var endIndex = rangeobjects.endIndex
            if (endIndex == allSongPath!!.size) {
                endIndex--
            }

            for (songPathIndex in startIndex.. endIndex) {
                val songPath = allSongPath!![songPathIndex]
                val track = configureTrack(songPath, songPathIndex)
                if (track.id != Int.MAX_VALUE) {
                    tracks.add(track)
                }
            }

            songCount = songCount!!.plus(tracks.size)
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }

        return tracks
    }
    suspend private fun remainingTracks(obj: MutableList<AddMusicRange>): MutableList<Track> {
        val tracks = mutableListOf<Track>()
        var rangeRecords = mutableListOf<Boolean>()
        try {
            for (rgObj in obj) {
                coroutineScope {
                    launch {
                        val remainingTracks = remainingTracks(rgObj)
                        tracks.addAll(remainingTracks)
                        rangeRecords.add(true)
                    }
                }
            }
            runBlocking {
                while (rangeRecords.size != obj.size) {
                    delay(10)
                }
            }

        }
        catch (ex: java.lang.Exception) {
            val exMsg = ex.message
        }

        return tracks
    }

    private fun configureTrack(songPath: String, id: Int): Track {
        try {
            val metaData = MediaMetadataRetriever()
            val fp = FileInputStream(songPath)
            metaData.setDataSource(fp.fd)

            val title = metaData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            val artist = metaData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
            val album = metaData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
            val duration = (metaData.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toInt()) / 1000

            fp.close()
            metaData.release()

            return Track(id, title, artist, album, duration, ByteArray(0), songPath)
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }

        return Track()
    }

    private fun addToDatabase(tracks: List<Track>) {
        TrackRepository(ctx!!).insertTracks(tracks)
        PlayCountRepository(ctx!!).insertPlayCounts(tracks)
        TrackRepository(ctx!!).createSongCount(songCount!!)
    }
    private fun addToDatabase(track: Track) {
        try {
            TrackRepository(ctx!!).insertTrack(track)
            PlayCountRepository(ctx!!).insertPlayCount(track)
            //TrackRepository(ctx!!).createSongCount(songCount!!)
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
    }
    private fun addToDatabase(ctx: Context, tracks: List<Track>) {
        TrackRepository(ctx).insertTracks(tracks)
        PlayCountRepository(ctx).insertPlayCounts(tracks)
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

    data class AddMusicRange(val startIndex: Int, val endIndex: Int) {
    }
}
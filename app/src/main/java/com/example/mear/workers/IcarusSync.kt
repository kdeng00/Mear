package com.example.mear.workers

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

import com.example.mear.models.Song
import com.example.mear.repositories.TokenRepository
import com.example.mear.repositories.TrackRepository

class IcarusSyncManager(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        val appPath: String = inputData.getString("appPath")!!
        val songId = inputData.getInt("songId", 0)
        if (songId == 0) {
            return Result.failure()
        }

        val tokenRepo = TokenRepository()
        val trackRepo = TrackRepository()
        val tok = tokenRepo.retrieveToken(appPath)
        val song = Song()
        song.id = songId
        song.title = inputData.getString("songTitle")!!
        song.artist = inputData.getString("songArtist")!!
        song.album = inputData.getString("songAlbum")!!
        song.albumArtist = inputData.getString("songAlbumArtist")!!
        song.genre = inputData.getString("songGenre")!!
        song.year = inputData.getInt("songYear", 0)
        song.duration = inputData.getInt("songDuration", 0)
        song.coverArtId = inputData.getInt("songCoverArtId", 0)
        song.disc = inputData.getInt("songDisc", 0)
        song.track = inputData.getInt("songTrack", 0)

        val downloadedSong = trackRepo.download(tok, song, appPath)

        val output: Data = workDataOf("songPath" to downloadedSong.path,
            "songFilename" to downloadedSong.filename,
            "songDownloaded" to true)

        return Result.success(output)
    }
}
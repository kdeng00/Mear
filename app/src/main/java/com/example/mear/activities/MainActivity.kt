package com.example.mear.activities

import android.graphics.Color
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import java.lang.Exception
import java.util.concurrent.TimeUnit
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_play_controls.*
import kotlinx.android.synthetic.main.fragment_track_cover.*
import kotlinx.android.synthetic.main.fragment_track_details.*
import kotlinx.android.synthetic.main.fragment_track_elapsing.*
import kotlinx.android.synthetic.main.fragment_track_flow.*
import kotlin.io.*
import kotlin.random.Random

import com.example.mear.R
import com.example.mear.repositories.TrackRepository
import org.jetbrains.anko.image


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initialize()
    }


    private fun initialize() {
        songCount = TrackRepository(this).getSongCount()
        currentSong = fetchSongIndex(PlayTypes.PlaySong)
        initializeMediaPlayer()

        initializeClickListeners()
        initializeCompletionListener()
    }
    private fun initializeClickListeners() {
        PlayTrack.setOnClickListener {
            playSongTrack()
        }
        NextTrack.setOnClickListener {
            playNextSongTrack()
        }
        PreviousTrack.setOnClickListener {
            playPreviousSongTrack()
        }
        ShuffleTracks.setOnClickListener {
            toggleShuffle()
        }
        SettingsLink.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            try {


                startActivity(intent)
            }
            catch (ex: Exception) {
                val exMsg = ex.message
                println(exMsg)
            }
        }
    }
    private fun initializeCompletionListener() {
        trackPlayer!!.setOnCompletionListener {
            playNextSongTrack()
        }
    }
    private fun initializeMediaPlayer() {
        if (trackPlayer == null) {
            trackPlayer = MediaPlayer()
            playerInitialized = true
            val tr = TrackRepository(this).getTrack(currentSong!!)
            trackPlayer!!.setDataSource(tr.songPath)
            trackPlayer!!.prepare()
        }
    }

    private fun toggleShuffle() {
        val shuffleText = ShuffleTracks.text.toString()
        val on = getString(R.string.shuffle_on)
        val off = getString(R.string.shuffle_off)
        when (shuffleText) {
            on -> {
                shuffleOn = false
                ShuffleTracks.setText(R.string.shuffle_off)
            }
            off -> {
                shuffleOn = true
                ShuffleTracks.setText(R.string.shuffle_on)
            }
            else -> {
                shuffleOn = false
                ShuffleTracks.setText(R.string.shuffle_off)
            }
        }
    }

    private fun playSongTrack() {
        PlayTrack.isEnabled = true
        try {
            if (!trackPlayer!!.isPlaying) {
                trackPlayer!!.start()
            } else {
                trackPlayer!!.pause()
            }
            configureTrackDisplay()
        }
        catch (ex: Exception) {
            val exMsg = ex.message
        }
        PlayTrack.isEnabled = true
    }
    private fun playNextSongTrack() {
        NextTrack.isEnabled = false
        try {
            currentSong = fetchSongIndex(PlayTypes.PlayNextSong)

            if (trackPlayer!!.isPlaying) {
                trackPlayer!!.stop()
            }

            trackPlayer!!.reset()
            trackPlayer!!.setDataSource(TrackRepository(this).getTrack(currentSong!!).songPath)
            trackPlayer!!.prepare()
            trackPlayer!!.start()
            configureTrackDisplay()
        }
        catch (ex: Exception) {
            val exMsg = ex.message
            print(exMsg)
        }
        NextTrack.isEnabled = true
    }
    private fun playPreviousSongTrack() {
        PreviousTrack.isEnabled = false
        try {
            currentSong = fetchSongIndex(PlayTypes.PlayPreviousSong)

            if (trackPlayer!!.isPlaying) {
                trackPlayer!!.stop()
            }

            trackPlayer!!.reset()
            trackPlayer!!.setDataSource(TrackRepository(this).getTrack(currentSong!!).songPath)
            trackPlayer!!.prepare()
            trackPlayer!!.start()
            configureTrackDisplay()
        }
        catch (ex: Exception) {
            val exMsg = ex.message
            print(exMsg)
        }
        PreviousTrack.isEnabled = true
    }
    private fun configureTrackDisplay() {
        configurePlayControlsDisplay()
        val currTrack = TrackRepository(this).getTrack(currentSong!!)
        val trackTitle = currTrack.title
        val artistTitle = currTrack.artist
        val albumTitle = currTrack.album
        val trackDuration = currTrack.length
        var trackCover: ByteArray? = null
        val dur = String.format("%02d:%02d", TimeUnit.SECONDS.toMinutes(trackDuration.toLong()),
            (trackDuration % 60)
            )

        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(currTrack.songPath)

        if (mmr.embeddedPicture != null) {
            trackCover = mmr.embeddedPicture
        }

        TrackTitle.text = null
        ArtistTitle.text = null
        AlbumTitle.text = null
        CurrentPosition.text = null
        TrackCover.setImageBitmap(null)

        TrackTitle.text = trackTitle
        ArtistTitle.text = artistTitle
        AlbumTitle.text = albumTitle
        TrackDuration.text = dur
        if (trackCover != null) {
            val songImage = BitmapFactory
                .decodeByteArray(trackCover, 0, trackCover.size)
            TrackCover.setImageBitmap(songImage)
        }
    }
    private fun configurePlayControlsDisplay() {
        PlayTrack.background = null
        PlayTrack.colorFilter = null

        if (!trackPlayer!!.isPlaying) {
            PlayTrack.setImageResource(android.R.drawable.ic_media_pause)
            PlayTrack.setColorFilter(Color.RED)
        }
        else {
            PlayTrack.setImageResource(android.R.drawable.ic_media_play)
            PlayTrack.setColorFilter(Color.GREEN)
        }
    }

    private fun fetchSongIndex(playType: PlayTypes): Int {
        var songIndex: Int? = null

        try {
            when (playType) {
                PlayTypes.PlayPreviousSong -> {
                    songIndex = songCount
                    if (currentSong!! != 0) {
                        if (!shuffleOn!!) {
                            songIndex = currentSong!!.dec()
                        }
                        else {
                            songIndex = Random.nextInt(0, songCount!!)
                        }
                    }
                }
                PlayTypes.PlaySong -> {
                    songIndex = Random.nextInt(0, songCount!!)
                }
                PlayTypes.PlayNextSong -> {
                    songIndex = 0
                    if (currentSong!! != songCount!!) {
                        if (!shuffleOn!!) {
                            songIndex = currentSong!!.inc()
                        }
                        else {
                            songIndex = Random.nextInt(0, songCount!!)
                        }
                    }
                }
            }
        }
        catch (ex: Exception) {
            val exMsg = ex.message
            println(exMsg)
        }

        return songIndex!!
    }


    private var trackPlayer: MediaPlayer? = null
    private var currentSong: Int? = null
    private var playerInitialized: Boolean? = false
    private var shuffleOn: Boolean? = false
    private var songCount: Int? = null

    private enum class PlayTypes {
        PlayNextSong, PlaySong, PlayPreviousSong
    }
}

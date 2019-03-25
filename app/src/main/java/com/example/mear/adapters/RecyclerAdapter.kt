package com.example.mear.adapters

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

import java.lang.Exception
import kotlinx.android.synthetic.main.fragment_song_view.view.*

import com.squareup.picasso.Picasso

import com.example.mear.activities.SongViewActivity
import com.example.mear.constants.Filenames
import com.example.mear.inflate
import com.example.mear.models.TrackItems
import com.example.mear.R
import com.example.mear.playback.service.MusicService
import com.example.mear.repositories.TrackRepository

class RecyclerAdapter( private  val trackItemsSourceInit: ArrayList<TrackItems>) :
                                                RecyclerView.Adapter<RecyclerAdapter.TrackItemsHolder>() {
    var musicService: MusicService? = null

    override fun getItemCount(): Int {
        return trackItemsSourceInit!!.size
    }

    override fun onBindViewHolder(holder: TrackItemsHolder, position: Int) {
        val itemPhoto = trackItemsSourceInit!![position]
        holder.bindTrackItems(itemPhoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): TrackItemsHolder {
        val inflatedView = parent.inflate(R.layout.fragment_song_view, false)
        return TrackItemsHolder(inflatedView, musicService!!)
    }


    class TrackItemsHolder(v: View, musicService: MusicService) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var musicService: MusicService = musicService
        private var trackItem: TrackItems? = null
        private val imgWidth = 90
        private val imgHeight = 90


        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            try {
                val context = itemView.context
                val showPhotoIntent = Intent(context, SongViewActivity::class.java)
                //showPhotoIntent.putExtra(PHOTO_KEY, trackItem)
                val id = trackItem!!.id
                musicService.playTrack(id)
                context.startActivity(showPhotoIntent)
            }
            catch (ex: Exception) {
                val exMsg = ex.message
            }
        }

        fun bindTrackItems(trackItems: TrackItems) {
            try {
                var context = view.context
                this.trackItem = trackItems
                view.trackTitle.text = trackItem!!.trackTitle
                view.trackArtist.text = trackItem!!.artistTitle
                val id = trackItems.id

                var trackCoverPath = "${context.filesDir}/${Filenames.TRACK_COVERS}"
                trackCoverPath = "${trackCoverPath}${id}.bmp"
                Picasso.get().load(trackCoverPath).into(view.trackCover)
            }
            catch (ex:Exception) {
                val exMsg = ex.message
            }
        }

        companion object {
            private val PHOTO_KEY = "PHOTO"
        }
    }
}
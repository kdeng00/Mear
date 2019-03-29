package com.example.mear.adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

import java.lang.Exception
import kotlinx.android.synthetic.main.fragment_song_view.view.*

import com.squareup.picasso.Picasso

import com.example.mear.constants.Filenames
import com.example.mear.inflate
import com.example.mear.models.TrackItems
import com.example.mear.R


class RecyclerAdapter(val mOnClickListenerI: (TrackItems) -> Unit ,  private  val trackItemsSourceInit: ArrayList<TrackItems>) :
                                                RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    open interface trackItemClickListener{
        fun onClick(view: View)
    }

    override fun getItemCount(): Int {
        return trackItemsSourceInit!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemPhoto = trackItemsSourceInit!![position]
        (holder as TrackItemsHolder).bindTrackItems(itemPhoto, mOnClickListenerI)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        val inflatedView = parent.inflate(R.layout.fragment_song_view, false)
        return TrackItemsHolder(inflatedView)
    }

    fun configureActivity(actvity: Activity) {
    }

    class TrackItemsHolder(v: View) : RecyclerView.ViewHolder(v) {

        private var view: View = v
        private var trackItem: TrackItems? = null
        private val imgWidth = 90
        private val imgHeight = 90
        private var mBound = false



        fun bindTrackItems(trackItems: TrackItems, clickList: (TrackItems) -> Unit) {
            try {
                var context = view.context
                this.trackItem = trackItems
                view.trackTitle.text = trackItem!!.trackTitle
                view.trackArtist.text = trackItem!!.artistTitle
                val id = trackItems.id

                var trackCoverPath = "${context.filesDir}/${Filenames.TRACK_COVERS}"
                trackCoverPath = "${trackCoverPath}${id}.bmp"
                Picasso.get().load(trackCoverPath).into(view.trackCover)
                view.setOnClickListener { clickList(trackItem!!) }
            }
            catch (ex:Exception) {
                val exMsg = ex.message
            }
        }

        companion object {
        }
    }
}
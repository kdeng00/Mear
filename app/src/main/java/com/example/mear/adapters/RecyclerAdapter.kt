package com.example.mear.adapters

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable

import java.lang.Exception
import kotlinx.android.synthetic.main.fragment_song_view.view.*
import kotlinx.coroutines.GlobalScope

import com.squareup.picasso.Picasso

import com.example.mear.constants.Filenames
import com.example.mear.inflate
import com.example.mear.models.TrackItems
import com.example.mear.R
import com.example.mear.util.ConvertByteArray
import com.example.mear.util.ExtractCover
import org.jetbrains.anko.imageBitmap


class RecyclerAdapter(val mOnClickListenerI: (TrackItems) -> Unit,
                      var trackItemsSourceInit: ArrayList<TrackItems>):
                      RecyclerView.Adapter<RecyclerAdapter.TrackItemsHolder>(), Filterable {

    var allTrackItems = trackItemsSourceInit
    val adp = this

    override fun onBindViewHolder(holder: TrackItemsHolder, position: Int) {
        val itemPhoto = trackItemsSourceInit!![position]
        holder.bindTrackItems(itemPhoto, mOnClickListenerI)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): TrackItemsHolder {
        val inflatedView = parent.inflate(R.layout.fragment_song_view, false)
        return TrackItemsHolder(inflatedView)
    }

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val fr = FilterResults()

                val sortedList = mutableListOf<TrackItems>()

                for (trackItm in allTrackItems) {
                    if (trackItm.artistTitle.contains(constraint!!, true) ||
                            trackItm.trackTitle.contains(constraint!!, true)) {
                        sortedList.add(trackItm)
                    }
                }

                fr.count = sortedList.size
                fr.values = sortedList

                return fr
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                try {
                    val itms = results!!.values as ArrayList<TrackItems>

                    adp.trackItemsSourceInit = itms
                }
                catch (ex: Exception) {
                    val exMsg = ex.message
                }

                adp.notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return trackItemsSourceInit!!.size
    }


    fun configureActivity(actvity: Activity) {
    }

    class TrackItemsHolder(v: View) : RecyclerView.ViewHolder(v) {

        private var view: View = v
        private var trackItem: TrackItems? = null
        private val imgWidth = 40
        private val imgHeight = 40
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


                //val extractArt = ExtractCover(null)
                //val coverArt = extractArt.retrieveCover(trackItem!!.trackPath)

                //val convertByte = ConvertByteArray(coverArt)
                //var coverArtBmp = convertByte.convertToBmptScales(imgWidth, imgHeight)

                //view.trackCover.imageBitmap = coverArtBmp
                // TODO: Implement usage of Picasso
                //Picasso.get().load(trackCoverPath).into(view.trackCover)

                view.setOnClickListener { clickList(trackItem!!) }
            }
            catch (ex:Exception) {
                val exMsg = ex.message
            }
        }
    }
}
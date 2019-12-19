package com.example.mear.adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
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
import com.example.mear.models.Song
import com.example.mear.models.TrackItems
import com.example.mear.R
import com.example.mear.util.ConvertByteArray
import com.example.mear.util.ExtractCover
import org.jetbrains.anko.imageBitmap


class SongAdapter(val mOnClickListener: (Song) -> Unit,
                  var songItemSource: ArrayList<Song>) :
    RecyclerView.Adapter<SongAdapter.SongItemHolder>(), Filterable {

    var allIcarusSongs = songItemSource
    val adp = this


    override fun onBindViewHolder(p0: SongItemHolder, p1: Int) {
        val ph = songItemSource!![p1]
        p0.bindSongItems(ph, mOnClickListener)
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SongItemHolder {
        val inflatedView = p0.inflate(R.layout.fragment_song_view, false)
        return SongItemHolder(inflatedView)
    }


    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val fr = FilterResults()
                val sortedList = mutableListOf<Song>()

                for (song in allIcarusSongs) {
                    if (song.artist.contains(constraint!!, true) ||
                            song.title.contains(constraint!!, true)) {
                        sortedList.add(song)
                    }
                }

                fr.count = sortedList.size
                fr.values = sortedList

                return fr
            }


            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                try {
                    val items = results!!.values as ArrayList<Song>
                    adp.songItemSource = items
                }
                catch (ex: Exception) {
                    val msg = ex.message
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return songItemSource!!.size
    }


    class SongItemHolder(var v: View): RecyclerView.ViewHolder(v) {
        private var songItem: Song? = null


        fun bindSongItems(songItems: Song, clickList: (Song) -> Unit) {
            try {
                val context = v.context
                this.songItem = songItems

                v.trackTitle.setText(songItems.title)
                v.trackArtist.setText(songItems.albumArtist)

                v.setOnClickListener { clickList(songItem!!)}
                if (songItems.downloaded) {
                    var rs = v.resources
                    v.setBackgroundColor(v.resources.getColor(R.color.track_seek))
                }
            }
            catch (ex: Exception) {
                val msg = ex.message
            }
        }
    }
}
package com.example.mear.com.example.mear.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.ImageView
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

import com.example.mear.models.TrackItems
import com.example.mear.R

class RecyclerAdapter(private  val context: Context,
                                                    private  val source: ArrayList<TrackItems>): BaseAdapter() {


    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    override fun getCount(): Int {
        return source.size
    }

    //2
    override fun getItem(position: Int): Any {
        return source[position]
    }

    //3
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    //4
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.fragment_song_view, parent, false)

        val trackTitleTextView = rowView.findViewById(R.id.trackTitle) as TextView

// Get subtitle element
        val trackArtistTextView= rowView.findViewById(R.id.trackArtist) as TextView

// Get detail element
        val trackCoverImageView = rowView.findViewById(R.id.trackCover) as ImageView

        val track = getItem(position) as TrackItems

// 2
        trackTitleTextView.text = track.trackTitle
        trackArtistTextView.text = track.artistTitle
        trackCoverImageView.setImageBitmap(BitmapFactory.decodeByteArray(track.trackCover, 0, track.trackCover.size))


        return rowView
    }
}
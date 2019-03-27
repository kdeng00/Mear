package com.example.mear.adapters

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
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
//import android.R



//class RecyclerAdapter(private val mOnClickListenerI: trackItemClickListener,  private  val trackItemsSourceInit: ArrayList<TrackItems>) :
//class RecyclerAdapter(private val mOnClickListenerI: trackItemClickListener,  private  val trackItemsSourceInit: ArrayList<TrackItems>) :
class RecyclerAdapter(val mOnClickListenerI: (TrackItems) -> Unit ,  private  val trackItemsSourceInit: ArrayList<TrackItems>) :
                                                RecyclerView.Adapter<RecyclerView.ViewHolder>()
                                                //Application.ActivityLifecycleCallbacks {
{

    //private val mOnClickListenerI: trackItemClickListener? = null
    private val mOnClick = mOnClickListenerI

    /**
    override fun onActivityPaused(activity: Activity?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityStarted(activity: Activity?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityDestroyed(activity: Activity?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityStopped(activity: Activity?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityResumed(activity: Activity?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    */

    init {
    }

    //var activityInst: Activity? = act
    //var musicService: MusicService? = null

    open interface trackItemClickListener{
        fun onClick(view: View)
    }

    override fun getItemCount(): Int {
        return trackItemsSourceInit!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemPhoto = trackItemsSourceInit!![position]
        //(holder as TrackItemsHolder).bindTrackItems(itemPhoto)
        (holder as TrackItemsHolder).bindTrackItems(itemPhoto, mOnClickListenerI)
    }

//    override fun onCreateViewHolder(parent: ViewGroup, position: Int): TrackItemsHolder {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerView.ViewHolder {
        val inflatedView = parent.inflate(R.layout.fragment_song_view, false)
        //inflatedView.setOnClickListener(parent.parent)
        return TrackItemsHolder(inflatedView)
        //return TrackItemsHolder(inflatedView, mOnClick)
    }

    fun configureActivity(actvity: Activity) {
        //activityInst = actvity
        //activityInst!!.application.registerActivityLifecycleCallbacks(this)
    }

    class TrackItemsHolder(v: View) : RecyclerView.ViewHolder(v) {



        private var view: View = v
        //private var act = activity
        //private var musicService: MusicService? = null
        private var trackItem: TrackItems? = null
        private val imgWidth = 90
        private val imgHeight = 90
        private var mBound = false


        init {
            //v.setOnClickListener(this)
        }

        //constructor(vi: View) : super(this)
        //constructor(vi: View) : super(this)


        /**
        private val connection = object : ServiceConnection {

            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                val binder = service as MusicService.LocalBinder
                musicService = binder.service
                mBound = true
            }

            override fun onServiceDisconnected(arg0: ComponentName) {
                mBound = false
            }
        }
        */


        /**
        override fun onClick(v: View) {
            try {
                //val context = itemView.context
                //val showPhotoIntent = Intent(context, SongViewActivity::class.java)
                //showPhotoIntent.putExtra(PHOTO_KEY, trackItem)
            }
            catch (ex: Exception) {
                val exMsg = ex.message
            }
        }
        */

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
            private val PHOTO_KEY = "PHOTO"
        }
    }
}
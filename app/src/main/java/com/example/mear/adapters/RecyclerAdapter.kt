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

class RecyclerAdapter( private  val trackItemsSourceInit: ArrayList<TrackItems>, private var act: Activity) :
                                                RecyclerView.Adapter<RecyclerAdapter.TrackItemsHolder>(),
                                                Application.ActivityLifecycleCallbacks {
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

    var activityInst: Activity? = act
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
        return TrackItemsHolder(inflatedView, activityInst)
    }

    fun configureActivity(actvity: Activity) {
        activityInst = actvity
        activityInst!!.application.registerActivityLifecycleCallbacks(this)
    }

    class TrackItemsHolder(v: View, activity: Activity?) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var act = activity
        private var musicService: MusicService? = null
        private var trackItem: TrackItems? = null
        private val imgWidth = 90
        private val imgHeight = 90
        private var mBound = false


        init {
            v.setOnClickListener(this)
        }


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


        override fun onClick(v: View) {
            try {
                val context = itemView.context
                val showPhotoIntent = Intent(context, SongViewActivity::class.java)
                //showPhotoIntent.putExtra(PHOTO_KEY, trackItem)
                val id = trackItem!!.id
                //context.startActivity(showPhotoIntent)

                if (!mBound) {
                    val intent = Intent(act, MusicService::class.java)
                    act!!.bindService(intent, connection, Context.BIND_AUTO_CREATE)
                    /**
                    Intent(this, MusicService::class.java).also { intent ->
                        act!!.applicationContext(intent, connection, Context.BIND_AUTO_CREATE)
                        act.applicationContext
                    }
                    */

                }
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
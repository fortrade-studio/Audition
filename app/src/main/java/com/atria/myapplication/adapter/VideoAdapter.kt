package com.atria.myapplication.adapter

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.atria.myapplication.Constants
import com.atria.myapplication.Constants.current
import com.atria.myapplication.Constants.extras
import com.atria.myapplication.R
import com.atria.myapplication.VideoFragment
import com.atria.myapplication.WindowActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class VideoAdapter(
    val videos:List<String>,
    val context:Context,
    val fragment:VideoFragment,
    val isUserProfile:Boolean
) : RecyclerView.Adapter<VideoAdapter.VideoHolder>() {


    companion object{
        private const val TAG = "VideoAdapter"
    }

    class VideoHolder(view: View) : RecyclerView.ViewHolder(view) {
        val videoView = view.findViewById<VideoView>(R.id.videoView)
        val imageView = view.findViewById<ImageView>(R.id.roundedImageView)
    }


    private val number:String? by lazy {
        FirebaseAuth.getInstance().currentUser?.phoneNumber
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder {
        if (viewType == 11 && isUserProfile) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.images_view, parent, false)
            return VideoHolder(view)
        }
        val view = LayoutInflater.from(parent.context).inflate(R.layout.videos_view, parent, false)
        return VideoHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return 11;
        return super.getItemViewType(position)
    }


    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        if (position == 0 && isUserProfile) {
            holder.imageView.setImageResource(R.drawable.ic_upload)
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            Log.i(TAG, "onBindViewHolder: ${videos.toString()}")
            val link = if(isUserProfile) videos[position.minus(1)]
            else videos[position]
            holder.videoView.setVideoURI(Uri.parse(link))
            val random = Random()
            holder.videoView.setOnPreparedListener {
                it.setVolume(0f, 0f)
                it.start()
                holder.videoView.setOnClickListener {
                    val intent = Intent(context, WindowActivity::class.java)
                    intent.putExtra(extras, videos.toTypedArray())
                    intent.putExtra(current, if(isUserProfile) position-1 else position)
                    intent.putExtra(Constants.videos, true)
                    context.startActivity(intent)
                }
            }
        }

    }

    override fun getItemCount(): Int {
            if(isUserProfile) {
                return videos.size+1;
            }else{
                return videos.size
            }
        }

}
package com.atria.myapplication.adapter

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.atria.myapplication.Constants
import com.atria.myapplication.Constants.current
import com.atria.myapplication.Constants.extras
import com.atria.myapplication.R
import com.atria.myapplication.WindowActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class VideoAdapter(
    val videos:List<String>,
    val context:Context
) : RecyclerView.Adapter<VideoAdapter.VideoHolder>() {

    class VideoHolder(view:View) : RecyclerView.ViewHolder(view){
        val videoView = view.findViewById<VideoView>(R.id.videoView)
        val imageView = view.findViewById<ImageView>(R.id.roundedImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder {
        if(viewType==11){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.images_view,parent,false)
            return VideoHolder(view)
        }
        val view = LayoutInflater.from(parent.context).inflate(R.layout.videos_view,parent,false)
        return VideoHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        if(position==0) return 11;
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        if(position==0){
            holder.imageView.setImageResource(R.drawable.ic_upload)
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            val link = videos[position]
            holder.videoView.setVideoURI(Uri.parse(link))
            val random = Random()
            holder.videoView.setOnPreparedListener {
                it.setVolume(0f, 0f)
                it.start()
                it.seekTo(random.nextInt(1_00_000))
                it.pause()
                holder.videoView.setOnClickListener {
                    val intent = Intent(context, WindowActivity::class.java)
                    intent.putExtra(extras, videos.toTypedArray())
                    intent.putExtra(current, position)
                    intent.putExtra(Constants.videos, true)
                    context.startActivity(intent)
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return videos.size;
    }

}
package com.atria.myapplication.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.atria.myapplication.R
import com.atria.myapplication.diffutils.SearchDiffUtils
import com.atria.myapplication.diffutils.VideoData
import com.atria.myapplication.diffutils.VideoDiffUtils

class HomeAdapter(
    val list : ArrayList<VideoData>
) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    inner class HomeViewHolder(view:View) : RecyclerView.ViewHolder(view){
        val videoView = view.findViewById<VideoView>(R.id.videoView)
    }

    fun updateList(newList:List<VideoData>){
        val diff = DiffUtil.calculateDiff(VideoDiffUtils(newList, list))
        list.clear()
        list.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shorts_view,parent,false)
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.videoView.setVideoURI(Uri.parse(list[position].link))
        holder.videoView.setOnPreparedListener {
            it.start()
        }
        holder.videoView.setOnCompletionListener { it.start() }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
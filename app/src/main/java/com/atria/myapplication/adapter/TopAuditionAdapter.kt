package com.atria.myapplication.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.atria.myapplication.HomeFragment
import com.atria.myapplication.R
import com.atria.myapplication.viewModel.home.HomeFragmentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

class TopAuditionAdapter(
    val list: List<HomeFragmentViewModel.Top>
) : RecyclerView.Adapter<TopAuditionAdapter.TopAuditionViewHolder>(){

    class TopAuditionViewHolder(view:View) : RecyclerView.ViewHolder(view){
        val profileImageView = view.findViewById<ImageView>(R.id.profileImageView)
        val usernameTextView = view.findViewById<TextView>(R.id.topUserName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopAuditionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.top_performer,parent,false)
        return TopAuditionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopAuditionViewHolder, position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = URL(list[position].profileImage)
            val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            CoroutineScope(Dispatchers.Main).launch {
                holder.profileImageView.setImageBitmap(bmp)
                holder.usernameTextView.text = list[position].username
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
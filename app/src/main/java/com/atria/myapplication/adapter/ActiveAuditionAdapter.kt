package com.atria.myapplication.adapter

import android.graphics.ImageDecoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.atria.myapplication.R
import com.atria.myapplication.viewModel.home.HomeFragmentViewModel
import com.bumptech.glide.Glide

class ActiveAuditionAdapter(
    val list: List<HomeFragmentViewModel.Active>,
    val fr : Fragment,
    val size : Int=1
) : RecyclerView.Adapter<ActiveAuditionAdapter.ActiveAuditionViewHolder>() {

    inner class ActiveAuditionViewHolder(view : View):RecyclerView.ViewHolder(view){
        val companyProfileImageView = view.findViewById<ImageView>(R.id.companyProfileImageView)
        val companyNameTextView = view.findViewById<TextView>(R.id.companyNameTextView)
        val locationTextView = view.findViewById<TextView>(R.id.locationTextView)
        val positionTextView = view.findViewById<TextView>(R.id.positionTextView)
        val detailTextView = view.findViewById<TextView>(R.id.detailTextView)
        val bookMarkButton = view.findViewById<ImageView>(R.id.saveImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveAuditionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.audition_active,parent,false)
        return ActiveAuditionViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActiveAuditionViewHolder, position: Int) {
        Glide.with(fr)
            .load(list[position].image)
            .into(holder.companyProfileImageView)

        holder.companyNameTextView.text = list[position].company
        holder.locationTextView.text = list[position].location
        holder.positionTextView.text = list[position].position
        holder.detailTextView.text = list[position].details

        holder.bookMarkButton.setOnClickListener {
            if(holder.bookMarkButton.tag == "unsaved") {
                holder.bookMarkButton.setImageResource(R.drawable.ic_baseline_bookmark_24)
                holder.bookMarkButton.tag = "saved"
            }else{
                holder.bookMarkButton.setImageResource(R.drawable.ic_baseline_bookmark_border_24)
                holder.bookMarkButton.tag = "unsaved"
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

}
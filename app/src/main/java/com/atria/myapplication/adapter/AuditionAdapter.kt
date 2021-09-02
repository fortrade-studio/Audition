package com.atria.myapplication.adapter

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

class AuditionAdapter(
    val list:List<HomeFragmentViewModel.Audition>,
    val fragment: Fragment
) : RecyclerView.Adapter<AuditionAdapter.AuditionViewHolder>() {

    inner class AuditionViewHolder(view :  View) : RecyclerView.ViewHolder(view){
        val companyProfileImageView = view.findViewById<ImageView>(R.id.companyProfileImageView)
        val positionTextView = view.findViewById<TextView>(R.id.positionTextView)
        val companyTextView = view.findViewById<TextView>(R.id.companyNameTextView)
        val locationTextView = view.findViewById<TextView>(R.id.locationTextView)
        val bookMarkButton = view.findViewById<ImageView>(R.id.saveImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuditionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.audition_openings,parent,false)
        return AuditionViewHolder(view)
    }

    override fun onBindViewHolder(holder: AuditionViewHolder, position: Int) {
        Glide.with(fragment)
            .load(list[position].image)
            .into(holder.companyProfileImageView)

        holder.positionTextView.text = list[position].position
        holder.companyTextView.text = list[position].company
        holder.locationTextView.text = list[position].location

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
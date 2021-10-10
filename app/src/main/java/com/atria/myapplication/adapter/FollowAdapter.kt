package com.atria.myapplication.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.atria.myapplication.R
import com.atria.myapplication.databinding.SearchLayoutBinding
import com.atria.myapplication.viewModel.follow.FollowingViewModel
import com.bumptech.glide.Glide

class FollowAdapter(
    val following:List<FollowingViewModel.Following>,
    val context:Context,
    val view :View
) : RecyclerView.Adapter<FollowAdapter.FollowViewHolder>() {

    private lateinit var followAdapterBinding : SearchLayoutBinding

    inner class FollowViewHolder(view:View):RecyclerView.ViewHolder(view){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowViewHolder {
        followAdapterBinding = SearchLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return FollowViewHolder(followAdapterBinding.root)
    }

    override fun onBindViewHolder(holder: FollowViewHolder, position: Int) {
        followAdapterBinding.usernameTextView.text = following[position].Users
        followAdapterBinding.nameTextView.text = following[position].name
        Glide.with(context)
            .load(following[position].img)
            .into(followAdapterBinding.circleImageView)

        followAdapterBinding.root.setOnClickListener {
            val args = Bundle()
            args.putString("ph",following[position].userId)
            Navigation.findNavController(view).navigate(R.id.action_followingFragment_to_profileFragment,args)
        }
    }

    override fun getItemCount(): Int {
        return following.size
    }

}
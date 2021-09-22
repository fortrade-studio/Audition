package com.atria.myapplication.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.atria.myapplication.Constants
import com.atria.myapplication.Constants.circular
import com.atria.myapplication.Constants.links
import com.atria.myapplication.Constants.user
import com.atria.myapplication.Constants.viewdata
import com.atria.myapplication.R
import com.atria.myapplication.diffutils.SearchDiffUtils
import com.atria.myapplication.viewModel.search.SearchFragmentViewModel
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IndexOutOfBoundsException

class SearchAdapter(
    val list: ArrayList<SearchFragmentViewModel.SearchUser>,
    val fragment: Fragment
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    inner class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val searchBox = view.findViewById<ConstraintLayout>(R.id.searchBox)
        val circularProfileImage = view.findViewById<ImageView>(R.id.circleImageView)
        val usernameTextView = view.findViewById<TextView>(R.id.usernameTextView)
        val nameTextView = view.findViewById<TextView>(R.id.nameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.search_layout, parent, false)
        return SearchViewHolder(view)
    }

    fun updateArrayList(newList: ArrayList<SearchFragmentViewModel.SearchUser>) {
        val diff = DiffUtil.calculateDiff(SearchDiffUtils(newList, list))
        list.clear()
        list.addAll(newList)
        diff.dispatchUpdatesTo(this)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {

        holder.searchBox.setOnClickListener {
            try {
                Constants.homeToProfileCallback.postValue(list[position].phNumber)
            } catch (e: Exception) {
                Constants.homeToProfileCallback.postValue(list[0].phNumber)
            }
        }

        holder.usernameTextView.text = list[position].username
        holder.nameTextView.text = list[position].name

        loadImageOfUser(position) {
            Glide.with(fragment)
                .load(it)
                .into(holder.circularProfileImage)
        }
    }

    private fun loadImageOfUser(position: Int, onSuccess: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            FirebaseFirestore.getInstance()
                .collection(user)
                .document(list[position].phNumber)
                .collection(viewdata)
                .document(links)
                .get()
                .addOnSuccessListener {
                    val circular = it.get(circular) as String
                    onSuccess(circular)
                }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}
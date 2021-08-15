package com.atria.myapplication.viewModel.video

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import com.atria.myapplication.Constants.links
import com.atria.myapplication.Constants.profile_id
import com.atria.myapplication.Constants.user
import com.atria.myapplication.Constants.videos
import com.atria.myapplication.Constants.viewdata
import com.google.firebase.firestore.FirebaseFirestore

class VideoFragmentViewModel(
    val context: Context,
    val view: View
) : ViewModel() {

    fun getVideos(onSuccess: (ArrayList<String>) -> Unit) {
        firebase.collection(user)
            .document(profile_id)
            .collection(viewdata)
            .document(videos)
            .get()
            .addOnSuccessListener {
                val videos = it.get(links) as ArrayList<String>
                onSuccess(videos)
            }
    }

    companion object {
        val firebase = FirebaseFirestore.getInstance()
    }

}
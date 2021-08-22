package com.atria.myapplication.viewModel.video

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import com.atria.myapplication.Constants
import com.atria.myapplication.Constants.links
import com.atria.myapplication.Constants.profile_id
import com.atria.myapplication.Constants.user
import com.atria.myapplication.Constants.videos
import com.atria.myapplication.Constants.viewdata
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList

class VideoFragmentViewModel(
    val context: Context,
    val view: View
) : ViewModel() {

    private lateinit var videos:ArrayList<String>

    fun getVideos(onSuccess: (ArrayList<String>) -> Unit) {
        firebase.collection(user)
            .document(profile_id)
            .collection(viewdata)
            .document(Constants.videos)
            .get()
            .addOnSuccessListener {
                videos = it.get(links) as ArrayList<String>
                onSuccess(videos)
            }
    }

    fun uploadVideo(uri:Uri){
        storage.getReference("users/videos/${auth.currentUser?.phoneNumber}/")
            .child(System.currentTimeMillis().toString())
            .putFile(uri)
            .addOnSuccessListener {
                it.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                    firebase.collection(user)
                        .document(auth.currentUser?.phoneNumber!!)
                        .collection(viewdata)
                        .document(Constants.videos)
                        .set(mapOf(Pair(links,videos.apply { add(it.toString()) })))
                        .addOnSuccessListener {
                            Log.i(TAG, "uploadVideo: DONE VIDEO")
                        }
                }
            }

    }

    companion object {
        private const val TAG = "VideoFragmentViewModel"
        val firebase = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val storage = FirebaseStorage.getInstance()
    }

}
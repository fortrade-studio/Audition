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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList

class VideoFragmentViewModel(
    val context: Context,
    val view: View
) : ViewModel() {

    private var videos: ArrayList<String> = ArrayList()

    fun getVideos(onSuccess: (ArrayList<String>) -> Unit) {
        firebase.collection(user)
            .document(profile_id)
            .collection(viewdata)
            .document(Constants.videos)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    videos = it.get(links) as ArrayList<String>
                    onSuccess(videos)
                } else {
                    onSuccess(arrayListOf())
                }
            }
    }

    fun uploadVideo(uri: Uri,onSuccess: () -> Unit,onFailed:()->Unit) {
        storage.getReference("users/videos/${auth.currentUser?.phoneNumber}/")
            .child(System.currentTimeMillis().toString())
            .putFile(uri)
            .addOnSuccessListener {
                it.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                    firebase.collection(user)
                        .document(auth.currentUser?.phoneNumber!!)
                        .collection(viewdata)
                        .document(Constants.videos)
                        .set(mapOf(Pair(links, videos.apply { add(it.toString()) })))
                        .addOnSuccessListener {v->
                            database.getReference("videos")
                                .child(auth.currentUser?.phoneNumber.toString()+videos.size)
                                .setValue(mapOf(Pair("link",it.toString()),Pair("likes",0),Pair("userid",
                                    auth.currentUser?.phoneNumber)),DatabaseReference.CompletionListener { error, ref ->
                                    if(error == null){
                                        onSuccess()
                                        Log.i(TAG, "uploadVideo: DONE VIDEO")
                                    }else{
                                        onFailed()
                                    }
                                })

                        }
                }
            }

    }

    companion object {
        private const val TAG = "VideoFragmentViewModel"
        val firebase = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val storage = FirebaseStorage.getInstance()
        val database = FirebaseDatabase.getInstance()

    }

}
package com.atria.myapplication.viewModel.profile

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import com.atria.myapplication.Constants
import com.atria.myapplication.Constants.big
import com.atria.myapplication.Constants.circular
import com.atria.myapplication.Constants.follow
import com.atria.myapplication.Constants.following
import com.atria.myapplication.Constants.links
import com.atria.myapplication.Constants.name
import com.atria.myapplication.Constants.summary
import com.atria.myapplication.Constants.user
import com.atria.myapplication.Constants.username
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragmentViewModel(
    val context:Context,
    val view:View
): ViewModel() {

    companion object{
        private val firebase = FirebaseFirestore.getInstance()
    }

    data class ViewData(
        val big:String="",
        val circular:String="",
        val follower:Int=0,
        val following:Int=0,
        val name:String="",
        val username:String="",
        val summary:String=""
    )

    fun getUserData(ph:String,onSuccess:(ViewData)->Unit){
        firebase.collection(Constants.user)
            .document(ph)
            .collection(Constants.viewdata)
            .document(links)
            .get()
            .addOnSuccessListener {
                val viewData = it.toObject(ViewData::class.java)
                if(viewData != null) {
                    onSuccess(viewData)
                }
            }
            .addOnFailureListener {

            }
    }

}
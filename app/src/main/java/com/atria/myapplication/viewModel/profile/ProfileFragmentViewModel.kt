package com.atria.myapplication.viewModel.profile

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
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
import com.atria.myapplication.Constants.user_id
import com.atria.myapplication.Constants.username
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragmentViewModel(
    val context: Context,
    val view: View
) : ViewModel() {

    companion object {
        private val firebase = FirebaseFirestore.getInstance()
        private val user_name = FirebaseAuth.getInstance().currentUser?.phoneNumber
    }

    // if following then follow live says true else false
    val followLive = MutableLiveData<Boolean>(false)
    val onFollowFetch = MutableLiveData<Long>()

    data class ViewData(
        val big: String = "",
        val circular: String = "",
        val follower: Int = 0,
        val following: Int = 0,
        val name: String = "",
        val username: String = "",
        val summary: String = ""
    )

    fun checkIfFollow(ph: String) {
        try {
            firebase.collection(user)
                .document(user_name!!)
                .collection("Following")
                .document(ph)
                .get()
                .addOnSuccessListener {
                    followLive.postValue(it.exists())
                }
        }catch (e : Exception){
            followLive.postValue(false)
        }

    }


    fun unFollowUser(ph:String,onSuccess: () -> Unit){
        firebase.collection(user)
            .document(user_name!!)
            .collection("Following")
            .document(ph)
            .delete()
            .addOnSuccessListener {
                firebase.collection(user)
                    .document(ph)
                    .collection("ViewData")
                    .document("links")
                    .update("follower",FieldValue.increment(-1))
                    .addOnSuccessListener {
                        getFollowData(ph)
                        onSuccess()
                    }
            }
    }

    fun followUser(ph: String, username: String, onSuccess: () -> Unit) {
        if (followLive.value == false) {
            // you can follow
            firebase.collection(user)
                .document(user_name!!)
                .collection("Following")
                .document(ph)
                .set(mapOf(Pair(user, username),Pair("userId",ph)))
                .addOnSuccessListener {
                    firebase.collection(user)
                        .document(ph)
                        .collection("ViewData")
                        .document("links")
                        .update("follower",FieldValue.increment(1))
                        .addOnSuccessListener {
                            getFollowData(ph)
                            onSuccess()
                        }
                }
        }

    }

    fun getFollowData(ph:String){
        firebase.collection(user)
            .document(ph)
            .collection("ViewData")
            .document("links")
            .get()
            .addOnSuccessListener {
                onFollowFetch.postValue(it.get("follower") as Long)
            }
    }

    fun getFollowing(id:String,onFetched:(Int)->Unit){
        firebase.collection(user)
            .document(id)
            .collection("Following")
            .get()
            .addOnSuccessListener {
                onFetched(it.size())
            }
    }

    fun getUserData(ph: String, onSuccess: (ViewData) -> Unit) {
        firebase.collection(Constants.user)
            .document(ph)
            .collection(Constants.viewdata)
            .document(links)
            .get()
            .addOnSuccessListener {
                val viewData = it.toObject(ViewData::class.java)
                if (viewData != null) {
                    onSuccess(viewData)
                }
            }
            .addOnFailureListener {

            }
    }

}
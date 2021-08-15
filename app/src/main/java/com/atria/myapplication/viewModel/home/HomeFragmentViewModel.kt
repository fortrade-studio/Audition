package com.atria.myapplication.viewModel.home

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.atria.myapplication.Constants.message
import com.atria.myapplication.Constants.notset
import com.atria.myapplication.Constants.top
import com.atria.myapplication.Constants.user
import com.atria.myapplication.Constants.username
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class HomeFragmentViewModel
    (val view:View,val context : Context):
    ViewModel(){

    companion object{
        private val phNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber
        private val firebase = FirebaseFirestore.getInstance()
        private const val TAG = "HomeFragmentViewModel"
    }

    data class Top(
        val username:String="",
        val profileImage:String=""
    )

    var liveTopScorer : MutableLiveData<List<Top>> = MutableLiveData()


    fun getTopAuditionData() {
        firebase.collection(top)
            .get()
            .addOnSuccessListener {
                val top = it.toObjects(Top::class.java)
                Log.i(TAG, "getTopAuditionData: success")
                liveTopScorer.postValue(top)
            }
            .addOnFailureListener {
                Log.e(TAG, "getTopAuditionData: ",it )
                Log.i(TAG, "getTopAuditionData: failed")
            }
    }

}
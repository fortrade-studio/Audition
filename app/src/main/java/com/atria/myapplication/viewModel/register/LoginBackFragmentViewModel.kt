package com.atria.myapplication.viewModel.register

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import com.atria.myapplication.room.User
import com.google.firebase.firestore.FirebaseFirestore

class LoginBackFragmentViewModel(
    val context:Context,
    val view: View
) : ViewModel() {

    private val fb = FirebaseFirestore.getInstance()

    companion object{
        private const val TAG = "LoginBackFragmentViewMo"
        const val userString = "Users"
    }

    fun checkForUsername(ph:String, isSettled:(Boolean)->Unit,onFailure:()->Unit){
        fb.collection(userString)
            .document(ph)
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                if(user?.username=="not set"){
                    // username is not set
                    isSettled(false)
                }else{
                    isSettled(true)
                }
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    fun verifyNumber(ph:String,onVerified:(Boolean)->Unit , onFailure: () -> Unit){
        fb.collection(userString)
            .document(ph)
            .get()
            .addOnSuccessListener {
                if(!it.exists()){
                    onVerified(false)
                }else{
                    onVerified(true)
                }
            }
            .addOnFailureListener {
                onFailure()
            }
    }

}
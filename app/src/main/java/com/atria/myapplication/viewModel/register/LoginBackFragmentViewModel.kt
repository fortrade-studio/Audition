package com.atria.myapplication.viewModel.register

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class LoginBackFragmentViewModel(
    val context:Context,
    val view: View
) : ViewModel() {

    private val fb = FirebaseFirestore.getInstance()

    companion object{
        const val userString = "Users"
    }

    fun verifyNumber(ph:String,onVerified:(Boolean)->Unit){
        fb.collection(userString)
            .whereEqualTo("phNumber",ph)
            .get()
            .addOnSuccessListener {
                if(it.isEmpty){
                    onVerified(false)
                }else{
                    onVerified(true)
                }
            }
            .addOnFailureListener {
                onVerified(true)
            }
    }

}
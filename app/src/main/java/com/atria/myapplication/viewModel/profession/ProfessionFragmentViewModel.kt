package com.atria.myapplication.viewModel.profession

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import com.atria.myapplication.Constants.user
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfessionFragmentViewModel(
    val activity:Activity
):ViewModel() {

    companion object{
        private val firebase = FirebaseFirestore.getInstance()
        private val auth = FirebaseAuth.getInstance().currentUser!!
    }

    fun checkRecord():Int{
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return -1
        val highScore = sharedPref.getInt("saved", -1)
        return highScore
    }

    fun uploadArtistType(value:String,onSuccessListener: ()->Unit){

        firebase.collection(user)
            .document(auth.phoneNumber!!)
            .update("artistType",value)
            .addOnSuccessListener {
                onSuccessListener()
            }

    }

}
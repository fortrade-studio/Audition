package com.atria.myapplication.viewModel.topic

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import com.atria.myapplication.Constants
import com.atria.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TopicFragmentViewModel(
    val activity:Activity,
):ViewModel() {

    companion object{
        private val firebase = FirebaseFirestore.getInstance()
        private val auth = FirebaseAuth.getInstance().currentUser!!
    }

    private fun saveRecord(){
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putInt("saved", 0)
            apply()
        }
    }

    fun uploadAuditionType(value:String,onSuccessListener: ()->Unit){
        firebase.collection(Constants.user)
            .document(auth.phoneNumber!!)
            .update("auditionType",value)
            .addOnSuccessListener {
                saveRecord()
                onSuccessListener()
            }

    }

}
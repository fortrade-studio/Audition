package com.atria.myapplication.viewModel.about

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import com.atria.myapplication.Constants
import com.atria.myapplication.Constants.profile_id
import com.atria.myapplication.Constants.user
import com.atria.myapplication.room.User
import com.google.firebase.firestore.FirebaseFirestore

class AboutFragmentViewModel(
    val context:Context,
    val view:View
) :ViewModel(){

    private var user : User? = null


    companion object{
        private val firebase = FirebaseFirestore.getInstance()
    }


    fun getPersonalData(onSuccess:(User)->Unit){
        firebase.collection(Constants.user)
            .document(profile_id)
            .get()
            .addOnSuccessListener {
                if(user != null){
                    onSuccess(user as User)
                }else{
                    user = it.toObject(User::class.java)
                    if(user!=null) {
                        onSuccess(user as User)
                    }
                }

            }
    }


}
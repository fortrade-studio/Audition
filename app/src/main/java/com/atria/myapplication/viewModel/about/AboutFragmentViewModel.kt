package com.atria.myapplication.viewModel.about

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import com.atria.myapplication.Constants
import com.atria.myapplication.Constants.profile_id
import com.google.firebase.firestore.FirebaseFirestore

class AboutFragmentViewModel(
    val context:Context,
    val view:View
) :ViewModel(){

    companion object{
        private val firebase = FirebaseFirestore.getInstance()
    }

    private data class user(
        val date :String = "",
        val email:String = "",
        val gender :String = "",
        val name:String = "",
        val phNumber:String = "",
        val username:String = ""
    )

    fun getPersonalData(onSuccess:(String)->Unit){
        firebase.collection(Constants.user)
            .document(profile_id)
            .get()
            .addOnSuccessListener {
                val user = it.toObject(user::class.java)
                if(user!=null) {
                    val string =
                        user.name + "\n\n" + user.username + "\n\n" + user.email + "\n\n" + user.gender + "\n\n"
                    onSuccess(string)
                }
            }
    }


}
package com.atria.myapplication.viewModel.images

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import com.atria.myapplication.Constants.images
import com.atria.myapplication.Constants.links
import com.atria.myapplication.Constants.profile_id
import com.atria.myapplication.Constants.user
import com.atria.myapplication.Constants.viewdata
import com.google.firebase.firestore.FirebaseFirestore

class ImagesFragmentViewModel(
    val context : Context,
    val view : View
) :ViewModel(){

    companion object{
        val fireabase = FirebaseFirestore.getInstance()
    }

    fun getImagesOfUser(onSuccess:(ArrayList<String>)->Unit){
        fireabase.collection(user)
            .document(profile_id)
            .collection(viewdata)
            .document(images)
            .get()
            .addOnSuccessListener {
                val images = it.get(links) as ArrayList<String>
                onSuccess(images)
            }
    }

}
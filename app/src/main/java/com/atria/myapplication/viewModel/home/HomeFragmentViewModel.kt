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
import com.google.firebase.ktx.Firebase


class HomeFragmentViewModel(
    val view: View,
    val context: Context
) : ViewModel() {

    companion object{
        val firebase = FirebaseFirestore.getInstance()
    }

    data class Audition(
        val company:String = "",
        val image : String = "",
        val location : String = "",
        val position : String = ""
    )
    data class Active(
        val company:String = "",
        val image : String = "",
        val location : String = "",
        val position : String = "",
        val details:String = ""
    )

    fun getNewestOpenings(onSuccess:(List<Audition>)->Unit){
        firebase.collection("Auditions")
            .document("new")
            .collection("New")
            .get()
            .addOnSuccessListener {
                val auditions = it.toObjects(Audition::class.java)
                onSuccess(auditions)
            }
    }

    fun getActivelyOpening(onSuccess: (List<Active>) -> Unit){
        firebase.collection("Auditions")
            .document("active")
            .collection("Active")
            .get()
            .addOnSuccessListener {
                val actives = it.toObjects(Active::class.java)
                onSuccess(actives)
            }
    }
}
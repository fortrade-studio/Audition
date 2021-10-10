package com.atria.myapplication.viewModel.follow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atria.myapplication.Constants
import com.atria.myapplication.viewModel.profile.ProfileFragmentViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class FollowingViewModel:ViewModel() {

    companion object{
        val auth = FirebaseAuth.getInstance().currentUser
        val firebase = FirebaseFirestore.getInstance()
    }

    data class Following(
        val Users:String="",
        val userId:String="",
        val img:String="",
        val name:String=""
    )

    fun getFollowings(onFetched:(List<Following>)->Unit){
        firebase.collection(Constants.user)
            .document(auth?.phoneNumber!!)
            .collection("Following")
            .get()
            .addOnSuccessListener {
                val following = it.toObjects(Following::class.java)
                onFetched(following)
            }

    }

}

class FollowingViewModelFactory:ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FollowingViewModel() as T
    }
}
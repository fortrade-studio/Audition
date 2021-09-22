package com.atria.myapplication.viewModel.search

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.atria.myapplication.Constants.user
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchFragmentViewModel(

) : ViewModel() {

    companion object {
        val firebase = FirebaseFirestore.getInstance()
        private const val TAG = "SearchFragmentViewModel"
    }

    data class SearchUser(
        val username: String = "",
        val name: String = "",
        val profileImage: String = "",
        var phNumber  : String = ""
    )

    fun search(searchValue: String, onSuccess: (List<SearchUser>) -> Unit) {
        if (searchValue == "not set" || searchValue == "") {
            return onSuccess(ArrayList())
        }
        firebase.collection(user)
            .get()
            .addOnSuccessListener { it ->
                val searchResult = it.toObjects(SearchUser::class.java)
                val filter = searchResult.filter {
                    it.username.contains(searchValue, true)
                }
                if (searchResult.isEmpty()) {
                    onSuccess(ArrayList())
                } else {
                    onSuccess(filter)
                }
            }
    }

}
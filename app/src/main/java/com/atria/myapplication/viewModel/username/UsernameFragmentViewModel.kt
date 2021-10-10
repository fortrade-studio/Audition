package com.atria.myapplication.viewModel.username

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UsernameFragmentViewModel : ViewModel() {

    companion object{
        private const val TAG = "UsernameFragmentViewMod"
        private val firebase = FirebaseFirestore.getInstance()
        private val auth = FirebaseAuth.getInstance()
    }
    val usernameAcceptable : MutableLiveData<Boolean> = MutableLiveData(false)


    // 0 -> special characters

    private val specialCharacters = " !#$%&'()*+,-./:;<=>?@[]^`{|}~"
    fun checkForFormat(username: String, onFormatChecked: (Boolean) -> Unit ){
        CoroutineScope(Dispatchers.IO).launch {
            for (char in username){
                if (specialCharacters.contains(char)){
                    onFormatChecked(false)
                    usernameAcceptable.postValue(false)
                    return@launch
                }
            }
            onFormatChecked(true)
        }

    }

    fun uploadUsernameToCloud(username: String)= CoroutineScope(Dispatchers.IO).launch {
        // two things are needed here , upload name to user fields and name to username list
        firebase
            .collection("Users")
            .document(auth.currentUser!!.phoneNumber!!)
            .get()
            .addOnSuccessListener {

            }
    }

    data class Username(
        val name: String
    )

    // onFetched(true) -> there exists a record
    fun checkForUsername(
        username: String,
        onFetched: (Boolean) -> Unit,
        onNetworkError: (Exception) -> Unit
    ) = CoroutineScope(Dispatchers.IO).launch {
        Log.i(TAG, "checkForUsername: $username")
        firebase.collection("Username")
            .document(username.trim())
            .get()
            .addOnSuccessListener {
                Log.i(TAG, "checkForUsername: ${it["name"]}")
                onFetched(it.exists())
                if(!it.exists()){
                    usernameAcceptable.postValue(true)
                }else{
                    usernameAcceptable.postValue(false)
                }
            }
            .addOnFailureListener {
                usernameAcceptable.postValue(false)
                onNetworkError(it)
            }
    }

}

class UsernameFragmentViewModelFactory: ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UsernameFragmentViewModel() as T
    }

}
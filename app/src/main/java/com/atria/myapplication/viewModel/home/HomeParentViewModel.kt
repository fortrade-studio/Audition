package com.atria.myapplication.viewModel.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atria.myapplication.Constants.circular
import com.atria.myapplication.Constants.links
import com.atria.myapplication.Constants.name
import com.atria.myapplication.Constants.user
import com.atria.myapplication.Constants.viewdata
import com.atria.myapplication.notification.PushNotification
import com.atria.myapplication.notification.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeParentViewModel : ViewModel() {

    companion object{
        val firebase = FirebaseFirestore.getInstance()
        val phNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber
        val ioScope = CoroutineScope(Dispatchers.IO)

        private const val TAG = "HomeParentViewModel"

    }

    fun getUserProfileAndName(onSuccess:(String , String)->Unit){
        ioScope.launch {
            firebase.collection(user)
                .document(phNumber!!)
                .collection(viewdata)
                .document(links)
                .get()
                .addOnSuccessListener {
                    val circular = it.get(circular) as String
                    val name = it.get(name) as String
                    onSuccess(circular,name)
                }
        }
    }

    fun sendNotification(notification:PushNotification){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if(response.isSuccessful){
                    Log.i(TAG, "sendNotification: ${response.body()}")
                }else{
                    Log.i(TAG, "failed: ${response.errorBody().toString()}")
                }

            }catch (e: Exception){
                Log.e(TAG, "sendNotification: ",e )
            }
        }
    }

}

class HomeParentViewModelFactory : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeParentViewModel() as T
    }

}
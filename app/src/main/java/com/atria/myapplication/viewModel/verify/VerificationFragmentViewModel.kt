package com.atria.myapplication.viewModel.verify

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import com.atria.myapplication.VerificationFragment
import com.atria.myapplication.room.User
import com.atria.myapplication.viewModel.profile.ProfileFragmentViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit
import kotlin.math.sign

class VerificationFragmentViewModel(context: Context, val view: View) : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFireStore = FirebaseFirestore.getInstance()
    private var verificationId : String? = null
    // int 1 -> already verified
    // int 0 -> code sent
    // int -1 -> failure
    fun sendVerificationCode(activity: Activity, onCodeSentFunction:(Int,PhoneAuthCredential?)->Unit,phNumber:String){

        val authCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                onCodeSentFunction(1,credential)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                onCodeSentFunction(-1,null)
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                verificationId = p0
                onCodeSentFunction(0,null)
            }

        }

        val phoneAuthOptions = PhoneAuthOptions
            .newBuilder(firebaseAuth)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setPhoneNumber(phNumber)
            .setCallbacks(authCallback)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions)

    }

    fun checkForCode(verificationCode:String, invalidCred:()->Unit, user: User?, onCompleted: (Boolean) -> Unit){
        if(verificationId != null) {
            val credential = PhoneAuthProvider.getCredential(verificationId!!, verificationCode)
            if(user == null){
                signInWithCredentials(credential){ onCompleted(it) }
                return
            }
            uploadUserToTheDatabase(user){
                if(it){
                    signInWithCredentials(credential){ onCompleted(it) }
                }else{
                    onCompleted(false)
                    Snackbar.make(view,"Sync Failed !! Please Check Your Internet Connection",Snackbar.LENGTH_LONG).show()
                }
            }
        }else{
            invalidCred()
        }
    }

     fun signInWithCredentials(credential: PhoneAuthCredential,onCompleted:(Boolean)->Unit){
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                onCompleted(true)
            }
            .addOnFailureListener {
                onCompleted(false)
            }
    }

    fun uploadUserToTheDatabase(user:User?,onCompleted: (Boolean) -> Unit){
        if (user != null) {
            firebaseFireStore
                .collection("Users")
                .document(user.phNumber)
                .set(user)
                .addOnSuccessListener {
                    uploadDefaultLinkToUser(user){
                        onCompleted(true)
                    }
                }
                .addOnFailureListener {
                    onCompleted(false)
                }
        }
    }

    fun uploadDefaultLinkToUser(user:User,onSuccess:()->Unit){
        firebaseFireStore.collection("Users")
            .document(user.phNumber)
            .collection("ViewData")
            .document("links")
            .set(ProfileFragmentViewModel.ViewData(
                "https://www.knivesindia.com/ecom/wp-content/uploads/2017/06/wood-blog-placeholder.jpg",
                "https://holmesbuilders.com/wp-content/uploads/2016/12/male-profile-image-placeholder.png",
                0,
                0,
                user.name,
                user.username,
                ".."
            ))
            .addOnSuccessListener {
                onSuccess()
            }
    }

}
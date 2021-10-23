package com.atria.myapplication.viewModel.verify

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
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


class VerificationFragmentViewModel(val context: Context, val view: View) : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFireStore = FirebaseFirestore.getInstance()
    private var verificationId : String? = null
    companion object {
        private const val MY_PREFS_NAME = "User"
        private const val logged = "loggedIn"
    }
    fun sendVerificationCode(
        activity: Activity,
        onCodeSentFunction: (Int, PhoneAuthCredential?) -> Unit,
        phNumber: String
    ){

        val authCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                onCodeSentFunction(1, credential)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(context, p0.localizedMessage.toString(), Toast.LENGTH_SHORT).show()
                onCodeSentFunction(-1, null)
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                verificationId = p0
                onCodeSentFunction(0, null)
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


    fun checkForCode(
        verificationCode: String,
        invalidCred: () -> Unit,
        user: User?,
        onCompleted: (Boolean) -> Unit
    ){
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
                    Snackbar.make(
                        view,
                        "Sync Failed !! Please Check Your Internet Connection",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }else{
            invalidCred()
        }
    }

     fun signInWithCredentials(credential: PhoneAuthCredential, onCompleted: (Boolean) -> Unit){
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                onCompleted(true)
            }
            .addOnFailureListener {
                onCompleted(false)
            }
    }

    fun uploadUserToTheDatabase(user: User?, onCompleted: (Boolean) -> Unit){
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

    // todo : 0 is verified
    // 1 is username verified
    fun storeInCache(){
        val editor: SharedPreferences.Editor =
            context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit()
        editor.putInt(logged, 0)
        editor.apply()
    }

    private fun uploadDefaultLinkToUser(user: User, onSuccess: () -> Unit){
        firebaseFireStore.collection("Users")
            .document(user.phNumber)
            .collection("ViewData")
            .document("links")
            .set(
                ProfileFragmentViewModel.ViewData(
                    "https://firebasestorage.googleapis.com/v0/b/audition-15207.appspot.com/o/default%2Fbackground%20(1).png?alt=media&token=cd2f233e-711c-4e0b-9687-3dc62fe4cb5e",
                    "https://firebasestorage.googleapis.com/v0/b/audition-15207.appspot.com/o/default%2Fpo.png?alt=media&token=964fe43d-d25a-41c5-88ba-b626bdef73b8",
                    0,
                    0,
                    user.name,
                    user.username,
                    ".."
                )
            )
            .addOnSuccessListener {
                onSuccess()
            }
    }

}
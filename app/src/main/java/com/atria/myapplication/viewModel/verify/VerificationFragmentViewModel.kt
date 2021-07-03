package com.atria.myapplication.viewModel.verify

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import kotlin.math.sign

class VerificationFragmentViewModel(context: Context, view: View) : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
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

    fun checkForCode(verificationCode:String,invalidCred:()->Unit , onCompleted: (Boolean) -> Unit){
        if(verificationId != null) {
            val credential = PhoneAuthProvider.getCredential(verificationId!!, verificationCode)
            signInWithCredentials(credential){ onCompleted(it) }
        }else{
            invalidCred()
        }
    }

    private fun signInWithCredentials(credential: PhoneAuthCredential,onCompleted:(Boolean)->Unit){
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                onCompleted(true)
            }
            .addOnFailureListener {
                onCompleted(false)
            }
    }

}
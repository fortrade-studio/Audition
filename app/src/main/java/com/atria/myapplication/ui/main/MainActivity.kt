package com.atria.myapplication.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.atria.myapplication.HomeActivity
import com.atria.myapplication.HomeFragment
import com.atria.myapplication.R
import com.atria.myapplication.service.NotificationFirebaseService
import com.atria.myapplication.utils.NumberToUniqueStringGenerator
import com.google.android.gms.auth.GoogleAuthUtil.getToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        if(FirebaseAuth.getInstance().currentUser != null){
//            val intent = Intent(this, HomeActivity::class.java)
//            startActivity(intent)
//        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(TAG, "onActivityResult: $resultCode")
    }
}
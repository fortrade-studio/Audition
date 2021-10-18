package com.atria.myapplication

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.androidstudy.networkmanager.Tovuti
import com.atria.myapplication.service.NotificationFirebaseService
import com.atria.myapplication.ui.main.MainActivity
import com.atria.myapplication.utils.NumberToUniqueStringGenerator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging

class HomeActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "HomeActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.navigationBarColor = this.resources.getColor(R.color.bg_black)
        }

        Constants.checkIfUser = when {
            intent.extras?.getInt("user") != 0 -> 1
            else -> 0
        }

        NotificationFirebaseService.sharedPreference =
            this.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseInstallations.getInstance().getToken(true).addOnSuccessListener {
            NotificationFirebaseService.token = it.token
        }
        FirebaseMessaging.getInstance()
            .subscribeToTopic("/topics/" + NumberToUniqueStringGenerator.userUniqueString())
            .addOnSuccessListener {}
            .addOnFailureListener {
                Log.e(TAG, "onViewCreated: ", it)
                Toast.makeText(
                    this,
                    "Notification Pipe Unable to Set!",
                    Toast.LENGTH_LONG
                ).show()
            }

        Tovuti.from(this).monitor { connectionType, isConnected, isFast ->
            if (isFast) {
            } else {
                Log.i(TAG, "onCreate: slow")
            };
        }
    }


    override fun onStop() {
        super.onStop()
        Tovuti.from(this).stop()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (Constants.isHome) {
            finish()
            moveTaskToBack(true)
        }
    }


}
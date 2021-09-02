package com.atria.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        Constants.checkIfUser = when {
            intent.extras?.getInt("user")!=0 -> 1
            else -> 0
        }
    }

}
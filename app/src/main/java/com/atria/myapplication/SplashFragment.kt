package com.atria.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore

class SplashFragment : Fragment(), Thread.UncaughtExceptionHandler {


    private val MY_PREFS_NAME = "User"
    private val logged = "loggedIn"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(this)

        val sharedPreference  =
            requireContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE)
        val value = sharedPreference.getInt(logged,-1)

        Handler().postDelayed({
            if(value == 0){
                findNavController().navigate(R.id.action_splashFragment_to_usernameFragment2)
            }else if (value == 1 || value == 2){
                val intent = Intent(requireContext(),HomeActivity::class.java)
                startActivity(intent)
            }
        },2000L)


    }
    override fun uncaughtException(t: Thread, e: Throwable) {
        FirebaseFirestore.getInstance()
            .collection("Error")
            .document(this::class.java.simpleName)
            .collection(this::class.java.simpleName.toUpperCase())
            .document(e.localizedMessage)
            .set(mapOf(Pair("value",e.stackTraceToString())))
            .addOnSuccessListener { Log.i("here", "uncaughtException: reported")}
    }

}
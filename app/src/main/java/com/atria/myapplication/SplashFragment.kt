package com.atria.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.properties.Delegates

class SplashFragment : Fragment(), Thread.UncaughtExceptionHandler {


    companion object{
        private const val MY_PREFS_NAME = "User"
        private const val logged = "loggedIn"
    }

    private var value by Delegates.notNull<Int>()

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
        value = sharedPreference.getInt(logged,-1)



        Handler(Looper.getMainLooper()).postDelayed({
            if(value == 0){
                findNavController().navigate(R.id.action_splashFragment_to_usernameFragment2)
            }else if (value == 1 || value == 2){
                if(context!=null) {
                    val intent = Intent(requireContext(), HomeActivity::class.java)
                        startActivity(intent)
                }
            }else{
                lifecycleScope.launchWhenResumed {
                    findNavController().navigate(R.id.loginFragment)
                }
            }
        },2000L)


    }
    override fun uncaughtException(t: Thread, e: Throwable) {
        FirebaseFirestore.getInstance()
            .collection("Error")
            .document(this::class.java.simpleName)
            .collection(this::class.java.simpleName.toUpperCase(Locale.ROOT))
            .document(e.localizedMessage)
            .set(mapOf(Pair("value",e.stackTraceToString())))
            .addOnSuccessListener { Log.i("here", "uncaughtException: reported")}
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            if(value == 0){
                findNavController().navigate(R.id.action_splashFragment_to_usernameFragment2)
            }else if (value == 1 || value == 2){
                if(context!=null) {
                    val intent = Intent(requireContext(), HomeActivity::class.java)
                    startActivity(intent)
                }
            }else{
                lifecycleScope.launchWhenResumed {
                    findNavController().navigate(R.id.loginFragment)
                }
            }
        },2000L)
    }

}
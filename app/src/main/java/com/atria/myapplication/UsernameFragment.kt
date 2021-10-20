package com.atria.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.atria.myapplication.databinding.FragmentUsernameBinding
import com.atria.myapplication.viewModel.username.UsernameFragmentViewModel
import com.atria.myapplication.viewModel.username.UsernameFragmentViewModelFactory
import com.github.ybq.android.spinkit.style.Wave
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.launch


class UsernameFragment : Fragment() , Thread.UncaughtExceptionHandler{

    private lateinit var usernameFragmentBinding: FragmentUsernameBinding
    private lateinit var usernameFragmentViewModel: UsernameFragmentViewModel
    private lateinit var intent: Intent

    companion object {
        private val mainScope = CoroutineScope(Dispatchers.Main)
        private const val TAG = "UsernameFragment"
        private val auth = FirebaseAuth.getInstance()
        private const val MY_PREFS_NAME = "User"
        private const val logged = "loggedIn"
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        usernameFragmentBinding = FragmentUsernameBinding.inflate(inflater, container, false)
        return usernameFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(this)

        if (auth.currentUser == null){
            requireActivity().onBackPressed()
        }

        if(getStoredCache() != -1){
            requireActivity().finish()
            requireActivity().moveTaskToBack(true)
        }

        intent = Intent(requireContext(), HomeActivity::class.java)
        usernameFragmentViewModel = ViewModelProvider(this, UsernameFragmentViewModelFactory()).get(
            UsernameFragmentViewModel::class.java
        )

        usernameFragmentBinding.usernameEditText.addTextChangedListener { s ->
            usernameFragmentViewModel.usernameAcceptable.postValue(
                s.toString().trim().length in 5..15
            )
            usernameFragmentViewModel.checkForFormat(s.toString()) { it ->
                if (!it) {
                    mainScope.launch {
                        usernameFragmentBinding.usernameEditText.error =
                            "No spaces or special characters allowed"
                    }
                } else {
                    usernameFragmentViewModel.checkForUsername(s.toString(), {
                        if (it) {
                            // not available
                            mainScope.launch {
                                usernameFragmentBinding.usernameEditText.error = "not available"
                            }
                        }
                    }) {
                        Log.e(TAG, "onViewCreated: ", it)
                        Snackbar.make(
                            requireView(),
                            "Something Went Wrong !! Please Check Your Connection",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        usernameFragmentViewModel.usernameAcceptable.observe(viewLifecycleOwner) {
            val s = usernameFragmentBinding.usernameEditText.text.toString()
            if (it) {
                if (s.trim().length in 5..15
                ) {
                    usernameFragmentBinding.appCompatButton.setOnClickListener {
                        usernameFragmentBinding.spinKit.visibility = View.VISIBLE
                        usernameFragmentBinding.spinKit.setIndeterminateDrawable(Wave())
                        usernameFragmentViewModel.uploadUsernameToCloud(s.trim(), {
                            storeInCache()
                            usernameFragmentBinding.spinKit.visibility = View.GONE
                            startActivity(intent)
                        }) {
                            usernameFragmentBinding.spinKit.visibility = View.GONE
                            Snackbar.make(
                                requireView(),
                                "Something Went Wrong !! Please Check Your Connection",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    // not in range 11 to 4
                    usernameFragmentBinding.appCompatButton.setOnClickListener { v ->
                        usernameFragmentBinding.usernameEditText.error =
                            "username length range is 5 to 10"
                    }
                }
            } else {
                // not acceptable
                usernameFragmentBinding.appCompatButton.setOnClickListener {
                    Snackbar.make(
                        requireView(),
                        "Something Went Wrong !! Please Check Your Connection",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

        }
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        FirebaseFirestore.getInstance()
            .collection("Error")
            .document(this::class.java.simpleName)
            .collection(this::class.java.simpleName.toUpperCase())
            .document(e.localizedMessage)
            .set(mapOf(Pair("value",e.stackTraceToString())))
            .addOnSuccessListener { Log.i(TAG, "uncaughtException: reported")}
    }

    private val MY_PREFS_NAME = "User"
    private val logged = "loggedIn"

    fun storeInCache(){
        val editor: SharedPreferences.Editor =
            requireActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putInt(logged, 1)
        editor.apply()
    }

    fun getStoredCache():Int{
        val sharedPreference  =
            requireContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreference.getInt(logged,-1)
    }

}
package com.atria.myapplication

import android.content.Intent
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
                        usernameFragmentViewModel.uploadUsernameToCloud(s.trim(), {
                            startActivity(intent)
                        }) {
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

}
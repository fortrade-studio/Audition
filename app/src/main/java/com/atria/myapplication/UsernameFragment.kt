package com.atria.myapplication

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
import com.atria.myapplication.databinding.FragmentUsernameBinding
import com.atria.myapplication.viewModel.username.UsernameFragmentViewModel
import com.atria.myapplication.viewModel.username.UsernameFragmentViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.launch


class UsernameFragment : Fragment() {

    private lateinit var usernameFragmentBinding: FragmentUsernameBinding
    private lateinit var usernameFragmentViewModel: UsernameFragmentViewModel

    companion object {
        private val mainScope = CoroutineScope(Dispatchers.Main)
        private const val TAG = "UsernameFragment"
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
            if (it) {
                if (usernameFragmentBinding.usernameEditText.text.toString()
                        .trim().length in 5..15
                ) {
                    usernameFragmentBinding.appCompatButton.setOnClickListener {
                        Log.i(TAG, "onViewCreated: alright clicked")
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

}
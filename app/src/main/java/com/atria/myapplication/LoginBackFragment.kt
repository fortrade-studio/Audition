package com.atria.myapplication

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.atria.myapplication.databinding.FragmentLoginBackBinding
import com.atria.myapplication.viewModel.register.LoginBackFragmentViewModel
import com.atria.myapplication.viewModel.register.LoginBackFragmentViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore


class LoginBackFragment : Fragment() , Thread.UncaughtExceptionHandler{

    private lateinit var loginFragmentBinding: FragmentLoginBackBinding
    private var stateNumber = MutableLiveData<Int>(0)
    private lateinit var loginBackFragmentViewModel: LoginBackFragmentViewModel

    companion object{
        private val MY_PREFS_NAME = "User"
        private val logged = "loggedIn"
        private const val TAG = "LoginBackFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loginFragmentBinding = FragmentLoginBackBinding.inflate(inflater, container, false)
        return loginFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(this)
        loginBackFragmentViewModel = ViewModelProvider(
            this, LoginBackFragmentViewModelFactory(
                requireContext(), requireView()
            )
        ).get(LoginBackFragmentViewModel::class.java)



        loginFragmentBinding.phoneEditText.addTextChangedListener {
            loginFragmentBinding.phoneEditText.error = null
            stateNumber.postValue(0)
            loginBackFragmentViewModel.verifyNumber("+91" + it.toString(), {s->
                if (s) {
                    // available
                    stateNumber.postValue(1)
                } else {
                    // not available
                    loginFragmentBinding.phoneEditText.requestFocus()
                    Log.i(TAG, "onViewCreated: ${it.toString()}")
                    loginFragmentBinding.phoneEditText.error = "No Account For this Number"
                    stateNumber.postValue(-1)
                }
            }){
                // ERROR :
                Snackbar.make(requireView(),"Something went wrong !!",Snackbar.LENGTH_LONG).show()
            }
        }

        val objects = listOf("+91", "+00")

        loginFragmentBinding.countryCodeSpinner.adapter =
            ArrayAdapter(requireContext(), R.layout.spinner_item, objects)

        stateNumber.observe(viewLifecycleOwner) {
            if (it == 1) {
                // if true means user can move on
                val number = objects[loginFragmentBinding.countryCodeSpinner.selectedItemPosition] + loginFragmentBinding.phoneEditText.text

                loginFragmentBinding.phoneEditText.error = null
                val bundle = Bundle().apply {
                    putBoolean("verification", true)
                    putString(
                        "phNumber",
                        number
                    )
                }
                loginFragmentBinding.loginButton.setOnClickListener {
                    loginBackFragmentViewModel.checkForUsername(number,{
                        if (it){
                            storeInCache(1)
                            findNavController().navigate(
                                R.id.action_loginBackFragment_to_verificationFragment,
                                bundle
                            )
                        }else{
                            storeInCache(0)
                            findNavController().navigate(
                                R.id.action_loginBackFragment_to_verificationFragment,
                                bundle
                            )
                        }
                    }){ Snackbar.make(requireView(), "Something went wrong ", Snackbar.LENGTH_LONG)
                        .show()}

                }
            } else if (it == 0) {
                // here it is loading
                loginFragmentBinding.loginButton.setOnClickListener {
                    Snackbar.make(requireView(), "Verifying your number....", Snackbar.LENGTH_LONG)
                        .show()
                }
            } else {
                loginFragmentBinding.phoneEditText.requestFocus()
                loginFragmentBinding.phoneEditText.error = "No Account For this Number"
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
            .addOnSuccessListener { Log.i("here", "uncaughtException: reported")}
    }

    private fun storeInCache(value:Int){
        val editor: SharedPreferences.Editor =
            requireContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putInt(logged, value)
        editor.apply()
    }



}
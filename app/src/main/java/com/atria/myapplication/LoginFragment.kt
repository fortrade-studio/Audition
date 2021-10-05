package com.atria.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.atria.myapplication.databinding.FragmentLoginBinding
import com.atria.myapplication.viewModel.login.LoginFragmentViewModel
import com.atria.myapplication.viewModel.login.LoginFragmentViewModelFactory
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {


    private lateinit var loginFragmentViewModel: LoginFragmentViewModel
    private lateinit var loginFragmentBinding: FragmentLoginBinding
    private var accountExists = MutableLiveData<Boolean>(true)
    private val testing = false
    private val firebase = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        loginFragmentBinding = FragmentLoginBinding.inflate(inflater, container, false)
        return loginFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(firebase.currentUser !=null){
            val intent = Intent(requireContext(), HomeActivity::class.java)
            requireContext().startActivity(intent)
        }


        val objects = listOf("+91", "+00")
        val gender = listOf("Gender", "Male", "Female")
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager


        loginFragmentBinding.root.setOnClickListener {
            inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
        }

        accountExists.observe(viewLifecycleOwner) {
            if(testing){
                loginFragmentBinding.materialCheckBox.isClickable = true
                return@observe
            }else{
                if(it){
                    loginFragmentBinding.materialCheckBox.isChecked = false
                }
                loginFragmentBinding.materialCheckBox.isClickable = !it
            }
        }

        loginFragmentBinding.phoneEditText.addTextChangedListener {
            loginFragmentViewModel.checkIfUserExists(objects[loginFragmentBinding.countryCodeSpinner.selectedItemPosition] + it.toString()) {
                if (!it) {
                    accountExists.postValue(true)
                    loginFragmentBinding.phoneEditText.requestFocus()
                    loginFragmentBinding.phoneEditText.error = "Account Already Exist"
                } else {
                    accountExists.postValue(false)
                }
            }
        }

        loginFragmentBinding.countryCodeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    loginFragmentViewModel.checkIfUserExists(objects[position] + loginFragmentBinding.phoneEditText.text.toString()) {
                        if (!it) {
                            accountExists.postValue(true)
                            loginFragmentBinding.phoneEditText.requestFocus()
                            loginFragmentBinding.phoneEditText.error = "Account Already Exist"
                        } else {
                            accountExists.postValue(false)
                            loginFragmentBinding.phoneEditText.error = null
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }


        // init the view model
        loginFragmentViewModel =
            ViewModelProvider(
                this, LoginFragmentViewModelFactory(requireContext(), requireView())
            ).get(LoginFragmentViewModel::class.java)


        // we just need to check if the inputs are not empty
        loginFragmentBinding.signUpButton.setOnClickListener {
            checkForEmpty { // onEmptyNotFound we will upload to cloud
                val selectedItemPosition =
                    loginFragmentBinding.countryCodeSpinner.selectedItemPosition
                loginFragmentViewModel.showConfirmDialog(
                    objects[selectedItemPosition],
                    loginFragmentBinding.phoneEditText.text.toString()
                ) {
                    val bundle = Bundle().apply {
                        putString(
                            "phNumber",
                            objects[loginFragmentBinding.countryCodeSpinner.selectedItemPosition] + loginFragmentBinding.phoneEditText.text.toString()
                        )
                        putString("email", loginFragmentBinding.emailEditText.text.toString())
                        putString("name", loginFragmentBinding.nameEditText.text.toString())
                        putString(
                            "gender",
                            gender[loginFragmentBinding.spinner.selectedItemPosition]
                        )
                        putString("date", loginFragmentBinding.editTextDate.text.toString())
                    }
                    findNavController().navigate(
                        R.id.action_loginFragment_to_verificationFragment,
                        bundle
                    )
                }
            }
        }

        // need to init the spinners

        loginFragmentBinding.countryCodeSpinner.adapter = ArrayAdapter(
            requireContext(), R.layout.spinner_item, objects
        )

        loginFragmentBinding.spinner.adapter =
            ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                gender
            )

        loginFragmentBinding.loginTextView.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_loginBackFragment)
        }

    }

    private fun checkForEmpty(onEmptyNotFound: () -> Unit) {


        if (accountExists.value == true && !testing) {
            loginFragmentBinding.phoneEditText.requestFocus()
            loginFragmentBinding.phoneEditText.error = "Account Already Exist"
            return
        }

        var isEmpty = false
        loginFragmentBinding.root.children.forEach {
            if (it is EditText) {
                if (TextUtils.isEmpty(it.text)) {
                    it.requestFocus()
                    it.error = getString(R.string.empty_field)
                    isEmpty = true
                    return
                } else {
                    it.error = null
                }
            }
        }

        if (loginFragmentBinding.spinner.selectedItemPosition == 0) {
            loginFragmentBinding.spinner.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.shake
                )
            )
            return
        }

        if (!loginFragmentBinding.materialCheckBox.isChecked) {
            loginFragmentBinding.termsTextView.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.shake
                )
            )
            return
        }

        if (!isEmpty) {
            onEmptyNotFound()
        }
    }


}
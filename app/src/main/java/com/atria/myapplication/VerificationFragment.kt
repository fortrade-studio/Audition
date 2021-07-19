package com.atria.myapplication

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.service.autofill.UserData
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.atria.myapplication.databinding.FragmentVerificationBinding
import com.atria.myapplication.room.User
import com.atria.myapplication.room.UserDatabase
import com.atria.myapplication.room.UserRepository
import com.atria.myapplication.viewModel.verify.VerificationFragmentViewModel
import com.atria.myapplication.viewModel.verify.VerificationViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VerificationFragment : Fragment() {

    private lateinit var verificationBinding: FragmentVerificationBinding
    private lateinit var verificationFragmentViewModel: VerificationFragmentViewModel
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var repo:UserRepository

    private var user: User? = null

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val mainScope = CoroutineScope(Dispatchers.Main)

    companion object {
        private const val TAG = "VerificationFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        verificationBinding = FragmentVerificationBinding.inflate(inflater, container, false)
        return verificationBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userDao = UserDatabase.getDatabase(requireContext()).getUserDao()
        repo = UserRepository(userDao)

        val isLogin:Boolean  = arguments?.getBoolean("verification")?:false
        val phNumber: String? = arguments?.getString("phNumber")

        val name :String? = arguments?.getString("name")
        val email :String? = arguments?.getString("email")
        val gender :String? = arguments?.getString("gender")
        val date :String? = arguments?.getString("date")


        verificationBinding.headerView.text = getString(R.string.info_verification,phNumber)
        if ( !isLogin &&  phNumber == null ) {
            findNavController().navigate(R.id.action_verificationFragment_to_loginFragment)
        }else{
            if(name != null && email != null && gender != null && date != null &&  phNumber != null){
                user = User(0,name,email,gender,date,phNumber,"not set","","")
            }
        }



        verificationFragmentViewModel = ViewModelProvider(
            this,
            VerificationViewModelFactory(requireContext(), requireView())
        ).get(VerificationFragmentViewModel::class.java)

        val verificationFunction  = {
            verificationFragmentViewModel.sendVerificationCode(requireActivity(), { i, auth ->
                when (i) {
                    1 -> {
                        verificationBinding.verifyButton.isClickable = false
                        verificationBinding.lvGhostView.visibility = View.VISIBLE
                        verificationBinding.lvGhostView.startAnim()
                        verificationBinding.otpView.otp = auth?.smsCode

                        if (isLogin) {
                            findNavController().navigate(R.id.action_verificationFragment_to_categoryFragment)
                        } else {

                            // here we need to upload the data to the cloud before confirming sign in
                            verificationFragmentViewModel.uploadUserToTheDatabase(user) {
                                if (it) {
                                    // success
                                    if (auth != null) {
                                        verificationFragmentViewModel.signInWithCredentials(auth) {
                                            if (it) {
                                                // success
                                                verificationBinding.lvGhostView.stopAnim()
                                                verificationBinding.lvGhostView.visibility =
                                                    View.INVISIBLE
                                                findNavController().navigate(R.id.action_verificationFragment_to_categoryFragment)
                                            } else {
                                                verificationBinding.lvGhostView.stopAnim()
                                                verificationBinding.lvGhostView.visibility =
                                                    View.INVISIBLE
                                                Snackbar.make(
                                                    requireView(),
                                                    "Error Signing In",
                                                    Snackbar.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    } else {
                                        verificationBinding.lvGhostView.stopAnim()
                                        verificationBinding.lvGhostView.visibility = View.INVISIBLE
                                        Snackbar.make(
                                            requireView(),
                                            "Error Signing In",
                                            Snackbar.LENGTH_LONG
                                        ).show()
                                    }
                                } else {
                                    // failed
                                    verificationBinding.lvGhostView.stopAnim()
                                    verificationBinding.lvGhostView.visibility = View.INVISIBLE
                                    Snackbar.make(
                                        requireView(),
                                        "Sync To Cloud Failed !! Please Check Your Connection",
                                        Snackbar.LENGTH_LONG
                                    ).show()

                                }
                            }
                        }
                    }
                    -1 -> {
                        Snackbar.make(
                            requireView(),
                            "Something Went Wrong Sending the OTP ! Please Try Again After Some Time",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }, phNumber!!)
        }

        verificationFunction()

        countDownTimer = object : CountDownTimer(60_000,1000) {
            override fun onTick(millisUntilFinished: Long) {
                verificationBinding.resendOtp.isClickable = false
                verificationBinding.resendOtp.text = "00:"+(millisUntilFinished/1000).toString();
            }

            override fun onFinish() {
                verificationBinding.resendOtp.text = getString(R.string.resend)
                verificationBinding.resendOtp.isClickable = true
                verificationBinding.resendOtp.setOnClickListener {
                    verificationFunction()
                    this.start()
                }
            }
        }.apply {
            start()
        }


        with(verificationBinding) {
            verifyButton.setOnClickListener {
                verificationBinding.lvGhostView.visibility = View.VISIBLE
                verificationBinding.lvGhostView.startAnim()
                if (otpView.otp.isEmpty() || otpView.otp.length < 6) {
                    verificationBinding.lvGhostView.stopAnim()
                    verificationBinding.lvGhostView.visibility = View.INVISIBLE
                    otpView.showError()
                } else {
                    verificationFragmentViewModel.checkForCode(otpView.otp, {
                        // invalid cred
                        verificationBinding.lvGhostView.stopAnim()
                        verificationBinding.lvGhostView.visibility = View.INVISIBLE
                        otpView.showError()
                    },user) {
                        if (!it) {
                            verificationBinding.lvGhostView.stopAnim()
                            verificationBinding.lvGhostView.visibility = View.INVISIBLE
                            otpView.showError()
                        } else {
                            // here it is true that means verification was a success
                            verificationBinding.lvGhostView.stopAnim()
                            verificationBinding.lvGhostView.visibility = View.INVISIBLE
                            if (user != null) {
                                ioScope.launch {
                                    repo.insertUser(user!!)
                                    mainScope.launch {
                                        findNavController().navigate(R.id.action_verificationFragment_to_categoryFragment)
                                    }
                                }
                            } else {
                                findNavController().navigate(R.id.action_verificationFragment_to_categoryFragment)
                            }
                        }
                    }
                }
            }
        }

    }

    override fun onDetach() {
        super.onDetach()
        countDownTimer.cancel()
    }


}
package com.atria.myapplication

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.atria.myapplication.databinding.FragmentVerificationBinding
import com.atria.myapplication.viewModel.verify.VerificationFragmentViewModel
import com.atria.myapplication.viewModel.verify.VerificationViewModelFactory
import com.google.android.material.snackbar.Snackbar

class VerificationFragment : Fragment() {

    private lateinit var verificationBinding: FragmentVerificationBinding
    private lateinit var verificationFragmentViewModel: VerificationFragmentViewModel

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

        val phNumber: String? = arguments?.getString("phNumber")
        verificationBinding.headerView.text = getString(R.string.info_verification,phNumber)
        if (phNumber == null) {
            findNavController().navigate(R.id.action_verificationFragment_to_loginFragment)
        }

        verificationFragmentViewModel = ViewModelProvider(
            this,
            VerificationViewModelFactory(requireContext(), requireView())
        ).get(VerificationFragmentViewModel::class.java)

        verificationFragmentViewModel.sendVerificationCode(requireActivity(), { i, auth ->
            when (i) {
                1 -> {
                    verificationBinding.verifyButton.isClickable = false
                    verificationBinding.lvGhostView.visibility = View.VISIBLE
                    verificationBinding.lvGhostView.startAnim()
                    verificationBinding.otpView.otp = auth?.smsCode
                    Handler().postDelayed({
                        verificationBinding.lvGhostView.visibility = View.INVISIBLE
                        findNavController().navigate(R.id.action_verificationFragment_to_categoryFragment)
                        },
                        1000L
                    )
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
                    }) {
                        if (!it) {
                            verificationBinding.lvGhostView.stopAnim()
                            verificationBinding.lvGhostView.visibility = View.INVISIBLE
                            otpView.showError()
                        } else {
                            // here it is true that means verification was a success
                            verificationBinding.lvGhostView.stopAnim()
                            verificationBinding.lvGhostView.visibility = View.INVISIBLE
                            findNavController().navigate(R.id.action_verificationFragment_to_categoryFragment)
                        }
                    }
                }
            }
        }

    }


}
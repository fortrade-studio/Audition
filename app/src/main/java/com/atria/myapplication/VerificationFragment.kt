package com.atria.myapplication

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.atria.myapplication.broadcast.SmsBroadCastReceiver
import com.atria.myapplication.databinding.FragmentLoginBinding
import com.atria.myapplication.databinding.FragmentVerificationBinding
import com.google.android.gms.auth.api.phone.SmsRetriever
import org.jetbrains.annotations.Contract
import java.util.regex.Pattern

class VerificationFragment : Fragment() {

    private lateinit var verificationBinding: FragmentVerificationBinding
    private lateinit var smsBroadcastReceiver:SmsBroadCastReceiver

    companion object{
        private val userRequestConsentCode = 20002;
        private const val TAG = "VerificationFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment
        verificationBinding = FragmentVerificationBinding.inflate(inflater,container,false)
        return  verificationBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startSmsUserConsent()

        verificationBinding.verifyButton.setOnClickListener {
            verificationBinding.lvGhostView.visibility = View.VISIBLE
            verificationBinding.lvGhostView.startAnim()
        }

        verificationBinding.otpView.setOtpCompletionListener {
        }

    }

    private fun startSmsUserConsent(){
        val client = SmsRetriever.getClient(requireContext())
        client.startSmsRetriever().addOnSuccessListener {
            Toast.makeText(requireContext(), "Consent Success", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Consent Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerBroadCastReceiver(){
        smsBroadcastReceiver = SmsBroadCastReceiver()
        smsBroadcastReceiver.onSuccess = {
            activity?.startActivityForResult(it, userRequestConsentCode)
        }
        smsBroadcastReceiver.onFailure = {
            Toast.makeText(requireContext(), "failed", Toast.LENGTH_SHORT).show()
        }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        requireContext().registerReceiver(smsBroadcastReceiver,intentFilter)
    }

    override fun onStart() {
        super.onStart()
        registerBroadCastReceiver()
    }

    override fun onStop() {
        super.onStop()
        requireContext().unregisterReceiver(smsBroadcastReceiver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == userRequestConsentCode){
            if(resultCode == RESULT_OK && data != null){
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                Log.i(TAG, "onActivityResult: $message")
                Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
                getOtpFromMessage(message)
            }else{
                Log.i(TAG, "onActivityResult: Result not okkay")
            }
        }
    }

    private fun getOtpFromMessage(message: String?) {

        val pattern = Pattern.compile("(|^)\\d{6}")
        val matcher = pattern.matcher(message)
        if(matcher.find()){
            Log.i(TAG, "getOtpFromMessage: matcher find  ${matcher.group(0 )}")
            verificationBinding.otpView.setText(matcher.group(0))
        }else{
            Log.i(TAG, "getOtpFromMessage: Not found")
        }
    }

}
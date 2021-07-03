package com.atria.myapplication.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import kotlinx.coroutines.channels.BroadcastChannel

class SmsBroadCastReceiver : BroadcastReceiver() {

    var onSuccess: ((Intent) -> Unit)? = null
    var onFailure: (() -> Unit)? = null

    companion object{
        private const val TAG = "SmsBroadCastReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == SmsRetriever.SMS_RETRIEVED_ACTION){
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status
            when(status.statusCode){
                CommonStatusCodes.SUCCESS -> {
                    Log.i(TAG, "onReceive: Success")
                    val parcelable = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                    if (parcelable != null) {
                        onSuccess?.let { it(parcelable) }
                    } else {
                        Log.i(TAG, "Parcelable null: ")
                    }
                }
                CommonStatusCodes.TIMEOUT ->{
                    Log.i(TAG, "onReceive: TImout")

                    onFailure?.invoke()
                }
                else->{
                    Log.i(TAG, "onReceive: ${status.statusCode}")
                }
            }
        }
    }
}

package com.atria.myapplication.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.atria.myapplication.R
import com.atria.myapplication.ui.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationFirebaseService : FirebaseMessagingService() {

    companion object{
        var sharedPreference : SharedPreferences? = null

        var token:String?
        get() = sharedPreference?.getString("token","")
        set(value)  {
            sharedPreference?.edit()?.putString("token",value)?.apply()
        }

        private const val TAG = "NotificationFirebaseSer"
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        token = p0
    }

    private val channel_id = "testing"

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.i(TAG, "onMessageReceived: ")

        val intent = Intent(this,MainActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1001

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            createChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this,0,intent,FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this,channel_id)
            .setContentText(message.data["message"])
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_baseline_person_24)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationId,notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(notificationManager: NotificationManager){
        val channelName = "testing"
        val channel = NotificationChannel(channel_id,channelName,IMPORTANCE_HIGH).apply {
            description = "testing"
            enableLights(true)
            lightColor = Color.GREEN
        }

        notificationManager.createNotificationChannel(channel)
    }
}
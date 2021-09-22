package com.atria.myapplication.notification

import com.atria.myapplication.notification.NotificationKey.Companion.CONTENT_TYPE
import com.atria.myapplication.notification.NotificationKey.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationInterface {

    @Headers("Authorization: key=$SERVER_KEY","Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ):Response<ResponseBody>

}
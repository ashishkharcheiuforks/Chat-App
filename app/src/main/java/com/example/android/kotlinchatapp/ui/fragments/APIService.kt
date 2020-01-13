package com.example.android.kotlinchatapp.ui.fragments

import com.example.android.kotlinchatapp.ui.notification.MyResponse
import com.example.android.kotlinchatapp.ui.notification.Sender
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIService {
    @Headers(

            "Content-Type:application/json",
            "Authorization:key=AAAAcQgVSkU:APA91bHjCk9pGzOy4fvk0kemHArWJU6FBrijlXdiK8mdPKt1q1hSoNdipe_UNoWzX1TCh_X6UsHdK9Zire3pcIjX-7SrNGv_6yFH9koW_OfVAxpNhXmBdWNtEfrjJ_bmjKc3JCMKrLAH"

    )
    @POST("fcm/send")
    fun sendNotification(@Body body: Sender): Call<MyResponse>



}
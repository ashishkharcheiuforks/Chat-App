package com.example.android.kotlinchatapp.ui.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.example.android.kotlinchatapp.ui.message_activity.MessageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessaging:FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val sented=remoteMessage?.data?.get("sented")
        val firebaseUser=FirebaseAuth.getInstance().currentUser
        if (firebaseUser!=null&&sented.equals(firebaseUser.uid)){
            sendNotificayion(remoteMessage)
        }
    }

    private fun sendNotificayion(remoteMessage: RemoteMessage?) {
        val user=remoteMessage?.data?.get("user")
        val icon=remoteMessage?.data?.get("icon")
        val title=remoteMessage?.data?.get("title")
        val body=remoteMessage?.data?.get("body")
        val notification = remoteMessage?.notification
        val s=user?.replace("[\\D]","")
        val j=-1
        val intent = Intent(this, MessageActivity::class.java)
        val bundle=Bundle()
        bundle.putString("userid",user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent=PendingIntent.getActivity(this,j!!,intent,PendingIntent.FLAG_ONE_SHOT)

        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(this)
            .setSmallIcon(icon?.toInt()!!)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentIntent(pendingIntent)
        val noti=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var i=0
        if (j>0)
            i=j

        noti.notify(1,builder.build())


    }
}
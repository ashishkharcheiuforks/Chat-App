package com.example.android.kotlinchatapp.application

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatApplication :Application(),LifecycleObserver{
    lateinit var reference: DatabaseReference
    internal var firebaseUser: FirebaseUser? = null
    lateinit var mAuth: FirebaseAuth
    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        mAuth = FirebaseAuth.getInstance()
        firebaseUser = mAuth.currentUser
        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart(){
        setStatus("online")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
     fun onResume() {
        setStatus("online")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
     fun onPause() {
        setStatus("offline")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
     fun onStop() {
        setStatus("offline")

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setStatus(status:String){
        reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser?.uid!!)
        val hash:HashMap<String,String> =HashMap<String,String>()

        val date= LocalDateTime.now()
        val formater= DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")
        val formatedDate=date.format(formater)
        hash.put("status",status)
        reference.child("status").setValue(status)
        reference.child("lastSeen").setValue(formatedDate)
    }

}
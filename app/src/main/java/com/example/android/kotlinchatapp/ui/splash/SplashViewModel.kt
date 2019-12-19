package com.example.android.kotlinchatapp.ui.splash

import android.content.Intent
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.kotlinchatapp.ui.activities.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SplashViewModel : ViewModel() {
    val navigate = MutableLiveData<Boolean>()
    var logedin=false
     var auth: FirebaseAuth=FirebaseAuth.getInstance()
    var firebaseUser: FirebaseUser?=auth.currentUser


    fun startHandler() {
        Handler().postDelayed({ navigate.postValue(true) }, 3000)
    }

    fun checkLoging(){
        if (firebaseUser!=null){
           logedin=true
        }
    }
}
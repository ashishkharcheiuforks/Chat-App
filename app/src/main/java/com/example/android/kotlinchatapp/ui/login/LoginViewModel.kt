package com.example.android.kotlinchatapp.ui.login

import android.content.Intent
import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.kotlinchatapp.ui.activities.HomeActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_login.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LoginViewModel : ViewModel() {
    var email = ""
    var password = ""
    var emailError = MutableLiveData<String>()
    var passwordError = MutableLiveData<String>()
    var navigateToRegisteration = MutableLiveData<Boolean>()
    var navigateToForgetPassword = MutableLiveData<Boolean>()
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var firebaseUser: FirebaseUser? = auth.currentUser
    var error = MutableLiveData<String>()
    lateinit var navigator:LoginNavigator
    var loginSuccess = MutableLiveData<Boolean>()


    @RequiresApi(Build.VERSION_CODES.O)
    fun validate() {
        if (email.trim().isNotEmpty()) {
            if (Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                if (password.trim().isNotEmpty()) {
                    if (password.trim().length >= 6) {
                        login()
                    } else {
                        error.value = "password must be more than 6 characters"
                    }
                } else {
                    error.value = "password field must be filled"
                }
            } else {
                error.value = "email is not valid"
            }
        } else {
            error.value = "email field must be filled"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun login() {
        navigator.showLoading()
        auth.signInWithEmailAndPassword(email.trim().toString(), password.trim().toString())
            .addOnCompleteListener(OnCompleteListener<AuthResult>() {
                if (it.isSuccessful) {
                    loginSuccess.value = true
                    setStatus("online")
                } else {
                    error.value = it.exception!!.message
                }
                navigator.hideLoading()
            })
    }

    fun navigateToRegisteration() {
        navigateToRegisteration.value = true
    }

    fun navigateToForgetPassword() {
        navigateToForgetPassword.value = true
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setStatus(status: String) {
         val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser?.uid!!)
        val hash: java.util.HashMap<String, String> = java.util.HashMap<String, String>()

        val date = LocalDateTime.now()
        val formater = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")
        val formatedDate = date.format(formater)
        hash.put("status", status)
        reference.child("status").setValue(status)
        reference.child("lastSeen").setValue(formatedDate)

    }
}
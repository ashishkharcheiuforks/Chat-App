package com.example.android.kotlinchatapp.ui.registeration

import android.annotation.SuppressLint
import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap


class RegisterationViewModel : ViewModel() {
    var email = ""
    var password = ""
    var userName = ""
    var confirmPassword=""
    var navigateToLogin = MutableLiveData<Boolean>()
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var firebaseUser: FirebaseUser? = auth.currentUser
    var error = MutableLiveData<String>()
    lateinit var navigator: RegistraionNavigator
    var regiterSuccess = MutableLiveData<Boolean>()
    lateinit var reference: DatabaseReference

    @RequiresApi(Build.VERSION_CODES.O)
    fun validate() {
        if (userName.trim().isNotEmpty()) {
            if (userName.trim().length >= 5) {
                if (email.trim().isNotEmpty()) {
                    if (Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                        if (password.trim().isNotEmpty()) {
                            if (password.trim().length >= 6) {
                                if (confirmPassword.trim().isNotEmpty()){
                                    if (confirmPassword.trim().length>=6){
                                        if (password.trim() == confirmPassword.trim()){
                                            register()
                                        }else{
                                            error.value = "the two passwords are not matching"
                                        }
                                    }else{
                                        error.value = "confirm password must be more than 6 characters"
                                    }
                                }else{
                                    error.value = "confirm password field must be filled"
                                }
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
            } else {
                error.value = "user name must be more than 5 characters"
            }
        } else {
            error.value = "user name field must be filled"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun register() {
        navigator.showLoading()
        auth.createUserWithEmailAndPassword(email.trim().toString(), password.trim().toString())
            .addOnCompleteListener(OnCompleteListener<AuthResult> { it ->
                if(it.isSuccessful){
                    val firebaseUser=auth.currentUser
                    val userId=firebaseUser!!.uid
                    reference= FirebaseDatabase.getInstance().getReference("Users").child(userId)
//                    val user= User(userId,"default",userName.text.toString())
                    val hashMap=HashMap<String,String>()
                    hashMap?.put("id",userId)
                    hashMap?.put("userName",userName.trim().toString())

                    hashMap?.put("imageURL","default")
                    hashMap?.put("search",userName.trim().toString().toLowerCase())
                    hashMap.put("lastSeen",getDate())
                    hashMap.put("status","online")
                    reference.setValue(hashMap).addOnCompleteListener{
                        if (it.isSuccessful){
                            navigator.hideLoading()
                            regiterSuccess.value=true
                        }

                    }.addOnCanceledListener {
                        navigator.hideLoading()
                        error.value="error"
                    }
                }
                else{
                    error.value=it.exception!!.message
                    navigator.hideLoading()
                }
            }
            )
    }

    fun navigateToLogin() {
        navigateToLogin.value = true
    }
    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDate():String {
        val currentTime: Date = Calendar.getInstance().getTime()
        val formater = SimpleDateFormat("yyyy-MM-dd hh:mm aa")
        val formatedDate = formater.format(currentTime)
        return formatedDate
    }

}
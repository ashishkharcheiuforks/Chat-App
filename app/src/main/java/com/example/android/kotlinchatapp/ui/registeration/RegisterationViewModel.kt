package com.example.android.kotlinchatapp.ui.registeration

import android.content.Intent
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.kotlinchatapp.ui.activities.HomeActivity
import com.example.android.kotlinchatapp.ui.login.LoginNavigator
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterationViewModel : ViewModel() {
    var email = ""
    var password = ""
    var userName = ""
    var navigateToLogin = MutableLiveData<Boolean>()
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var firebaseUser: FirebaseUser? = auth.currentUser
    var error = MutableLiveData<String>()
    lateinit var navigator: RegistraionNavigator
    var regiterSuccess = MutableLiveData<Boolean>()
    lateinit var reference: DatabaseReference

    fun validate() {
        if (userName.trim().isNotEmpty()) {
            if (userName.trim().length >= 5) {
                if (email.trim().isNotEmpty()) {
                    if (Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                        if (password.trim().isNotEmpty()) {
                            if (password.trim().length >= 6) {
                                register()
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

    private fun register() {
        navigator.showLoading()
        auth.createUserWithEmailAndPassword(email.trim().toString(), password.trim().toString())
            .addOnCompleteListener(OnCompleteListener<AuthResult> {
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

                    reference.setValue(hashMap).addOnCompleteListener(OnCompleteListener<Void>(){
                        if (it.isSuccessful){
                            navigator.hideLoading()
                            regiterSuccess.value=true
                        }

                    })
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

}
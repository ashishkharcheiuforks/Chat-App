package com.example.android.kotlinchatapp.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.android.kotlinchatapp.R
import com.example.android.kotlinchatapp.ui.login.LoginActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        supportActionBar?.title="Reset Password"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        firebaseAuth=FirebaseAuth.getInstance()
        reset_btn.setOnClickListener(View.OnClickListener {
            val email =email_txt.text.toString().trim()
            if (email.isEmpty()){
                Toast.makeText(this,"all fileds are required",Toast.LENGTH_LONG).show()
            }
            else{
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(OnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this,"please check your email",Toast.LENGTH_LONG).show()
                        startActivity(Intent(applicationContext,
                            LoginActivity::class.java))

                    }
                    else{
                        Toast.makeText(this,it.exception?.message,Toast.LENGTH_LONG).show()

                    }
                })

            }
        })
    }
}

package com.example.android.kotlinchatapp.ui.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.android.kotlinchatapp.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    lateinit var auth:FirebaseAuth
     var firebaseUser:FirebaseUser?=null
    lateinit var progressDialog:ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        progressDialog= ProgressDialog(this)
        progressDialog.setMessage("loading")
        progressDialog.setCancelable(false)
        supportActionBar?.title="Login"
        auth=FirebaseAuth.getInstance()

        firebaseUser=auth.currentUser

        if (firebaseUser!=null){
            val i= Intent(this, HomeActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            finish()
        }

        reset_password.setOnClickListener(View.OnClickListener {
            startActivity(Intent(applicationContext,
                ResetPasswordActivity::class.java))
        })

        btn_login.setOnClickListener(View.OnClickListener {
            progressDialog.show()
            login()
        })
        register_txt.setOnClickListener(View.OnClickListener {
            val i= Intent(applicationContext, RegisterActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            finish()
        })
    }

    private fun login() {
        auth.signInWithEmailAndPassword(email.text.toString(),password.text.toString())
            .addOnCompleteListener(OnCompleteListener<AuthResult>(){
                if (it.isSuccessful){

                    val i= Intent(applicationContext, HomeActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(i)
                    progressDialog.dismiss()

                }
                else {
                    Toasty.error(applicationContext, it.exception?.message.toString(), Toasty.LENGTH_LONG).show()
                    progressDialog.dismiss()
                }
            })
    }
}

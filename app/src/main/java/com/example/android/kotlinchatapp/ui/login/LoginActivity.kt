package com.example.android.kotlinchatapp.ui.login

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.android.kotlinchatapp.R
import com.example.android.kotlinchatapp.databinding.ActivityLoginBinding
import com.example.android.kotlinchatapp.ui.activities.HomeActivity
import com.example.android.kotlinchatapp.ui.registeration.RegisterActivity
import com.example.android.kotlinchatapp.ui.activities.ResetPasswordActivity
import es.dmoral.toasty.Toasty

class LoginActivity : AppCompatActivity(),LoginNavigator {
    //    lateinit var auth:FirebaseAuth
//     var firebaseUser:FirebaseUser?=null
    lateinit var binding: ActivityLoginBinding
    lateinit var loginViewModel: LoginViewModel
    lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        binding.vm = loginViewModel
        loginViewModel.navigator=this
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("loading")
        progressDialog.setCancelable(false)
        supportActionBar?.title = "Login"
        loginViewModel.navigateToRegisteration.observe(this, Observer {
            startActivity(Intent(this, RegisterActivity::class.java))
        })
        loginViewModel.navigateToForgetPassword.observe(this, Observer {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        })
        loginViewModel.error.observe(this, Observer {
            Toasty.error(applicationContext, it, Toasty.LENGTH_LONG).show()
            progressDialog.dismiss()
        })
        loginViewModel.loginSuccess.observe(this, Observer {
            val i = Intent(this, HomeActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            progressDialog.dismiss()
        })

//        auth=FirebaseAuth.getInstance()
//
//        firebaseUser=auth.currentUser
//
//        if (firebaseUser!=null){
//            val i= Intent(this, HomeActivity::class.java)
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(i)
//            finish()
//        }
//
//        reset_password.setOnClickListener(View.OnClickListener {
//            startActivity(Intent(applicationContext,
//                ResetPasswordActivity::class.java))
//        })

//        btn_login.setOnClickListener(View.OnClickListener {
//            progressDialog.show()
////            login()
//        })
//        register_txt.setOnClickListener(View.OnClickListener {
//            val i= Intent(applicationContext, RegisterActivity::class.java)
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(i)
//            finish()
//        })
    }

    override fun showLoading() {
        progressDialog.show()
    }

    override fun hideLoading() {
        progressDialog.dismiss()
    }

//    private fun login() {
//        auth.signInWithEmailAndPassword(email.text.toString(),password.text.toString())
//            .addOnCompleteListener(OnCompleteListener<AuthResult>(){
//                if (it.isSuccessful){
//
//                    val i= Intent(applicationContext, HomeActivity::class.java)
//                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    startActivity(i)
//                    progressDialog.dismiss()
//
//                }
//                else {
//                    Toasty.error(applicationContext, it.exception?.message.toString(), Toasty.LENGTH_LONG).show()
//                    progressDialog.dismiss()
//                }
//            })
//    }
}

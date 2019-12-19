package com.example.android.kotlinchatapp.ui.registeration

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.android.kotlinchatapp.R
import com.example.android.kotlinchatapp.databinding.ActivityRegisterBinding
import com.example.android.kotlinchatapp.ui.activities.HomeActivity
import com.example.android.kotlinchatapp.ui.activities.ResetPasswordActivity
import com.example.android.kotlinchatapp.ui.login.LoginActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity(), RegistraionNavigator {
    //    lateinit var auth: FirebaseAuth
//    lateinit var reference: DatabaseReference
    lateinit var progressDialog: ProgressDialog
    lateinit var binding: ActivityRegisterBinding
    lateinit var registerationViewModel: RegisterationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        binding.lifecycleOwner = this
        registerationViewModel=ViewModelProviders.of(this).get(RegisterationViewModel::class.java)
        binding.vm=registerationViewModel
        registerationViewModel.navigator=this
        supportActionBar?.title = "Register"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("loading")
        progressDialog.setCancelable(false)
        registerationViewModel.navigateToLogin.observe(this, Observer {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        })
        registerationViewModel.error.observe(this, Observer {
            Toasty.error(applicationContext, it, Toasty.LENGTH_LONG).show()
            progressDialog.dismiss()
        })
        registerationViewModel.regiterSuccess.observe(this, Observer {
            val i = Intent(this, HomeActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            progressDialog.dismiss()
        })

//        auth = FirebaseAuth.getInstance()

//        btn_register.setOnClickListener(View.OnClickListener {
//            progressDialog.show()
//            register()
//        })
//
//        login_txt.setOnClickListener(View.OnClickListener {
//            val i= Intent(this, LoginActivity::class.java)
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


//    private fun register() {
//        auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
//            .addOnCompleteListener(OnCompleteListener<AuthResult> {
//                if(it.isSuccessful){
//                    val firebaseUser=auth.currentUser
//                    val userId=firebaseUser!!.uid
//                    reference= FirebaseDatabase.getInstance().getReference("Users").child(userId)
////                    val user= User(userId,"default",userName.text.toString())
//                    val hashMap=HashMap<String,String>()
//                    hashMap?.put("id",userId)
//                    hashMap?.put("userName",userName.text.toString())
//
//                    hashMap?.put("imageURL","default")
//                    hashMap?.put("search",userName.text.toString().toLowerCase())
//
//                    reference.setValue(hashMap).addOnCompleteListener(OnCompleteListener<Void>(){
//                        if (it.isSuccessful){
//                            val i= Intent(this, HomeActivity::class.java)
//                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                            startActivity(i)
//                            progressDialog.dismiss()
//
//                        }
//
//                    })
//                }
//                else{
//                    Toast.makeText(this,it.exception!!.message,Toast.LENGTH_LONG).show()
//                    progressDialog.dismiss()
//                }
//            }
//            )
//    }
}

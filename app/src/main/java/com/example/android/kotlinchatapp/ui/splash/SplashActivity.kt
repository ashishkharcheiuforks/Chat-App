package com.example.android.kotlinchatapp.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.android.kotlinchatapp.R
import com.example.android.kotlinchatapp.databinding.ActivitySplashBinding
import com.example.android.kotlinchatapp.ui.activities.HomeActivity
import com.example.android.kotlinchatapp.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    lateinit var binding:ActivitySplashBinding
    lateinit var splashViewModel: SplashViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_splash)
        binding.lifecycleOwner=this
        splashViewModel=ViewModelProviders.of(this).get(SplashViewModel::class.java)
        binding.vm=splashViewModel

        var fromTop=AnimationUtils.loadAnimation(this,R.anim.fromtop)
        binding.splashMainIcon.animation=fromTop
        val fromRight=AnimationUtils.loadAnimation(this,R.anim.fromright)
        binding.splashIconSender.animation=fromRight
        val fromleft=AnimationUtils.loadAnimation(this,R.anim.fromleft)
        binding.splashIconReceiver.animation=fromleft
        splashViewModel.checkLoging()
        splashViewModel.startHandler()
        splashViewModel.navigate.observe(this, Observer {
            if (splashViewModel.logedin){
                startActivity(Intent(this,HomeActivity::class.java))
            }
            else{
                val intent=Intent(this,
                    LoginActivity::class.java)
                val option = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    splash_main_icon,
                    "logo"
                )
                startActivity(intent, option.toBundle())

            }
        })
    }
}

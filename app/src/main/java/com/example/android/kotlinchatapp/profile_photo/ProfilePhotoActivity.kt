package com.example.android.kotlinchatapp.profile_photo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.android.kotlinchatapp.R
import kotlinx.android.synthetic.main.activity_profile_photo.*

class ProfilePhotoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_photo)
        title="Profile Image"
//        actionBar.setDisplayShowHomeEnabled(true)
        intent.getStringExtra("profilePhoto")?.let { Glide.with(this).load(it).into(imageView) }
    }
}

package com.example.android.kotlinchatapp.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.android.kotlinchatapp.R

object BindingUtils {
    @BindingAdapter("app:image")
    @JvmStatic
    fun bindImage(view: ImageView, image: String?) {
        if (image!=null){
            Glide.with(view.context).load(image).error(R.drawable.placeholder).into(view)
        }
    }
    @BindingAdapter("app:profileImage")
    @JvmStatic
    fun bindProfileImage(view: ImageView, image: String?) {
        if (image!=null){
            Glide.with(view.context).load(image).error(R.drawable.profile_default_icon).into(view)
        }
    }
}
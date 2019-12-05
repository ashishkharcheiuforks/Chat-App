package com.example.android.kotlinchatapp.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object BindingUtils {
    @BindingAdapter("app:image")
    @JvmStatic
    fun bindImage(view: ImageView, image: String?) {
        if (image!=null){
            Glide.with(view.context).load(image).into(view)
        }
    }
}
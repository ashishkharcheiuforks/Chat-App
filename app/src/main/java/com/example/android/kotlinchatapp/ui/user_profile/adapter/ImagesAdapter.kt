package com.example.android.kotlinchatapp.ui.user_profile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android.kotlinchatapp.R
import com.example.android.kotlinchatapp.databinding.ItemUserImagesBinding
import com.example.android.kotlinchatapp.ui.model.Chat
import com.example.android.kotlinchatapp.ui.user_profile.OnItemClick

class ImagesAdapter(var images: ArrayList<Chat>) : RecyclerView.Adapter<ImagesAdapter.Holder>() {
lateinit var onItemClick: OnItemClick
lateinit var con:Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        con=parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemUserImagesBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_user_images, parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.onBind(images[position])
        holder.itemView.setOnClickListener {
            if (onItemClick!=null){
                onItemClick.onClick(images[position].message!!,holder.binding.imageView30)
            }
        }
    }

    inner class Holder(var binding: ItemUserImagesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(image: Chat) {
            binding.chat = image
        }
    }
}
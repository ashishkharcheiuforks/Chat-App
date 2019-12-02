package com.example.android.myapplication.Adapter

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.example.android.kotlinchatapp.activities.MessageActivity
import com.example.android.kotlinchatapp.Model.User
import com.example.android.kotlinchatapp.R
import kotlinx.android.synthetic.main.user_item.view.*


class UserAdapter(private val mContext: Context?, private val users: List<User>,private val isChat:Boolean) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false)
        val holder = ViewHolder(view)
        return holder
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val user = users.get(i)
        viewHolder.username.text = user.userName
        if (user.imageURL == "default") {
            viewHolder.profile_image.setImageResource(R.mipmap.ic_launcher_round)
        } else
            Glide.with(mContext!!).load(user.imageURL).into(viewHolder.profile_image).waitForLayout()

        if (isChat){
            if (user.status.equals("online")){
                viewHolder.img_online.visibility = VISIBLE
                viewHolder.img_offline.visibility= GONE
            }
            else{
                viewHolder.img_online.visibility = GONE
                viewHolder.img_offline.visibility= VISIBLE
            }
        }
        else{
            viewHolder.img_online.visibility = GONE
            viewHolder.img_offline.visibility= GONE
        }

        viewHolder.itemView.setOnClickListener {
            val i = Intent(mContext, MessageActivity::class.java)
            i.putExtra("userid", user.id)
            i.putExtra("imageURL",user.imageURL)
            mContext!!.startActivity(i)
        }

    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var username: TextView
        var profile_image: ImageView
        var img_online:ImageView
        var img_offline:ImageView
        init {
            username = itemView.user_name
            profile_image = itemView.profile_image
            img_offline = itemView.img_offline
            img_online = itemView.img_online
        }
    }
}

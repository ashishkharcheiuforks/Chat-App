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
import com.example.android.kotlinchatapp.ui.message_activity.MessageActivity
import com.example.android.kotlinchatapp.ui.model.User
import com.example.android.kotlinchatapp.R
import com.example.android.kotlinchatapp.ui.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.user_item.view.*


class UserAdapter(
    private val mContext: Context?,
    private val users: List<User>,
    private val isChat: Boolean
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val refrence = FirebaseDatabase.getInstance().getReference("Chats")
    val myId = firebaseUser?.let { it.uid}?:""
    lateinit var con: Context
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        con = viewGroup.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false)
        val holder = ViewHolder(view)
        return holder
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val user = users.get(i)
        viewHolder.username.text = user.userName
//        if (user.imageURL == "default") {
//            viewHolder.profile_image.setImageResource(R.mipmap.ic_launcher_round)
//        } else
        Glide.with(mContext!!).load(user.imageURL).error(R.drawable.profile_default_icon)
            .into(viewHolder.profile_image).waitForLayout()

        if (isChat) {
            if (user.status.equals("online")) {
                viewHolder.img_online.visibility = VISIBLE
                viewHolder.img_offline.visibility = GONE
            } else {
                viewHolder.img_online.visibility = GONE
                viewHolder.img_offline.visibility = VISIBLE
            }
            getLastMessage(users[i].id!!, viewHolder.lastMessage)
            viewHolder.lastMessage.visibility = VISIBLE

        } else {
            viewHolder.img_online.visibility = GONE
            viewHolder.img_offline.visibility = GONE
        }

        viewHolder.itemView.setOnClickListener {
            val i = Intent(mContext, MessageActivity::class.java)
            i.putExtra("userid", user.id)
            i.putExtra("imageURL", user.imageURL)
            mContext!!.startActivity(i)
        }

    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var username: TextView
        var profile_image: ImageView
        var img_online: ImageView
        var img_offline: ImageView
        var lastMessage: TextView

        init {
            username = itemView.user_name
            profile_image = itemView.profile_image
            img_offline = itemView.img_offline
            img_online = itemView.img_online
            lastMessage = itemView.lastMessage
        }
    }

    fun getLastMessage(userId: String, last_msg: TextView) {
        var lastMessage: Chat? = null
        refrence.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot in p0.children) {
                    var chat = snapshot.getValue(Chat::class.java)
                    if (chat!!.reciever.equals(myId) && chat.sender.equals(userId) || chat.reciever.equals(
                            userId
                        ) && chat.sender.equals(myId)
                    ) {
                        lastMessage = chat
                    }
                }
                lastMessage?.let {
                    if (lastMessage!!.type.equals("image")) {
                        last_msg.text = "Image"
                        last_msg.setCompoundDrawablesWithIntrinsicBounds(
                            if(!lastMessage!!.isseen!!&& lastMessage!!.reciever==myId)R.drawable.ic_camera_un_read_24dp else R.drawable.ic_black_camera,
                            0,
                            0,
                            0
                        )
                    } else {
                        last_msg.text = it.message
                        last_msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    }

                    if (!lastMessage!!.isseen!!&& lastMessage!!.reciever==myId){
                        last_msg.setTextColor(con.getColor(R.color.un_read_message))
                    }
                }
            }


        })
    }
}

package com.example.android.kotlinchatapp.ui.message_activity.adapter

import android.content.Context
import android.os.Build
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi


import com.bumptech.glide.Glide
import com.example.android.kotlinchatapp.ui.model.Chat
import com.example.android.kotlinchatapp.ui.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.example.android.kotlinchatapp.R
import com.example.android.kotlinchatapp.ui.message_activity.OnClickItem
import com.example.android.kotlinchatapp.utils.FormatDate


class MessageAdapter(private val mContext: Context, private val mChats: List<Chat>, private val userImgURL: String,private val currentUser: User) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    internal var firebaseUser: FirebaseUser? = null
    lateinit var onClickItem: OnClickItem
    lateinit var context: Context
    var scrollDirection = ScrollDirection.DOWN

    val formatDate=FormatDate
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        context=viewGroup.context
        if (i == MSG_TYPE_SENDER) {
            val view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_sender, viewGroup, false)
            val holder=ViewHolder(view)
            return holder
        } else {
            val view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_reciever, viewGroup, false)
            val holder=ViewHolder(view)
            return holder
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val chat = mChats[i]
        viewHolder.onBind(i)
        if (chat.type.equals("image")){
            viewHolder.show_image.visibility=VISIBLE
            viewHolder.show_message.visibility= GONE
            Glide.with(mContext).load(chat.message).error(R.drawable.placeholder).into(viewHolder.show_image)
            viewHolder.show_image.setOnClickListener {
                onClickItem.onClick(chat.message!!,viewHolder.show_image)
                viewHolder.show_message.transitionName=chat.message
            }

        }
        else{
            viewHolder.show_image.visibility=GONE
            viewHolder.show_message.visibility= VISIBLE
            viewHolder.show_message.text = chat.message
        }


        if (chat.sender.equals(currentUser.id)) {
//            if (userImgURL == ("default"))
//                viewHolder.profile_image.setImageResource(R.mipmap.ic_launcher_round)
//            else
                Glide.with(mContext).load(currentUser.imageURL).error(R.drawable.profile_default_icon).into(viewHolder.profile_image)
        }
        else{
//            if (userImgURL == ("default"))
//                viewHolder.profile_image.setImageResource(R.mipmap.ic_launcher_round)
//            else
                Glide.with(mContext).load(userImgURL).error(R.drawable.profile_default_icon).into(viewHolder.profile_image)
        }
        if (i==mChats.size-1&&chat.sender.equals(currentUser.id)){
            if (chat.isseen!!){
                viewHolder.txt_seen.text="Seen"
            }
            else
                viewHolder.txt_seen.text="Delivered"
        }
        else{
            viewHolder.txt_seen.visibility=GONE
        }
        viewHolder.date.text=chat.date?.let { formatDate.getDate(it) }?:""

//        viewHolder.show_image.setOnClickListener {
//            viewHolder.show_image.setLayoutParams(
//                LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.MATCH_PARENT
//                )
//            )
//            viewHolder.show_image.setScaleType(ImageView.ScaleType.FIT_XY)
//            //zoomOut = true
//        }

    }

    override fun getItemCount(): Int {
        return mChats.size
    }



    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        return if (mChats[position].sender == firebaseUser!!.uid)
            MSG_TYPE_SENDER
        else
            MSG_TYPE_RECIEVER
    }

    companion object {

        val MSG_TYPE_RECIEVER = 0
        val MSG_TYPE_SENDER = 1
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var show_message: TextView
        var profile_image: ImageView
        var txt_seen:TextView
        var show_image:ImageView
        var date:TextView
        fun onBind(position:Int){
            animateView(itemView)
        }
        init {
            show_message = itemView.findViewById(R.id.show_message)
            profile_image = itemView.findViewById(R.id.profile_image)
            txt_seen = itemView.findViewById(R.id.txt_seen)
            show_image=itemView.findViewById(R.id.show_image)
            date=itemView.findViewById(R.id.messageDate)
        }
        fun animateView(viewToAnimate: View) {
            if (viewToAnimate.animation == null) {
                if (scrollDirection == ScrollDirection.UP) {
                    val animation = AnimationUtils.loadAnimation(
                        viewToAnimate.context,
                        R.anim.slide_from_top
                    )
                    viewToAnimate.animation = animation
                }
            }
        }
    }
    enum class ScrollDirection {
        UP, DOWN
    }
}

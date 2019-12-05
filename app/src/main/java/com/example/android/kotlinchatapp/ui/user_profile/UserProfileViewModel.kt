package com.example.android.kotlinchatapp.ui.user_profile

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.example.android.kotlinchatapp.R
import com.example.android.kotlinchatapp.ui.message_activity.OnClickItem
import com.example.android.kotlinchatapp.ui.message_activity.adapter.MessageAdapter
import com.example.android.kotlinchatapp.ui.model.Chat
import com.example.android.kotlinchatapp.ui.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class UserProfileViewModel : ViewModel() {
    private var firebaseStore: FirebaseStorage? = FirebaseStorage.getInstance()
    private var storageReference: StorageReference? = FirebaseStorage.getInstance().reference
    var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
    val myid = firebaseUser.uid
    lateinit var reference: DatabaseReference
    var user = MutableLiveData<User>()
    var userId = ""
    var images = MutableLiveData<ArrayList<Chat>>()
    fun getUserData() {
        reference =
            FirebaseDatabase.getInstance().getReference("Users").child(userId)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user.value = dataSnapshot.getValue(User::class.java)!!
            }

        })

    }

    fun getUserImages() {
        val mChats = ArrayList<Chat>()
        reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mChats.clear()
                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if ((chat!!.reciever == myid && chat.sender == userId || chat.reciever == userId && chat.sender == myid) && chat.type == "image") {
                        mChats.add(chat)
                    }
                }
                images.value=mChats
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}
package com.example.android.kotlinchatapp.ui.chats

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.kotlinchatapp.ui.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatsViewModel : ViewModel() {
    var lastMessage = MutableLiveData<Chat>()
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val refrence = FirebaseDatabase.getInstance().getReference("Chats")
    val myId = firebaseUser!!.uid
    fun getLastMessage(userId: String) {
        refrence.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var message = Chat()
                for (snapshot in p0.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat!!.reciever.equals(myId) && chat.sender.equals(userId) || chat.reciever.equals(
                            userId
                        ) && chat.sender.equals(myId)
                    ) {
                        message = chat
                    }
                }
                lastMessage.value = message
            }

        })
    }
}
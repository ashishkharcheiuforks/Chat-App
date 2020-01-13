package com.example.android.kotlinchatapp.ui.notification

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseIdService :FirebaseMessagingService() {
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        val firebaseUser=FirebaseAuth.getInstance().currentUser
        val newToken=FirebaseInstanceId.getInstance().token
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener{
                if (it.isSuccessful) {
                    val token = it.result!!.token
                    if (firebaseUser!=null)
                        updateToken(token)

                }
            }
    }

    private fun updateToken(newToken: String?) {
        val firebaseUser=FirebaseAuth.getInstance().currentUser
        val reference=FirebaseDatabase.getInstance().getReference("Tokens")
        val token=Token(newToken!!)
        reference.child(firebaseUser?.uid!!).setValue(token)
    }
}
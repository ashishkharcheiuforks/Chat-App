package com.example.android.kotlinchatapp.ui.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileViewModel : ViewModel() {
    private var firebaseStore: FirebaseStorage? = FirebaseStorage.getInstance()
    private var storageReference: StorageReference? = FirebaseStorage.getInstance().reference
    var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
    var reference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
    lateinit var profileNavigator: ProfileNavigator

    fun editProfile(key: String, value: String) {
        reference.child(key).setValue(value)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (key == "userName")
                        reference.child("search").setValue(value.toLowerCase())
                }
                profileNavigator.editSuccess()
            }.addOnFailureListener {
                profileNavigator.editError("error occurred")
            }

    }

    fun validate(key: String, value: String) {
        if (value.isNotEmpty()) {
            when (key) {
                "userName" -> {
                    if (value.length < 5)
                        profileNavigator.editError("name should be more than 5 characters")
                    else
                        editProfile(key, value)
                }

                "bio" -> {
                    if (value.length < 10)
                        profileNavigator.editError("bio should be more than 10 characters")
                    else
                        editProfile(key, value)
                }
                "phone" -> {
                    if (value.length < 11)
                        profileNavigator.editError("invalid phone number")
                    else
                        editProfile(key, value)
                }
            }
        } else {
            profileNavigator.editError("field should not be empty")
        }

    }
}

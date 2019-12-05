package com.example.android.kotlinchatapp.ui.model

import android.os.Parcel
import android.os.Parcelable

class User() : Parcelable {
    var status: String?=null
    var id: String?=null
    var imageURL: String?=null
    var userName: String?=null
    var search: String?=null
    var lastSeen: String? = null
    var bio:String?=null
    var phone:String?=null

    fun getCurrentStatus():String{
        if (status=="online")
            return status!!
        else
            return lastSeen?.let { it }?: kotlin.run { "" }
    }


    constructor(parcel: Parcel) : this() {
        status = parcel.readString()
        id = parcel.readString()
        imageURL = parcel.readString()
        userName = parcel.readString()
        search = parcel.readString()
        lastSeen = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(status)
        parcel.writeString(id)
        parcel.writeString(imageURL)
        parcel.writeString(userName)
        parcel.writeString(search)
        parcel.writeString(lastSeen)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
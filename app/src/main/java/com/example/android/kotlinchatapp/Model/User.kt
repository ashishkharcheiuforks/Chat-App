package com.example.android.kotlinchatapp.Model

import java.util.*
import kotlin.collections.HashMap

class User() {
    lateinit var status:String
    lateinit var id: String
    lateinit var imageURL: String
    lateinit var userName: String
    lateinit var search:String
    var lastSeen:String?=null
    val hashMap: HashMap<String, String>
        get() {
            val hashMap = HashMap<String, String>()
            hashMap?.put("id", id)
            hashMap?.put("userName", userName)

            hashMap?.put("imageURL", imageURL)
            return hashMap
        }
}
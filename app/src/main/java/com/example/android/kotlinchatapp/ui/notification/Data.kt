package com.example.android.kotlinchatapp.ui.notification

class Data {
    lateinit var user:String
     var icon:Int=0
    lateinit var body :String
    lateinit var title:String
    lateinit var sented:String

    constructor(user: String, icon: Int, body: String, title: String, sented: String) {
        this.user = user
        this.icon = icon
        this.body = body
        this.title = title
        this.sented = sented
    }
}
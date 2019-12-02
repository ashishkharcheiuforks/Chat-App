package com.example.android.kotlinchatapp.Model

class Chat {
    var sender: String? = null
    var reciever: String? = null
    var message: String? = null
    var isseen:Boolean?=null
    var type:String?=null

    constructor() {

    }

    constructor(sender: String, reciever: String, message: String,isseen:Boolean,type:String) {
        this.sender = sender
        this.reciever = reciever
        this.message = message
        this.isseen=isseen
        this.type=type
    }
}

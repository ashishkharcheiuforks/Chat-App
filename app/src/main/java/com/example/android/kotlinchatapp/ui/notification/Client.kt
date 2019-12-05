package com.example.android.kotlinchatapp.ui.notification

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Client {
    companion object{
    private var retrofit:Retrofit?=null

    @JvmStatic
    public fun getClient(url:String):Retrofit{
        if (retrofit==null){
            retrofit=Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
    }
}
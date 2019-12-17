package com.example.android.kotlinchatapp.utils

import android.os.Build
import android.text.format.DateUtils
import android.util.Log
import androidx.annotation.RequiresApi
import org.joda.time.DateTimeComparator
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

object FormatDate {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDate(date:String):String{
        val mTime=date.split(" ")[1]
        val mDate=date.split(" ")[0]
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val time = sdf.parse(date)!!.time
        val now = Date().time
        val ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.SECOND_IN_MILLIS)
        val days = TimeUnit.MILLISECONDS.toDays(now - time)
        return when {
            days.toInt()==0 -> "today at $mTime"
            days.toInt()==1 -> "yesterday at $mTime"
            else -> "$mDate at $mTime"
        }

    }
}
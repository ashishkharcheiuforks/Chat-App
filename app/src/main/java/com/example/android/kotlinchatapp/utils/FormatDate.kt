package com.example.android.kotlinchatapp.utils

import android.annotation.SuppressLint
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
    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDate(date: String): String {
        val format=SimpleDateFormat("yyyy-MM-dd hh:mm aa")
        val formatForTime=SimpleDateFormat("hh:mm aa")
        val formatForDay=SimpleDateFormat("EE",Locale.ENGLISH)
        val formatForDate=SimpleDateFormat("DD MMM",Locale.ENGLISH)
        val messageDate=format.parse(date)
        val mTime = date.split(" ")[1]
        val mDate = date.split(" ")[0]
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val time = sdf.parse(date)!!.time
        val now = Date().time
        val ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.SECOND_IN_MILLIS)
        val days = TimeUnit.MILLISECONDS.toDays(now - time)
        return when {
            days.toInt() == 0 -> "today at ${formatForTime.format(messageDate)}"
            days.toInt() == 1 -> "yesterday at ${formatForTime.format(messageDate)}"
            days.toInt() in 2..6-> "${formatForDay.format(messageDate)} at ${formatForTime.format(messageDate)}"
            days.toInt() in 7..365 -> "${formatForDate.format(messageDate)} at ${formatForTime.format(messageDate)}"
            else -> "$mDate at ${formatForTime.format(messageDate)}"
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun currentdateinDdMmYyyyHhMmA(): String {
        val date = LocalDateTime.now()
        val formater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a")
        val formatedDate = date.format(formater)
        return formatedDate
    }

    fun dateFormatter_v3(time: String): Date {
        var date = time
        //17-10-2019T12:55 PM
        var spf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH)
        return spf.parse(date)
    }

    fun getDateForLastMessage(date: String): String{
        val format=SimpleDateFormat("yyyy-MM-dd hh:mm")
        val formatForTime=SimpleDateFormat("hh:mm aa")
        val formatForDay=SimpleDateFormat("EE",Locale.ENGLISH)
        val formatForDate=SimpleDateFormat("DD MMM")
        val messageDate=format.parse(date)
        val messageTime=formatForTime.format(messageDate)
        val mTime = date.split(" ")[1]
        val mDate = date.split(" ")[0]
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val time = sdf.parse(date)!!.time

        val now = Date().time
        val ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.SECOND_IN_MILLIS)
        val days = TimeUnit.MILLISECONDS.toDays(now - time)
        Log.e("asdasd",messageDate.toString())

        Log.e("asdasd",formatForTime.format(messageDate))
        Log.e("asdasdsssss",formatForDay.format(messageDate))
        Log.e("asdasd",formatForDate.format(messageDate))

        return when {
            days.toInt() == 0 -> formatForTime.format(messageDate)
            days.toInt() in 1..6 -> formatForDay.format(messageDate)
            days.toInt() in 7..365 -> formatForDate.format(messageDate)
            else -> mDate
        }
    }
}
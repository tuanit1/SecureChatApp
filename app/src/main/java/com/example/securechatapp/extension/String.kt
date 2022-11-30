package com.example.securechatapp.extension

import android.util.Base64
import android.widget.Toast
import java.io.UnsupportedEncodingException
import java.text.SimpleDateFormat
import java.util.*


fun String.decodeBase64(): String{
    var text1 = ""
    try {
        val data1: ByteArray = Base64.decode(this, Base64.DEFAULT)
        text1 = String(data1, Charsets.UTF_8)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return text1.trim()
}

fun String.encodeBase64(): String{
    var data: ByteArray? = null
    try {
        data = toByteArray(charset("UTF-8"))
    } catch (e1: Exception) {
        e1.printStackTrace()
    }
    return Base64.encodeToString(data, Base64.DEFAULT).trim()
}

fun String.toFormattedDate(): String{

    return try {
        val sdf = SimpleDateFormat("dd/M/yy • hh:mm", Locale.US)
        val sdfUTC = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val date = sdfUTC.parse(this)

        var timeString = ""

        date?.let {
            timeString = sdf.format(date)
        }

        timeString
    }catch (e: Exception){
        ""
    }

}

fun getDiffInMinute(dateString1: String, dateString2: String): Float{
    val sdfUTC = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    val date1 = sdfUTC.parse(dateString1)
    val date2 = sdfUTC.parse(dateString2)

    if(date1 != null && date2 != null){
        val diff: Long = date1.time - date2.time
        val seconds = diff / 1000f
        val minutes = seconds / 60f
//        val hours = minutes / 60
//        val days = hours / 24

        return minutes
    }else{
        return 0f
    }





}
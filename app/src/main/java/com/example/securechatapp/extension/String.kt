package com.example.securechatapp.extension

import android.util.Base64
import java.io.UnsupportedEncodingException
import java.text.SimpleDateFormat
import java.util.*


fun String.decodeBase64(): String{
    val data1: ByteArray = Base64.decode(this, Base64.DEFAULT)
    var text1 = ""
    try {
        text1 = String(data1, Charsets.UTF_8)
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
    }

    return text1
}

fun String.encodeBase64(): String{
    var data: ByteArray? = null
    try {
        data = toByteArray(charset("UTF-8"))
    } catch (e1: UnsupportedEncodingException) {
        e1.printStackTrace()
    }
    return Base64.encodeToString(data, Base64.DEFAULT)
}

fun String.toFormattedDate(): String{
    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale.US)
    val sdfUTC = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
    val date = sdfUTC.parse(this)

    var timeString = ""

    if(date != null){
        timeString = sdf.format(date)
    }

    return timeString
}
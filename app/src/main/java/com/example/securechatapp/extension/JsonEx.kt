package com.example.securechatapp.extension

import com.google.gson.Gson
import java.util.Objects

fun Any.toJsonString(): String{
    return Gson().toJson(this)
}

inline fun <reified T> jsonToObject(json: String): T{
    return Gson().fromJson(json, T::class.java)
}
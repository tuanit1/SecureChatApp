package com.example.securechatapp.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import java.io.ByteArrayOutputStream

fun checkFileIsImage(context: Context?, uri: Uri): Boolean{
    val contentResolver = context?.contentResolver
    val cursor = contentResolver?.query(uri, null, null, null, null)
    var extension = ""

    val imageExList = listOf("jpg", "png", "gif", "jpeg")


    cursor.use {
        if (it != null && it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val filename = it.getString(nameIndex)
            extension = filename.split(".").last()
        }
    }

    return imageExList.contains(extension)
}

fun getFileName(context: Context?, uri: Uri): String?{
    val contentResolver = context?.contentResolver
    val cursor = contentResolver?.query(uri, null, null, null, null)
    var filename: String? = null

    cursor.use {
        if (it != null && it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            filename = it.getString(nameIndex)
        }
    }

    return filename
}

fun compressBitmap(bitmap: Bitmap): Bitmap{
    val newWidth = 1024
    val newHeight = (bitmap.height * (newWidth.toFloat() / bitmap.width)).toInt()
    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
}

fun compressBitmapFromUri(context: Context, uri: Uri): Bitmap {
    val bitmap = if (Build.VERSION.SDK_INT < 28) {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    } else {
        val source: ImageDecoder.Source =
            ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }

    val newWidth = 1024
    val newHeight = (bitmap.height * (newWidth.toFloat()/ bitmap.width)).toInt()
    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
}
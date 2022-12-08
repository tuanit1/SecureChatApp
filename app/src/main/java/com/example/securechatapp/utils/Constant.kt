package com.example.securechatapp.utils

import android.os.Environment

object Constant {
    const val SERVER_URL = "10.0.24.249"
    var DOWNLOAD_PATH =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString() + "/" + "SecureChatApp"
    var mUID = ""
}
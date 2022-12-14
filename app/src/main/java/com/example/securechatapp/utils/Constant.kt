package com.example.securechatapp.utils

import android.os.Environment

object Constant {
    const val SERVER_URL = "https://pbl6-attt-chatappsecure.onrender.com"
    var DOWNLOAD_PATH =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString() + "/" + "SecureChatApp"
    var mUID = ""
    var mIsExpiredDialogShowed: Boolean = false
}
package com.example.securechatapp.utils

import android.os.Environment

object Constant {
    const val SERVER_URL = "10.0.25.184"
    var DOWNLOAD_PATH =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString() + "/" + "SecureChatApp"
    var mUID = ""
    var mIsExpiredDialogShowed: Boolean = false
}
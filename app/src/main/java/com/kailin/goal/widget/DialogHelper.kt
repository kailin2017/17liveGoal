package com.kailin.goal.widget

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog

class DialogHelper private constructor() {

    fun msgDialog(context: Context, title: String = "", msg: String = ""): AlertDialog {
        Log.e(TAG, "msgDialog-title:$title,msg:$msg")
        return AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(msg)
            .show()
    }

    companion object {
        private const val TAG = "DialogHelper"
        val instance: DialogHelper by lazy { DialogHelper() }
    }
}
package com.changhong.wifiautoconnectforkotlin2

import android.util.Log

val TAG: String = MainService::class.java.`package`.name
inline fun log(message: String?) {
    Log.e(TAG, "[${Thread.currentThread().stackTrace[2].lineNumber}]${Thread.currentThread().stackTrace[2].methodName}: $message")
}
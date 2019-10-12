package com.changhong.wifiautoconnectforkotlin2

import android.util.Log

val TAG: String = MainService::class.java.`package`.name
fun log(message: String?) {
    Log.e(TAG, "[${Thread.currentThread().stackTrace[3].lineNumber}]${Thread.currentThread().stackTrace[3].methodName}: $message")
}


fun log2(message: String?) {
    Log.e(TAG, "[${Thread.currentThread().stackTrace[4].lineNumber}]${Thread.currentThread().stackTrace[4].methodName}: $message")
}
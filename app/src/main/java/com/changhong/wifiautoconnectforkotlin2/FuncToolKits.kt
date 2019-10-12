package com.changhong.wifiautoconnectforkotlin2

import android.util.Log

val TAG: String = MainService::class.java.`package`.name
fun log(message: String?) {
    Log.e(TAG, "[${Thread.currentThread().stackTrace[3].lineNumber}]${Thread.currentThread().stackTrace[3].methodName}: $message")
}


// 打印调用 log2 函数调用时的位置信息
fun log2(message: String?) {
    Log.e(TAG, "[${Thread.currentThread().stackTrace[5].lineNumber}]${Thread.currentThread().stackTrace[5].methodName}: $message")
}
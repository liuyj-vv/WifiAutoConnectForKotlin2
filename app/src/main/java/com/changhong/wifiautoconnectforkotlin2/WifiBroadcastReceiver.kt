package com.changhong.wifiautoconnectforkotlin2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class WifiBroadcastReceiver: BroadcastReceiver() {
    val TAG: String = WifiBroadcastReceiver::class.java.`package`.name
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action;
        Log.e(TAG, Thread.currentThread().stackTrace[2].methodName + "[" + Thread.currentThread().stackTrace[2].lineNumber + "]" + action)
    }
}
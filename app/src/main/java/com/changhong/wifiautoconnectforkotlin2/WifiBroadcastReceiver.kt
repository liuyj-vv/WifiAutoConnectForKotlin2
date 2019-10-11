package com.changhong.wifiautoconnectforkotlin2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class WifiBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        log(action)
    }
}

// logcat |grep com.changhong.wifiautoconnectforkotlin2
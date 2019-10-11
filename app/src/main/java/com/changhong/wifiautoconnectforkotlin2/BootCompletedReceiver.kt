package com.changhong. wifiautoconnectforkotlin2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class BootCompletedReceiver: BroadcastReceiver() {
    val TAG = BootCompletedReceiver::class.java.`package`.name
    override fun onReceive(context: Context?, intent: Intent?) {
        log(intent?.action)
        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            Toast.makeText(context, "收到开机广播 kotlin2 $TAG", Toast.LENGTH_LONG).show()
            val i = Intent(context, MainService::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startService(i)
        }
    }
}
package com.changhong.wifiautoconnectforkotlin2

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.util.Log
import java.io.File

class WifiBroadcastHandler {
    var context :Context
    var wifiManager:WifiManager
    var connectivityManager:ConnectivityManager
    constructor(context: Context) {
        this.context = context
        wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

     fun hardwareChanged(intent: Intent?) {
         val wifiState = intent?.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1)
         if (WifiManager.WIFI_STATE_ENABLED != wifiState) run {
             log("wifi 还未打开！")
             return
         } else {
            //wifi 已打开
             log("wifi 已打开！")

         }
    }

    fun scanResults(intent: Intent?) {
        log("")

    }

    fun networkIDSChanged(intent: Intent?) {
        log("")

    }

    fun networkStateChanged(intent: Intent?) {
        log("")

    }

    fun supplicantChanged(intent: Intent?) {
        log("")

    }

    fun rssiChanged(intent: Intent?) {
        log("")

    }
}
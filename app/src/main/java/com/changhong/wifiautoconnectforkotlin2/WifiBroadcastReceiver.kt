package com.changhong.wifiautoconnectforkotlin2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import android.support.annotation.RequiresApi

class WifiBroadcastReceiver: BroadcastReceiver {
    private var context:Context
    private var wifiBroadcastHandler: WifiBroadcastHandler

    constructor(context: Context?) {
        this.context = context!!
        wifiBroadcastHandler = WifiBroadcastHandler(context)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action

        if (!config.read()) {
            return
        }

        when (action){
            WifiManager.WIFI_STATE_CHANGED_ACTION -> wifiBroadcastHandler.hardwareChanged(intent)
            WifiManager.SUPPLICANT_STATE_CHANGED_ACTION -> wifiBroadcastHandler.supplicantChanged(intent)
            WifiManager.NETWORK_IDS_CHANGED_ACTION -> wifiBroadcastHandler.networkIDSChanged(intent)
            WifiManager.NETWORK_STATE_CHANGED_ACTION -> wifiBroadcastHandler.networkStateChanged(intent)
            WifiManager.SCAN_RESULTS_AVAILABLE_ACTION -> wifiBroadcastHandler.scanResults(intent)
            WifiManager.RSSI_CHANGED_ACTION -> wifiBroadcastHandler.rssiChanged(intent)
            "CONFIG_LOG1" -> {
                
            }
            "CONFIG_LOG1" -> {

            }
        }

    }
}


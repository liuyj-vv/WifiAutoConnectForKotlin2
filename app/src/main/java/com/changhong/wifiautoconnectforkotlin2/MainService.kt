package com.changhong.wifiautoconnectforkotlin2

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.IBinder
import android.util.Log

class MainService : Service() {
    private val TAG: String = MainService::class.java.`package`.name
    private lateinit var wifiReceiver: WifiBroadcastReceiver

    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()

        wifiRegister()
    }

    private fun wifiRegister() {
        wifiReceiver = WifiBroadcastReceiver()
        val filter = IntentFilter()
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)    //用于监听Android Wifi打开或关闭的状态，
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        filter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION)
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)
        //        filter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
        //        filter.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)//用于判断是否连接到了有效wifi（不能用于判断是否能够连接互联网）
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION)
        //        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("TEST_ACTION")
        filter.addAction("TEST_ACTION2")
        filter.addAction("TEST_ACTION3")
        filter.addAction("TEST_ACTION4")
        registerReceiver(wifiReceiver, filter)
        Log.e(TAG, "wifi广播监听注册")
    }
}
package com.changhong.wifiautoconnectforkotlin2

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.support.annotation.RequiresApi
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


    fun connectWifi(wifiManager: WifiManager, ssid: String, passwd: String, wifiType: Int): Boolean {
        // 开始连接
        val netId = wifiManager.addNetwork(wifiHelper.createWifiConfig(wifiManager, ssid, passwd, wifiType))
        if (-1 == netId) {
            log2("添加新的网络到配置文件失败， ssid： $ssid, wifiType:$wifiType====>> networkID: $netId")
            return false
        }

        if (!wifiManager.enableNetwork(netId, true)) {
            log2("使能新的网络描述id,失败, netId: $netId")
            return false
        }

        if (!wifiManager.reconnect()) {
            log2("尝试连接连接到网络失败！！！ 【reconnect】")
            return false
        }

        log2("尝试连接连接网络到， ssid： $ssid, wifiType:$wifiType====>> networkID: $netId")
        return true
    }

    fun connectWifiForce(wifiManager: WifiManager, ssid: String, passwd: String, wifiType: Int): Boolean {
        // 开始连接
        val netId = wifiManager.addNetwork(wifiHelper.createWifiConfig(wifiManager, ssid, passwd, wifiType))
        if (-1 == netId) {
            log2("添加新的网络到配置文件失败， ssid： $ssid, wifiType:$wifiType====>> networkID: $netId")
            return false
        }

        if (!wifiManager.enableNetwork(netId, true)) {
            log2("使能新的网络描述id,失败, netId: $netId")
            return false
        }

        if (!wifiManager.reassociate()) {
            log2("尝试连接连接到网络失败！！！ 【reassociate】")
            return false
        }

        log2("强制 尝试连接连接网络到， ssid： $ssid, wifiType:$wifiType====>> networkID: $netId")
        return true
    }

    private fun setWifiSingleFrequency() {
        val bs = "/proc/net/rtl88x2bs/wlan0/chan_plan"
        val cs = "/proc/net/rtl88x2cs/wlan0/chan_plan"
        val bsFile = File(bs)
        val csFile = File(cs)
        if (bsFile.exists()) {
            val cmd2_4G ="echo 0x21 >  $bs  && wpa_cli -iwlan0 -p /data/misc/wifi/sockets scan / && wpa_cli -iwlan0 -p /data/misc/wifi/sockets scan_results";
            val cmd5G = "echo 0x1f > $bs  && wpa_cli -iwlan0 -p /data/misc/wifi/sockets scan / && wpa_cli -iwlan0 -p /data/misc/wifi/sockets scan_results";
            if (config.value.wifi_frequency_band.equals("2.4G")) {
                ExecCmd().run(cmd2_4G)
            } else if (config.value.wifi_frequency_band.equals("5G")) {
                ExecCmd().run(cmd5G)
            } else {
                log("未配置2.4G或5G，使用双频模式！")
            }
        } else if (csFile.exists()) {
            val cmd2_4G = "echo 0x21 > $cs && wpa_cli -iwlan0 -p /data/misc/wifi/sockets scan / && wpa_cli -iwlan0 -p /data/misc/wifi/sockets scan_results";
            val cmd5G = "echo 0x1f > $cs && wpa_cli -iwlan0 -p /data/misc/wifi/sockets scan / && wpa_cli -iwlan0 -p /data/misc/wifi/sockets scan_results";
            if (config.value.wifi_frequency_band.equals("2.4G")) {
                ExecCmd().run(cmd2_4G);
            } else if (config.value.wifi_frequency_band.equals("5G")) {
                ExecCmd().run(cmd5G);
            } else {
                log("未配置2.4G或5G，使用双频模式！")
            }
        } else {
            log("经检测，机顶盒 wifi 不是 rtl 驱动，请检查！！！")
        }
    }


    private fun isContainConfigssid(): Boolean {
        var index: Int = 0
        val scanResultList = wifiManager.scanResults

        if (scanResultList.isEmpty()) {
            return false
        }

        while (index < scanResultList.size) {
            if (scanResultList[index].SSID == config.value.ssid) {
                // 扫描到的热点，有配置文件中的ssid
                // log(scanResultList[index].SSID)
                break
            }
            if (scanResultList.size == index + 1) {
                // 扫描到的热点，没有配置文件中的ssid
                return false
            }
            index++
        }

        return true
    }

    private fun isConnectConfigSsid(): Boolean {
        val wifiInfo = wifiManager.connectionInfo
        if (null != wifiInfo && null != wifiInfo.ssid) {
            if (wifiInfo.ssid == "\"" + config.value.ssid + "\"" && "\"" + config.value.ssid + "\"" == wifiInfo.ssid) {
                //连接上的热点就是配置文件中的热点，
                return true
            }
        }
        return false
    }

    fun hardwareChanged(intent: Intent?) {
        val wifiState = intent?.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1)
        if (WifiManager.WIFI_STATE_ENABLED != wifiState) {
            log("wifi 还未打开！${wifiManager.wifiState} $wifiState")
            return
        } else {
            //wifi 已打开
            log("wifi 已打开！${wifiManager.wifiState} $wifiState")
            setWifiSingleFrequency()
        }
        when (wifiState) {
            WifiManager.WIFI_STATE_ENABLED ->{}
            WifiManager.WIFI_MODE_FULL,
            WifiManager.WIFI_MODE_SCAN_ONLY ->{

            }
        }

        if(isConnectConfigSsid()) {
            // 当前正在连接的 wifi 热点就是配置文件中的 ssid
            return
        }

        if (isContainConfigssid()) {
            // 扫描的的热点包含配置文件中的 ssid
            connectWifiForce(wifiManager, config.value.ssid, config.value.passwd, Integer.parseInt(config.value.wifiType))
            return
        }

        // 无论是否，正在连接。没有连接配置文件中的 ssid，去连接配置文件中的 ssid
//        connectWifiForce(wifiManager, config.value.ssid, config.value.passwd, Integer.parseInt(config.value.wifiType))
    }

    @SuppressWarnings("deprecated")
    fun supplicantChanged(intent: Intent?) {
        val supplicantState = intent?.getParcelableExtra<SupplicantState>(WifiManager.EXTRA_NEW_STATE) //// 获取当前网络新状态.
        val errorNo = intent?.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1)      //// 获取当前网络连接状态码.

        log("scanSize: ${wifiManager.scanResults.size}, wifiManager: ${wifiManager.connectionInfo.ssid} ${wifiManager.connectionInfo.supplicantState}, Broadcast: $supplicantState")

        if (errorNo == WifiManager.ERROR_AUTHENTICATING) {
            log("$supplicantState --> 身份验证不通过!!!!")
        }

        when(supplicantState) {
            SupplicantState.INVALID,
            SupplicantState.SCANNING,
            SupplicantState.UNINITIALIZED,

            SupplicantState.ASSOCIATED,
            SupplicantState.AUTHENTICATING,
            SupplicantState.FOUR_WAY_HANDSHAKE,

            SupplicantState.COMPLETED,
            SupplicantState.DORMANT,
            SupplicantState.DISCONNECTED,
            SupplicantState.INACTIVE,
            SupplicantState.ASSOCIATING,
            SupplicantState.GROUP_HANDSHAKE,
            SupplicantState.INTERFACE_DISABLED ->{

            }
        }
    }

    fun networkIDSChanged(intent: Intent?) {
        log("================ ??????? ==================")
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun networkStateChanged(intent: Intent?) {
        val networkInfo = intent?.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
        val bssid = intent?.getStringExtra(WifiManager.EXTRA_BSSID)
        val wifiInfo = intent?.getParcelableExtra<WifiInfo>(WifiManager.EXTRA_WIFI_INFO)

        log("wifiManager: ${wifiManager.connectionInfo.ssid}, connectivityManager: ${connectivityManager.activeNetworkInfo.detailedState}, Broadcast: ${networkInfo?.detailedState}")

        when(connectivityManager.activeNetworkInfo.detailedState) {
            NetworkInfo.DetailedState.AUTHENTICATING,
            NetworkInfo.DetailedState.BLOCKED,
            NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK,
            NetworkInfo.DetailedState.CONNECTED,
            NetworkInfo.DetailedState.CONNECTING,
            NetworkInfo.DetailedState.DISCONNECTED,
            NetworkInfo.DetailedState.DISCONNECTING,
            NetworkInfo.DetailedState.FAILED,
            NetworkInfo.DetailedState.IDLE,
            NetworkInfo.DetailedState.OBTAINING_IPADDR, //正在获取IP地址
            NetworkInfo.DetailedState.SCANNING,
            NetworkInfo.DetailedState.SUSPENDED,
            NetworkInfo.DetailedState.VERIFYING_POOR_LINK -> {

            }
        }
    }

    fun scanResults(intent: Intent?) {
        log("wifiManager: ${wifiManager.scanResults.size},  ${wifiManager.connectionInfo.supplicantState}")
    }

    fun rssiChanged(intent: Intent?) {
        log("")
    }

}
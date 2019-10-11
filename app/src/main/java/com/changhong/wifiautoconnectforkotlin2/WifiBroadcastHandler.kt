package com.changhong.wifiautoconnectforkotlin2

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
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
             log("wifi 还未打开！")
             return
         } else {
            //wifi 已打开
             log("wifi 已打开！")
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
         connectWifiForce(wifiManager, config.value.ssid, config.value.passwd, Integer.parseInt(config.value.wifiType))
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
}
package com.changhong.wifiautoconnectforkotlin2

import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager

object wifiHelper {
    private val WIFICIPHER_NOPASS = 0
    private val WIFICIPHER_WEP = 1
    private val WIFICIPHER_WPA = 2

    fun createWifiConfig(
        wifiManager: WifiManager,
        ssid: String,
        password: String,
        type: Int
    ): WifiConfiguration {
        //初始化WifiConfiguration
        val config = WifiConfiguration()
        config.allowedAuthAlgorithms.clear()
        config.allowedGroupCiphers.clear()
        config.allowedKeyManagement.clear()
        config.allowedPairwiseCiphers.clear()
        config.allowedProtocols.clear()

        //指定对应的SSID
        config.SSID = "\"" + ssid + "\""

        //如果之前有类似的配置
        val tempConfig = isExist(wifiManager, ssid)
        if (tempConfig != null) {
            //则清除旧有配置
            wifiManager.removeNetwork(tempConfig!!.networkId)
        }

        //不需要密码的场景
        when (type) {
            WIFICIPHER_NOPASS -> {
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            }
            //以WEP加密的场景
            WIFICIPHER_WEP -> {
                config.hiddenSSID = true
                config.wepKeys[0] = "\"" + password + "\""
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED)
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                config.wepTxKeyIndex = 0
                //以WPA加密的场景，自己测试时，发现热点以WPA2建立时，同样可以用这种配置连接
            }
            WIFICIPHER_WPA -> {
                config.preSharedKey = "\"" + password + "\""
                config.hiddenSSID = true
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                config.status = WifiConfiguration.Status.ENABLED
            }
        }

        return config
    }

    private fun isExist(wifiManager: WifiManager, ssid: String): WifiConfiguration? {
        val configs = wifiManager.configuredNetworks

        for (config in configs) {
            if (config.SSID == "\"" + ssid + "\"") {
                return config
            }
        }
        return null
    }
}
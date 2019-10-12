package com.changhong.wifiautoconnectforkotlin2

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.ScanResult
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.support.annotation.RequiresApi
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


    fun connectWifi(ssid: String = config.value.ssid, passwd: String = config.value.passwd, wifiType: Int = Integer.parseInt(config.value.wifiType)): Boolean {
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

    fun connectWifiForce(ssid: String = config.value.ssid, passwd: String = config.value.passwd, wifiType: Int = Integer.parseInt(config.value.wifiType)): Boolean {
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
            /** 1. 命令 system 权限不够
             *  wpa_cli -iwlan0 -p /data/misc/wifi/sockets scan /
             *  2. 查看当前扫描到的wifi热点命令，system 权限不能执行
             *  wpa_cli -iwlan0 -p /data/misc/wifi/sockets scan_results
             *
             *  经测试直接执行 echo 0x21 > file 写入对应的文件，就能配置 2.4G 或 5G
             * */
            val cmd2_4G ="echo 0x21 >  $bs"
            val cmd5G = "echo 0x1f > $bs"
            if (config.value.wifi_frequency_band.equals("2.4G")) {
                ExecCmd().run(cmd2_4G)
            } else if (config.value.wifi_frequency_band.equals("5G")) {
                ExecCmd().run(cmd5G)
            } else {
                log("未配置2.4G或5G，使用双频模式！")
            }
        } else if (csFile.exists()) {
            val cmd2_4G = "echo 0x21 > $cs"
            val cmd5G = "echo 0x1f > $cs"
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

    private fun isScanContainConfigssid(scanResultList: List<ScanResult> = wifiManager.scanResults, ssid: String = config.value.ssid): Boolean {
        for (scanResult in scanResultList) {
            if (scanResult.SSID == ssid) {
                log2("scanResultList is contain: $ssid")
                return true
            }
        }
        log2("scanResultList is not contain: $ssid")

        return false
    }

    private fun isCurrConnectIsConfigSsid(wifiInfo: WifiInfo = wifiManager.connectionInfo, ssid: String = config.value.ssid): Boolean {
        log2("wifiInfo.ssid: ${wifiInfo.ssid}, ssid ---> $ssid ${wifiInfo.ssid == "\"$ssid\""}")
        return wifiInfo.ssid == "\"$ssid\""
    }

    fun hardwareChanged(intent: Intent?) {
        val wifiState = intent?.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1)
        ExecCmd().run("id")
        if (WifiManager.WIFI_STATE_ENABLED != wifiState) {
            log("wifi is OFF！${wifiManager.wifiState} $wifiState")
            return
        } else {
            //wifi 已打开
            log("wifi is ON！ ${wifiManager.wifiState} $wifiState")
            setWifiSingleFrequency()
        }
        when (wifiState) {
            WifiManager.WIFI_STATE_ENABLED ->{}
            WifiManager.WIFI_MODE_FULL,
            WifiManager.WIFI_MODE_SCAN_ONLY ->{

            }
        }

        if(isCurrConnectIsConfigSsid()) {
            // 当前正在连接的 wifi 热点就是配置文件中的 ssid
            // 已经在连接我们希望连接的热点，直接返回
            return
        }

        if (isScanContainConfigssid()) {
            // 扫描的的热点包含配置文件中的 ssid
            connectWifi()
            return
        }

        // 无论是否，正在连接。没有连接配置文件中的 ssid，去连接配置文件中的 ssid
//        connectWifiForce(wifiManager, config.value.ssid, config.value.passwd, Integer.parseInt(config.value.wifiType))
    }

    @SuppressWarnings("deprecated")
    fun supplicantChanged(intent: Intent?) {
        val supplicantState = intent?.getParcelableExtra<SupplicantState>(WifiManager.EXTRA_NEW_STATE) //// 获取当前网络新状态.
        val errorNo = intent?.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1)      //// 获取当前网络连接状态码.
        var wifiInfo:WifiInfo = wifiManager.connectionInfo

        log("scanSize: ${wifiManager.scanResults.size}, wifiManager: ${wifiManager.connectionInfo.ssid} ${wifiManager.connectionInfo.supplicantState}, Broadcast: $supplicantState")

        if (errorNo == WifiManager.ERROR_AUTHENTICATING) {
            log("$supplicantState --> 身份验证不通过!!!!")
        }

        when(supplicantState) {
            SupplicantState.INVALID,
            SupplicantState.SCANNING,
            SupplicantState.UNINITIALIZED -> { }
            SupplicantState.ASSOCIATING,
            SupplicantState.ASSOCIATED -> {
                if (null == wifiInfo.ssid) {
                    return
                } else {
                    if(!isCurrConnectIsConfigSsid()) {
                        if (isScanContainConfigssid()) {
                            // 当前有扫描到配置文件中的热点才尝试连接，不然会断开当前的连接不在连接。直到配置文件中的热点出现
                            connectWifi()
                        }
                    }
                }

                if (isCurrConnectIsConfigSsid()) {
                    // 控制灯闪烁
                }
            }
            SupplicantState.DISCONNECTED -> { // 连接断开，当前未连接
                // 控制灯灭
            }

            SupplicantState.FOUR_WAY_HANDSHAKE -> { } // 握手正在进行中
            SupplicantState.AUTHENTICATING -> { } // 验证用户信息
            SupplicantState.COMPLETED,  // 这个消息，在获取网络IP地址的广播之后才会发出。
            SupplicantState.DORMANT,
            SupplicantState.INACTIVE,
            SupplicantState.GROUP_HANDSHAKE,
            SupplicantState.INTERFACE_DISABLED ->{ }
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

        log("Broadcast wifiInfo: ${wifiInfo?.ssid} ${wifiInfo?.supplicantState}, Broadcast networkInfo: ${networkInfo?.detailedState}")
        log("wifiManager: ${wifiManager.connectionInfo.ssid} ${wifiManager.connectionInfo.supplicantState},  connectivityManager: ${connectivityManager.activeNetworkInfo.detailedState}")

        // 经试验，
        // 1. 广播收到的 wifiInfo 不准确，几个内容都将传入 null。使用时使用 wifiManager.connectionInfo 代替。
        // 2. 广播收到的 networkInfo 和 connectivityManager.activeNetworkInfo 中的信息不一致，connectivityManager.activeNetworkInfo中只有一个状态 CONNECTED。使用广播传入的 networkInfo
        when(networkInfo?.detailedState) {
            NetworkInfo.DetailedState.AUTHENTICATING,
            NetworkInfo.DetailedState.BLOCKED,
            NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK,
            NetworkInfo.DetailedState.CONNECTING,
            NetworkInfo.DetailedState.DISCONNECTED,
            NetworkInfo.DetailedState.DISCONNECTING,
            NetworkInfo.DetailedState.FAILED,
            NetworkInfo.DetailedState.IDLE,
            NetworkInfo.DetailedState.SCANNING,
            NetworkInfo.DetailedState.SUSPENDED,
            NetworkInfo.DetailedState.OBTAINING_IPADDR, // 等待来自DHCP服务器的响应，以便分配IP地址信息
            NetworkInfo.DetailedState.VERIFYING_POOR_LINK -> { } // 链接连接不良
            NetworkInfo.DetailedState.CONNECTED -> {
                if(!isCurrConnectIsConfigSsid() && isScanContainConfigssid()) {
                    // 前方有判断，不应该运行到这个位置
                    // 1. 连接的不是配置中的热点
                    // 2. 扫描的热点信息，存在配置文件中的 ssid
                    connectWifi()
                }

                if (isCurrConnectIsConfigSsid()) {
                    // 此处启动 ping 测试
                    // 如若能够 ping 通，则将灯设置为常亮
                    ledControl.ledON()
                }
            }
        }
    }

    fun scanResults(intent: Intent?) {
        log("wifiManager.scan: ${wifiManager.scanResults.size},  ${wifiManager.connectionInfo.supplicantState}")

        if (null == wifiManager.connectionInfo.ssid) {
            if (isScanContainConfigssid()) {
                connectWifi()
            }
            return
        } else {
            if(!isCurrConnectIsConfigSsid() && isScanContainConfigssid()) {
                // 1. 连接的不是配置中的热点
                // 2. 扫描的热点信息，存在配置文件中的 ssid
                connectWifi()
            }
        }
    }

    fun rssiChanged(intent: Intent?) {
        if (null == wifiManager.connectionInfo.ssid) {
            if (isScanContainConfigssid()) {
                connectWifi()
            }
            return
        } else {
            if(!isCurrConnectIsConfigSsid() && isScanContainConfigssid()) {
                // 1. 连接的不是配置中的热点
                // 2. 扫描的热点信息，存在配置文件中的 ssid
                connectWifi()
            }
        }
    }

}
package com.changhong.wifiautoconnectforkotlin2

import java.io.*
import java.lang.reflect.Field

object config {
    class Value {
        var wifiType : String = ""
        var ssid: String = ""
        var passwd: String = ""
        var ping_repeate: String = ""
        var ping_parameter: String = ""
        var wifi_dhcpOrStatic: String = ""
        var wifi_ip: String = ""
        var wifi_mask: String = ""
        var wifi_gw: String = ""
        var ping_ok_do: String = ""
    }

    private val configFile = "/system/etc/ch_auto_test_wifi.cfg"
    private var configFileLastModified: Long = 0L
    private val readData = arrayOf("")
    var value = Value()
    val fields = value::class.java.declaredFields

    fun read(): Boolean {
        val file = File(configFile)
        if (!file.exists()) {
            log("文件 $configFile 不存在，读取配置文件失败！！！")
            return false
        }

        if (!(0L == configFileLastModified || configFileLastModified != file.lastModified())) {
//            log("配置文件已经读取过，且并未改变！")
            return true
        }

        var field: Field
        var key: String
        val data = arrayOf("")
        for (item in fields) {
            item.isAccessible = true
            key = item.name
            if (FileKeyValueOP.readFileKeyValue(file, key, data)){
                field = value::class.java.getDeclaredField(key)
                field.isAccessible = true
                field.set(value, data[0])
            } else {
                log("没有找到到配置： " + key)
                return false
            }
        }

        configFileLastModified = file.lastModified()

        log("成功从文件中读取到配置：$configFile")
        for (field in fields) {
            field.isAccessible = true
            log("${field.name} = ${field.get(value)}")
        }
        return true
    }

}

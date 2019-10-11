package com.changhong.wifiautoconnectforkotlin2

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.lang.Exception

object FileKeyValueOP{
    fun readFileKeyValue(file: File, key: String, vaule: Array<String>): Boolean {
        var line: String
        var index: Int
        var strKey: String
        var strValue: String
        var res: Boolean = false

        try {
            val reader: FileReader = FileReader(file)
            val br: BufferedReader = BufferedReader(reader)
            while (null != run{line = br.readLine(); line}) {
                line = line.trim()
                if (-1 != run{index = line.indexOf('='); index}) {
                    strKey = line.substring(0, index) // 获取键
                    if (key.trim() == strKey.trim()) { //比较
                        strValue = line.substring(index + 1).trim() // 获取值
                        if (-1 != run{index = strValue.indexOf("//"); index}) {
                            strValue = strValue.substring(0, index).trim() //去掉用 // 的注释
                        }
                        vaule[0] = strValue
                        res = true
                        break
                    }
                }
            }

            br.close()
            reader.close()
        } catch (e: Exception) {
//            e.printStackTrace()
//            log("读取配置文件异常")
        }

        return res
    }
}
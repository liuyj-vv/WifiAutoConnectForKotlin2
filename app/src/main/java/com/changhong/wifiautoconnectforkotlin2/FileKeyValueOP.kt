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
        var strValue: String    // 返回 defValue 表示失败
        var res: Boolean = false

        try {
            val reader: FileReader = FileReader(file)
            val br: BufferedReader = BufferedReader(reader)
            while (null != run{line = br.readLine(); line}) {
                line = line.trim()
                if (-1 != run{index = line.indexOf('='); index}) {
                    // 获取键
                    strKey = line.substring(0, index)
                    if (key.trim() == strKey.trim()) { //比较
                        // 获取值
                        strValue = line.substring(index + 1).trim()
                        if (-1 != run{index = strValue.indexOf("//"); index}) {
                            //去掉用 // 的注释
                            strValue = strValue.substring(0, index).trim()
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
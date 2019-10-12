package com.changhong.wifiautoconnectforkotlin2

import android.util.Log
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject

object ledControl {
    private fun switch(status: Int, type: String) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("jsonrpc", "2.0")
            jsonObject.put("method", "ledCtrl")
            val obj2 = JSONObject()
            obj2.put("status", status)//0 or 1
            obj2.put("type", type)//network standy ir
            jsonObject.put("params", obj2)
            jsonObject.put("id", 1)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val jsonStr = jsonObject.toString()
        val body = RequestBody.create(MyApp.JSON, jsonStr)
        val request =
            Request.Builder().url(MyApp.interfaceUrl).addHeader("content-type", "application/json;charset:utf-8")
                .post(body).build()
        try {
            val response = MyApp.getOkHttp()?.newCall(request)?.execute()
            if (!response?.isSuccessful!!) {
                Log.i(TAG, "----------ledCtrl----- fail")
            }
            response?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun ledON() {
        log("dddddddddddddddddddddddddddddddddddddddd")
        Thread(Runnable { switch(1, "ir") }).start()
    }

    fun ledOFF() {
        Thread(Runnable { switch(0, "ir") }).start()
    }
}
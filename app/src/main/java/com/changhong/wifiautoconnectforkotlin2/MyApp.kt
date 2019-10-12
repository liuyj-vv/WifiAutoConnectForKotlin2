package com.changhong.wifiautoconnectforkotlin2

import okhttp3.MediaType
import okhttp3.OkHttpClient
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object MyApp {
    val TAG = "chcommonfactory"
    private var instance: MyApp? = null
    var bUSB1 = false
    var bUSB2 = false

    internal var cpuNums = Runtime.getRuntime().availableProcessors()
    var mExeCutorService = Executors.newFixedThreadPool(cpuNums * 4)

    //for okhttp toMediaTypeOrNull
    val JSON = MediaType.parse("application/json; charset=utf-8")
    var interfaceUrl = "http://127.0.0.1:18176/rpc"

    private val CONNECT_TIMEOUT = 5
    private val READ_TIMEOUT = 5
    private val WRITE_TIMEOUT = 5

    private var mHttpClient: OkHttpClient? = null

    @Synchronized
    fun getInstance(): MyApp? {
        if (null == instance) {
            instance = this
        }
        return instance
    }

    fun getOkHttp(): OkHttpClient? {

        if (mHttpClient == null) {
            mHttpClient = OkHttpClient.Builder().readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS).build()
        }
        return mHttpClient
    }
}
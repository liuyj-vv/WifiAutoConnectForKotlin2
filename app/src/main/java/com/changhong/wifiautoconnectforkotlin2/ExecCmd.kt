package com.changhong.wifiautoconnectforkotlin2

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread

class ExecCmd {
    private var process: Process? = null
    private val lock = ReentrantLock()
    val command: Array<String> = arrayOf("sh", "-c", "输入的命令")

    internal var thread = Thread(Runnable {
        try {
            if (null != process) {
                process?.waitFor()
                log("Executing command is end --->: ${command[2]}")
                lock.lock()
                process = null
                lock.unlock()
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    })

    fun run(cmd: String) {
        try {
            lock.lock()
            if (null == process) {
                command[2] = cmd
                log("Executing command --->: ${command[2]}")
                process = Runtime.getRuntime().exec(command)
                // 打印输出到屏幕上
                printStdoutMessage()
                printStderrMessage()
                // 监听命令是否执行进程是否结束
                thread.start()
            }
        } catch (e: IOException) {
            process?.destroy()
            process = null
            e.printStackTrace()
        }

        lock.unlock()
        return
    }

    private fun printStdoutMessage() {
        lock.lock()
        if (null != process) {
            process?.inputStream?.let { printMessage(it, "stdout") }
        }
        lock.unlock()
    }

    private fun printStderrMessage() {
        lock.lock()
        if (null != process) {
            process?.errorStream?.let { printMessage(it, " error") }
        }
        lock.unlock()
    }

    private fun printMessage(input: InputStream, TAG: String) {
        thread {
            var line: String?
            try {
                val reader = InputStreamReader(input)
                val bufferedReader = BufferedReader(reader)
                while (null != run{line = bufferedReader.readLine(); line}) {
                    log("$process $TAG: $line")
                }
                reader.close()
                bufferedReader.close()
            } catch (e: IOException) {
//                e.printStackTrace()
            }
        }
    }
}
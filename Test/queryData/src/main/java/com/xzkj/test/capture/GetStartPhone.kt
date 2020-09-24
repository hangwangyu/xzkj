package com.xzkj.test.capture

import android.os.SystemClock
import java.text.SimpleDateFormat
import java.util.*


class GetStartPhone {


    /**
     * 获取系统开机时间(精确到秒)
     *
     * @return
     */
    fun getBootTime(): Long {
        var ut = SystemClock.elapsedRealtime()
        if (ut == 0L) {
            ut = 1
        }
        return ut
    }

    /**
     * 获取格式化系统开机时间(精确到秒)
     *
     * @return 格式化后的时间 5:03:06
     */
    fun getFormatBootTime(): String? {
        val date = Date()

        val simpleDateFormat =
            SimpleDateFormat("yyyy-MM-dd HH-mm-ss") //24小时制
        val format = simpleDateFormat.format(date)

        val time = simpleDateFormat.parse(format).time

        val l = (time - getBootTime())/1000
        return l.toString()
    }

    /**
     * 格式化启动时间
     *
     * @param t
     * @return
     */
    private fun convertBootTime(t: Long): String? {
        val s = (t % 60).toInt()
        val m = (t / 60 % 60).toInt()
        val h = (t / 3600).toInt()
        return h.toString() + ":" + pad(m) + ":" + pad(s)
    }

    /**
     * 格式化2位
     *
     * @param n
     * @return
     */
    private fun pad(n: Int): String {
        return if (n >= 10) {
            n.toString()
        } else {
            "0$n"
        }
    }
}
package com.xzkj.test.capture

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Handler
import com.xzkj.test.capture.data.OtherData

class MobileDeviceDetails(var context: Context?) {
    private val batteryReceiver by lazy { BatteryReceiver() }

    fun charger(intent: Intent): String {
        val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        return ispluged(plugged)
    }

    private fun ispluged(plugged: Int): String {
        when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC ->
                // 电源是AC charger.[应该是指充电器]
                return "Power supply is AC charger."
            BatteryManager.BATTERY_PLUGGED_USB ->
                // 电源是USB port
                return "Power supply is USB port"
            else -> {
                return ""
            }
        }
    }

    //当前电量
    fun currentBattery(intent: Intent): String {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        return level.toString()
    }

    //最大电量
    fun maxCurrent(intent: Intent): String {
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        return scale.toString()
    }

    //更多状态
    fun otherBattery(intent: Intent): OtherData {
        val otherData = orherData(intent)
        return otherData
    }

    private fun orherData(intent: Intent): OtherData {
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
        // 电池使用的技术。比如，对于锂电池是Li-ion
        val techPronology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
        // 当前电池的温度
        val temperaProture = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
        val str = ("voltage = " + voltage + " technology = "
                + techPronology + " temperature = " + temperaProture)
        return OtherData(voltage.toString(),techPronology,temperaProture.toString())
    }

    //电池健康状态
    fun healthBattery(intent: Intent): String {
        val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
        return isBattery(health)
    }

    private fun isBattery(health: Int): String {
        return when (health) {
            BatteryManager.BATTERY_HEALTH_GOOD -> {
                "BATTERY_HEALTH_VERY_GOOD"
            }
            BatteryManager.BATTERY_HEALTH_COLD -> {
                "BATTERY_HEALTH_COLD"
            }
            BatteryManager.BATTERY_HEALTH_DEAD -> {
                "BATTERY_HEALTH_DEAD"
            }
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> {
                "BATTERY_HEALTH_OVERHEAT"
            }
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> {
                "BATTERY_HEALTH_OVER_VOLTAGE"
            }
            BatteryManager.BATTERY_HEALTH_UNKNOWN -> {
                "BATTERY_HEALTH_UNKNOWN"
            }
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> {
                "BATTERY_HEALTH_UNSPECIFIED_FAILURE"
            }
            else -> {
                ""
            }
        }
    }

    //电池状态
    fun statusBattery(intent: Intent): String {
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        return when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING ->
                // 正在充电
                "Charging"
            BatteryManager.BATTERY_STATUS_DISCHARGING ->
                "BATTERY_STATUS_DISCHARGING"
            BatteryManager.BATTERY_STATUS_FULL ->
                // 充满
                "Battery full"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING ->
                // 没有充电
                "The battery is not charged"
            BatteryManager.BATTERY_STATUS_UNKNOWN ->
                // 未知状态
                "unknown state"
            else -> ""
        }
    }

    var current = ""
    var max_current = ""
    var battery_health = ""
    var battery_status = ""
    var other_battery = OtherData()
    var charger = ""

    inner class BatteryReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                setdate(intent)
            }
            unRegistBattery()
            if (mHandler != null) {
                mHandler?.sendEmptyMessageDelayed(1,  1000)
            }
        }

    }

    private fun setdate(intent: Intent?) {
        //当前电量
        current = currentBattery(intent!!)
        //最大电量
        max_current = maxCurrent(intent)
        //电池健康状态
        battery_health = healthBattery(intent)
        //电池状态
        battery_status = statusBattery(intent)
        //电源
        charger = charger(intent)
        //更多
        other_battery = otherBattery(intent)


    }

    private var mHandler: Handler? = null


    fun registBattery(handler: Handler) {
        this.mHandler = handler
        initregiste()

    }

    private fun initregiste() {
        val intent = IntentFilter()
        intent.addAction(Intent.ACTION_BATTERY_CHANGED)
        intent.addAction(Intent.ACTION_BATTERY_LOW)
        intent.addAction(Intent.ACTION_BATTERY_OKAY)
        context?.registerReceiver(batteryReceiver, intent)
    }

    fun unRegistBattery() {
        context?.unregisterReceiver(batteryReceiver)
    }

    /**
     * 获取手机电池容量
     */
    fun phoneSize(context: Context?): String {
        val profile: Any
        var s = 0.0
        try {
            profile = Class.forName("com.android.internal.os.PowerProfile")
                .getConstructor(Context::class.java)
                .newInstance(context)
            s = initsize(s, profile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "$s mAh"
    }

    private fun initsize(s: Double, profile: Any): Double {
        var s1 = s
        s1 = Class.forName("com.android.internal.os.PowerProfile")
            .getMethod("getBatteryCapacity")
            .invoke(profile) as Double
        return s1
    }
}
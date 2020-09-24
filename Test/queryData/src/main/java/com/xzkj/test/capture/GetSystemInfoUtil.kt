package com.xzkj.test.capture

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.text.TextUtils
import androidx.annotation.RequiresApi
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


object GetSystemInfoUtil {
    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    val systemVersion: String
        get() = Build.DISPLAY

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    val systemModel: String
        get() = Build.MODEL

    /**
     * 系统4.0的时候
     * 获取手机IMEI 或者Meid
     *
     * @return 手机IMEI
     */
    @SuppressLint("MissingPermission")
    fun getImeiOrMeid(ctx: Context): String? {
        val tm =
            ctx.getSystemService(Activity.TELEPHONY_SERVICE) as TelephonyManager
        return tm?.deviceId
    }

    /**
     * 拿到imei或者meid后判断是有多少位数
     *
     * @param ctx
     * @return
     */
    fun getNumber(ctx: Context): Int {
        var count = 0
        var number = getImeiOrMeid(ctx)!!.trim { it <= ' ' }.toLong()
        while (number > 0) {
            number = number / 10
            count++
        }
        return count
    }

    /**
     * Flyme 说 5.0 6.0统一使用这个获取IMEI IMEI2 MEID
     * @param ctx
     * @return
     */
    @SuppressLint("MissingPermission")
    fun getImeiAndMeid(ctx: Context): Map<*, *> {
        val map: MutableMap<String, String> =
            HashMap()
        val mTelephonyManager =
            ctx.getSystemService(Activity.TELEPHONY_SERVICE) as TelephonyManager
        var clazz: Class<*>? = null
        var method: Method? = null //(int slotId)
        try {
            clazz = Class.forName("android.os.SystemProperties")
            method = clazz.getMethod("get", String::class.java, String::class.java)
            val gsm = method.invoke(null, "ril.gsm.imei", "") as String
            val meid = method.invoke(null, "ril.cdma.meid", "") as String
            map["meid"] = meid
            if (!TextUtils.isEmpty(gsm)) {
                //the value of gsm like:xxxxxx,xxxxxx
                val imeiArray = gsm.split(",").toTypedArray()
                if (imeiArray != null && imeiArray.size > 0) {
                    map["imei1"] = imeiArray[0]
                    if (imeiArray.size > 1) {
                        map["imei2"] = imeiArray[1]
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            map["imei2"] = mTelephonyManager.getDeviceId(1)
                        }
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        map["imei1"] = mTelephonyManager.getDeviceId(0)
                        map["imei2"] = mTelephonyManager.getDeviceId(1)
                    }
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    map["imei1"] = mTelephonyManager.getDeviceId(0)
                    map["imei2"] = mTelephonyManager.getDeviceId(1)
                }
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        return map
    }

}
package com.xzkj.test.capture

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Build

class WifiList {

    fun getWifiLName(context: Context?): String? {
        var ssid = "no wifi"

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O || Build.VERSION.SDK_INT === Build.VERSION_CODES.P) {
            var mWifiManager = (context?.applicationContext
                ?.getSystemService(Context.WIFI_SERVICE) as WifiManager)
            var info = mWifiManager.connectionInfo
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                info.ssid
            } else {
                info.ssid.replace("\"", "")
            }
        } else if (Build.VERSION.SDK_INT === Build.VERSION_CODES.O_MR1) {
            var connManager = (context?.applicationContext
                ?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
            var networkInfo: NetworkInfo = connManager.activeNetworkInfo
            if (networkInfo.isConnected()) {
                if (networkInfo.getExtraInfo() != null) {
                    return networkInfo.getExtraInfo().replace("\"", "")
                }
            }
        }
        return ssid

    }


}
package com.xzkj.test.capture

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import com.xzkj.test.capture.data.AppListData
import java.util.*

@Suppress("DEPRECATION")
class PhoneInfo {

    fun getAppAllList(context: Context): String? {
        val appList = ArrayList<AppListData>()
        val packageManager = context.packageManager
        val arrInfo = packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES)
        if (arrInfo != null) {
            for (i in arrInfo.indices) {
                val pInfo = arrInfo[i]
                var sys_app =
                    if ((pInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                        1
                    } else {
                        2
                    }
                val app_name = pInfo.applicationInfo.loadLabel(context.packageManager).toString()
                val package_id = pInfo.packageName
                val first_inst_time = pInfo.firstInstallTime/1000

               var  versionCode = if (pInfo.versionName!=null){
                   pInfo.versionName
               }else{
                   "0"
               }
                appList.add(
                    AppListData(
                        sys_app,
                        app_name,
                        package_id,
                        first_inst_time,
                        versionCode
                    )
                )
            }
            return JsonInit().arrayToJson(appList)
        }
        return null
    }


    fun phoneDevices(): String {

        return Build.VERSION.RELEASE
    }

    fun isPhone(activity: Activity): Boolean {
        val tm =
            activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.phoneType == TelephonyManager.PHONE_TYPE_NONE
    }
}
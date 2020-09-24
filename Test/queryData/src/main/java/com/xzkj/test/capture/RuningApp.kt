package com.xzkj.test.capture

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.xzkj.test.capture.data.RunNingBean
import java.util.*

class RuningApp {


    fun queryRunningInfo(
        activity: Activity
    ): MutableList<ApplicationInfo> {
        var pm = activity.packageManager
        // 查询所有已经安装的应用程序
        val packAllList =
            pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES)
        Collections.sort(
            packAllList,
            ApplicationInfo.DisplayNameComparator(pm)
        )

        return packAllList
    }

    fun saveAllRunning(activity: Activity): MutableMap<String, ActivityManager.RunningAppProcessInfo> {
        // 保存所有正在运行的包名 以及它所在的进程信息
        val pross: MutableMap<String, ActivityManager.RunningAppProcessInfo> =
            HashMap()
        val mActivityManager =
            activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        // 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程
        if (mActivityManager != null) {
            val appProcessList = mActivityManager
                .runningAppProcesses
            if (appProcessList != null) {
                for (appProcess in appProcessList) {
                    val pid = appProcess.pid // pid
                    val processName = appProcess.processName // 进程名
                    //            Log.i(TAG, "processName: " + processName + "  pid: " + pid);
                    val pkgNameList =
                        appProcess.pkgList // 获得运行在该进程里的所有应用程序包

                    // 输出所有应用程序的包名
                    if (pkgNameList != null) {
                        for (i in pkgNameList.indices) {
                            val pkgName = pkgNameList[i]
                            // 加入至map对象里
                            pross[pkgName] = appProcess
                        }
                    }
                }
            }
        }
        return pross
    }

    fun saveRunningNAme(
        activity: Activity,
        pkm: PackageManager
    ): ArrayList<RunNingBean> {
        // 保存所有正在运行的应用程序信息
        var runningApp: ArrayList<RunNingBean> =
            ArrayList<RunNingBean>()
        if (queryRunningInfo(activity) != null) {
            for (app in queryRunningInfo(activity)) {
                // 如果该包名存在 则构造一个RunningAppInfo对象
                if (saveAllRunning(activity).containsKey(app.packageName)) {
                    // 获得该packageName的 pid 和 processName
                    runningApp.add(RunNingBean(app.loadLabel(pkm) as String))
                }
            }
        }
        return runningApp
    }

}
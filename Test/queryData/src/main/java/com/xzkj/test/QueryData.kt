package com.xzkj.test

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.*
import android.telephony.TelephonyManager
import android.text.format.Formatter
import android.util.DisplayMetrics
import android.view.Display
import androidx.core.content.ContextCompat
import com.xzkj.test.capture.*
import com.xzkj.test.capture.data.*
import com.xzkj.test.capture.listenner.GetPhoneSize
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.HashMap


class QueryData(var activity: Activity, var application: Context) : Handler.Callback {
    private val mobileDeviceDetails by lazy {
        MobileDeviceDetails(
            application
        )
    }
    private var isset = false
    private val handler by lazy { Handler(this) }
    private var hostIP = ""
    private var getNetIp = ""
    private var locale: Locale? = null

    var current = ""
    var max_current = ""
    var battery_health = ""
    var battery_status = ""
    var other_battery = OtherData()
    var charger = ""


    //设备信息
    public fun getMobileData(){
        isset = true
        mobileDeviceDetails.registBattery(handler)

        Thread {
            hostIP = if (GetIpAdressUtils.getHostIP() != null) {
                GetIpAdressUtils.getHostIP()
            } else {
                ""
            }

            getNetIp = if (GetIpAdressUtils.GetNetIp() != null) {
                GetIpAdressUtils.GetNetIp()
            } else {
                ""
            }

        }.start()

    }


    private fun unsetMobileData() {
        isset = false
        mobileDeviceDetails.unRegistBattery()
    }

    /**
     * 模拟器详情
     */
    fun getEmulatorDetails(): SimuLatorData? {
        val deviceData = DeviceData.get(activity)
        if (deviceData != null) {
            return deviceData
        }
        return null
    }

    /**
     * 上传模拟器
     */
    fun getEmulatordata(): Emulatordata? {
        val emulator1 = Simulator()
            .isEmulator(activity, object :
                Simulator.EmulatorListener {
                override fun emulator(emulator: SimuLatorData) {
                }
            })
        val checkInfoBuild = Simulator().checkInfoBuild(activity)
        val checkInnfoAndroid = Simulator().checkInnfoAndroid(activity)
        val checkFilesNum = Simulator().checkFilesNum()
        var emulatordata = Emulatordata(
            emulator1,
            checkInfoBuild,
            checkInnfoAndroid,
            checkFilesNum
        )

        return emulatordata
    }

    /**
     * 手机设备信息存储(硬件)
     */
    fun getStorageHardware(): StorageHardware? {

        var storageHardware = StorageHardware(
            Build.BOARD,
            Build.BOOTLOADER,
            Build.BRAND,
            Build.DEVICE,
            Build.HARDWARE,
            Build.MODEL,
            Build.PRODUCT,
            Build.MANUFACTURER,
            Build.FINGERPRINT,
            Build.DISPLAY,
            Build.getRadioVersion(),
            Build.SERIAL,
            Build.HOST,
            Build.ID
        )
        return storageHardware
    }

    /**
     * 手机屏幕分辨率信息存储
     */
    fun screenResolution(): DisPlayData? {

        val disPlayData = getFenbianlv(activity) as DisPlayData
        if (disPlayData != null) {
            return disPlayData
        }
        return null
    }

    /**
     * app名称列表信息存储表
     */
    fun getAppList(): String {

        val appAllList = PhoneInfo().getAppAllList(activity)
        if (appAllList != null) {
            return appAllList
        }
        return ""
    }

    /**
     * 运行APP
     */
    fun getRunningApp(): ArrayList<RunNingBean>? {

        val saveRunningNAme = RuningApp()
            .saveRunningNAme(activity, activity.packageManager)
        if (saveRunningNAme != null) {
            return saveRunningNAme
        }
        return null
    }

    /**
     * 上传通讯录
     */
    fun getContact(): ArrayList<PhoneData>? {
        val phoneNumBerData = PhoneNumber().getPhoneNumBerData(activity)
        if (phoneNumBerData != null) {
            return phoneNumBerData
        }
        return null
    }

    /**
     * 获取屏幕分辨率
     *
     * @return
     */
    fun getFenbianlv(mContext1: Context): DisPlayData? {
        try {
            val display = mContext1.resources
                .displayMetrics
            val dataToJson = JsonInit().dataToJson(display)
            val jsonToData = JsonInit()
                .jsonToData(dataToJson, DisPlayData().javaClass)
            return jsonToData
        } catch (e: Exception) {
            e.fillInStackTrace()
        }
        return null
    }

    /**
     * 移动网络类型
     */
    private fun getNetWorkType(): Int {
        val tm =
            activity.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager

        return tm?.networkType
    }



    /**
     * IMEI
     */
    @SuppressLint("MissingPermission", "NewApi")
    private fun getPhoneIMEI(): Map<*, *>? {
        val map: MutableMap<String, String> = HashMap()
        val tm =
            activity.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
        var imei1: String? = ""
        var imei2: String? = ""
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                imei1 = tm?.getImei(0)
                imei2 = tm?.getImei(1)
                map["imei1"] = imei1
                map["imei2"] = imei2
            }
        } catch (e: Exception) {
            e.fillInStackTrace()
        }
        return map

    }

    // 判断权限
    fun isHavesPermission(permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(
                application,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            return true
        }

        return false
    }

    /**
     * MEID
     */
    @SuppressLint("MissingPermission", "NewApi")
    private fun getPhoneMEID(): String? {
        if (!isHavesPermission(Manifest.permission.READ_PHONE_STATE)) {
            val tm =
                activity.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            var meid: String? = ""
            if (null != tm) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    meid = tm.getMeid();

                } else {
                    meid = tm.getDeviceId();
                }
            } else {
                meid = ""
            }

            return meid
        } else {
            return ""
        }
    }

    /**
     * 获得机身内存总大小
     */
    private fun getRomTotalSize(): String? {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSize.toLong()
        val totalBlocks = stat.blockCount.toLong()
        return Formatter.formatFileSize(
            activity,
            blockSize * totalBlocks
        )
    }

    /**
     * 获取mac地址（适配所有Android版本）
     * @return
     */
    private fun getMac(context: Context): String? {
        var mac: String? = ""
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mac = ResultVersion.getMacDefault(context)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mac = ResultVersion.getMacAddress()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mac = ResultVersion.getMacFromHardware()
        }
        return mac
    }


    override fun handleMessage(msg: Message): Boolean {

        current = mobileDeviceDetails.current  //电池电量
        battery_health = mobileDeviceDetails.battery_health//,电池健康状态
        max_current = mobileDeviceDetails.max_current//, 最大电量
        charger = mobileDeviceDetails.charger//,  充电电源
        battery_status = mobileDeviceDetails.battery_status//,  电池状态
        other_battery = mobileDeviceDetails.other_battery//,  其他属性

        //          WLAN MAC:
        var mac = if (getMac(application) != null) {
            getMac(activity)
        } else {
            ""
        }
//          手机型号:
        val systemModel = GetSystemInfoUtil.systemModel
//          移动网络类型:
        var netWorkType = if (getNetWorkType() != null) {
            getNetWorkType()
        } else {
            0
        }
//          屏幕尺寸:
        var pingmu = if (GetPhoneSize.getmInch(activity) != null) {
            GetPhoneSize.getmInch(activity)
        } else {
            0f
        }
//          存储:
        var romTotalSize = if (getRomTotalSize() != null) {
            getRomTotalSize()
        } else {
            ""
        }
//          运行内存:
        var totalMemory = if (ResultVersion.getTotalMemory(
                application
            ) != null
        ) {
            ResultVersion.getTotalMemory(application)
        } else {
            ""
        }
//          处理器:
        var phoneCpu = if (GetIpAdressUtils.getPhoneCpu() != null) {
            GetIpAdressUtils.getPhoneCpu()
        } else {
            ""
        }
//          内核版本:
        var property = if (System.getProperty("os.version") != null) {
            System.getProperty("os.version")
        } else {
            ""
        }
//          基带版本:
        var basebandVer = if (ResultVersion().baseband_Ver != null) {
            ResultVersion().baseband_Ver
        } else {
            ""
        }
//          当前链接WIFI:
        var wifiLName = if (WifiList().getWifiLName(application) != null) {
            WifiList().getWifiLName(application)
        } else {
            ""
        }
//          开机时间（毫秒）:
        var formatBootTime = if (GetStartPhone().getFormatBootTime() != null) {
            GetStartPhone().getFormatBootTime()
        } else {
            ""
        }
        var imei: String = ""
        var imei2: String = ""
        var meid: String = ""
        //IMEI &  MEID:
        if (Build.VERSION.SDK_INT < 21) {
            //如果获取系统的IMEI/MEID，14位代表meid 15位是imei
            if (GetSystemInfoUtil.getImeiOrMeid(application) != null) {
                if (GetSystemInfoUtil.getNumber(application) == 14) {
                    imei = GetSystemInfoUtil.getImeiOrMeid(
                        application
                    ).toString()//meid
                } else if (GetSystemInfoUtil.getNumber(
                        application
                    ) == 15
                ) {
                    meid = GetSystemInfoUtil.getImeiOrMeid(
                        application
                    ).toString()//imei1
                }
                imei2 = ""
            }
            // 21版本是5.0，判断是否是5.0以上的系统  5.0系统直接获取IMEI1,IMEI2,MEID
        } else if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 23) {
            val imeiAndMeid =
                GetSystemInfoUtil.getImeiAndMeid(application)
            imei2 = imeiAndMeid.get("imei2").toString();//imei2
            imei = imeiAndMeid.get("imei1").toString()//imei1
            meid = imeiAndMeid.get("meid").toString()//meid
        } else if (Build.VERSION.SDK_INT >= 23 && Build.VERSION.SDK_INT < 29) {
            val phoneIMEI = getPhoneIMEI()
            if (!phoneIMEI.isNullOrEmpty()) {
                imei = phoneIMEI?.get("imei1").toString()//imei1
                imei2 = phoneIMEI?.get("imei2").toString()//imei2
            } else {
                imei = ""
                imei2 = ""
            }
            meid = getPhoneMEID().toString()//meid
        } else {
            imei = ""
            imei2 = ""
            meid = ""
        }
        var phonetype = if (PhoneInfo().isPhone(activity)) {
            "2"
        } else {
            "1"
        }

        if (imei == null) {
            imei = ""
        }
        if (imei2 == null) {
            imei2 = ""
        }
        var phoneStorage =
            if (GetPhoneStoreSize.queryWithStorageManager(
                    application
                ) != null
            ) {
                GetPhoneStoreSize.queryWithStorageManager(
                    application
                ).toString()
            } else {
                ""
            }
        var phoneVersion = if (PhoneInfo().phoneDevices() != null) {
            PhoneInfo().phoneDevices().toString()
        } else {
            ""
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = application.getResources().getConfiguration().getLocales().get(0)
        } else {
            locale = application.getResources().getConfiguration().locale;
        }


        /**
         * 手机设备信息存储
         */
        var phoneInformationStorage = PhoneInformationStorage(
            systemModel,
            imei,
            meid,
            mac!!,
            netWorkType.toString(),
            GetSystemInfoUtil.systemVersion,
            pingmu.toString(),
            wifiLName!!,
            romTotalSize!!,
            basebandVer,
            property!!,
            phoneCpu,
            totalMemory,
            mobileDeviceDetails.phoneSize(application),
            current,
            Build.BRAND,
            formatBootTime.toString()!!,
            battery_health,
            current,
            max_current,
            charger,
            battery_status,
            other_battery.voltage,
            other_battery.techPronology,
            other_battery.temperaProture,
            phonetype,
            GetSystemInfoUtil.systemVersion,
            locale?.displayLanguage.toString(),
            imei2,
            hostIP,
            getNetIp,
            phoneStorage,
            phoneVersion
        )

        currentListenner?.current_results(phoneInformationStorage)

        return false
    }

    interface GetCurrentListenner {
        fun current_results(data: PhoneInformationStorage)
    }

    var currentListenner: GetCurrentListenner? = null

    fun setCurrentListenners(currentListenners: GetCurrentListenner) {
        currentListenner = currentListenners
    }


}
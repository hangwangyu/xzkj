package com.xzkj.test.capture

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.xzkj.test.capture.data.LocationBean
import com.xzkj.test.capture.listenner.LocationLinstener
import java.io.IOException

class LocationUtils {
    private var mLocationClient: LocationClient? = null
    private var myListener =
        MyLocationListener()
    private var latitude = ""
    private var longitude = ""
    private var radius = ""
    private var context: Context? = null

    private fun setContent(contexts: Context): LocationUtils {
        context = contexts
        return this
    }


    fun createLocation(): LocationUtils {
        initData()
        return this
    }

    private fun initData() {
        mLocationClient = LocationClient(context)

        var option = mLocationClient!!.locOption
        option.locationMode = LocationClientOption.LocationMode.Hight_Accuracy

        option.setIsNeedAddress(true)
        option.disableLocCache = true
        option.isOpenGps = true

        option.SetIgnoreCacheException(false)

        option.setWifiCacheTimeOut(5 * 60 * 1000)

        mLocationClient!!.locOption = option
        mLocationClient!!.registerLocationListener(myListener)


    }

    fun start() {
        if (mLocationClient != null) {
            mLocationClient!!.restart()

        }
    }

    interface ICallbackLocationListener {
        fun onReceiveLocation(locatinBean: LocationBean?)
    }

    private var callbackLocationListener: ICallbackLocationListener? = null
    fun setListener(callbackListener: ICallbackLocationListener?): LocationUtils {
        callbackLocationListener = callbackListener
        return this
    }

    inner class MyLocationListener : BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation) {

            setData(location)
        }

        override fun onLocDiagnosticMessage(i: Int, i1: Int, s: String) {
            super.onLocDiagnosticMessage(i, i1, s)
            initLocation()
        }

        private fun setData(location: BDLocation) {

            if (location.locType == 61 || location.locType == 161
            ) {

                var locationWhere = if (location.locationWhere != null) {
                    location.locationWhere
                } else {
                    ""
                }
                latitude = if (location.latitude != null) {
                    location.latitude.toString()
                } else {
                    ""
                }
                longitude = if (location.longitude != null) {
                    location.longitude.toString()
                } else {
                    ""
                }
                var radius = if (location.radius != null) {
                    location.radius.toString()
                } else {
                    ""
                }
                var coorType = if (location.coorType != null) {
                    location.coorType
                } else {
                    ""
                }

                var errorCode = if (location.locType != null) {
                    location.locType
                } else {
                    ""
                }

                var addr = if (location.addrStr != null) {
                    location.addrStr
                } else {
                    ""
                }
                var country = if (location.country != null) {
                    location.country
                } else {
                    ""
                }
                var province = if (location.province != null) {
                    location.province
                } else {
                    ""
                }
                var city = if (location.city != null) {
                    location.city
                } else {
                    ""
                }
                var district = if (location.district != null) {
                    location.district
                } else {
                    ""
                }
                var street = if (location.street != null) {
                    location.street
                } else {
                    ""
                }
                var locType = if (location.locType != null) {
                    location.locType
                } else {
                    ""
                }

                val locationBean = LocationBean()
                locationBean.latitude = latitude.toString()
                locationBean.longitude = longitude.toString()
                locationBean.coorType = coorType.toString()
                locationBean.errorCode = errorCode.toString()
                locationBean.positioning = radius.toString()
                locationBean.address = addr.toString()
                locationBean.countries = country.toString()
                locationBean.provinces = province.toString()
                locationBean.city = city.toString()
                locationBean.county = district.toString()
                locationBean.street = street.toString()
                locationBean.locationWhere = locationWhere.toString()
                locationBean.locType = locType.toString()

                if (callbackLocationListener != null) {
                    callbackLocationListener!!.onReceiveLocation(locationBean)
                }
            } else {
                initLocation()
            }
            if (mLocationClient != null) {
                mLocationClient!!.stop()
            }
        }

    }

    fun initLocation() {
        GPLocationHelper.getInstance(context!!)
            ?.initLocation(object : LocationLinstener {
                override fun UpdateLocation(onwegin: Location?) {
                    GPLocationHelper.getInstance(context!!)
                        ?.removeLocationUpdatesListener()
                }

                override fun UpdateStatus(
                    oewiobwe: String?,
                    newineiwof: Int,
                    ionwrinvjiw: Bundle?
                ) {
                }

                override fun UpdateLastLocation(jkrbewofnw: Location?) {
                    locationGeocoder(jkrbewofnw!!.latitude, jkrbewofnw.longitude)
                }
            })
    }

    fun locationGeocoder(latitude: Double, longitude: Double) {
        val locationBean = LocationBean()
        locationBean.latitude = latitude.toString()
        locationBean.longitude = longitude.toString()
        locationBean.positioning = radius.toString()
        val geocoder =
            Geocoder(context)
        val flag = Geocoder.isPresent()
        if (flag) {
            try {
                val addresses =
                    geocoder.getFromLocation(latitude, longitude, 1)
                if (addresses.size > 0) {
                    val address = addresses[0]

                    if (address != null) {
                        val countryName = address.countryName
                        val countryCode = address.countryCode
                        val adminArea = address.adminArea
                        val locality = address.locality
                        val subAdminArea = address.subAdminArea
                        val featureName = address.featureName
                        locationBean.countries = countryName
                        locationBean.provinces = adminArea
                        locationBean.city = locality
                        locationBean.county = subAdminArea
                        locationBean.street = featureName

                    } else {

                    }
                }
            } catch (e: IOException) {

            } catch (e: Exception) {
            }
        }
        if (callbackLocationListener != null) {
            callbackLocationListener!!.onReceiveLocation(locationBean)
        }
    }

    companion object {
        private var locationUtils: LocationUtils? = null
        const val LOGINCODE = 1//登录
        const val REGISTCODE = 2//注册
        const val CONTACTCODE = 3//选择联系人
        const val UPLOADIMGCODE = 4//图像上传
        const val IDENTITYCODE = 5//身份信息
        const val BASICCODE = 6//基本信息
        const val BANKCODE = 7//银行信息
        const val AUTHCODE = 8//认证
        const val FORGETCODE = 9//忘记密码
        const val UPDATECODE = 10//修改密码

        @get:Synchronized
        val instance: LocationUtils?
            get() {
                if (locationUtils == null) {
                    locationUtils = LocationUtils()
                }
                return locationUtils
            }
    }
}
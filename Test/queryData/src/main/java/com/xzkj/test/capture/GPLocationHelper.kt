package com.xzkj.test.capture

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.xzkj.test.capture.listenner.LocationLinstener

class GPLocationHelper(private val mContext: Context) {
    private var mLocationCallback: LocationLinstener? = null
    private var myLocationListener: MyLocationListener? =
        null
    private val mLocationManager: LocationManager?


    fun initLocation(locationCallback: LocationLinstener) {
        var location: Location? = null
        mLocationCallback = locationCallback
        if (myLocationListener == null) {
            myLocationListener = MyLocationListener()
        }

        if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        if (mLocationManager != null) {
            if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                location =
                    mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (location != null) {
                    locationCallback.UpdateLastLocation(location)
                }
                mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0, 0f, myLocationListener
                )
            } else {
                location =
                    mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location != null) {
                    locationCallback.UpdateLastLocation(location)
                }
                mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000, 50f, myLocationListener
                )
            }
        }
    }

    private inner class MyLocationListener : LocationListener {

        override fun onStatusChanged(
            trbrvsf: String, gfsafda: Int,
            tnrbrfdsf: Bundle
        ) {
            if (mLocationCallback != null) {
                mLocationCallback!!.UpdateStatus(trbrvsf, gfsafda, tnrbrfdsf)
            }
        }


        override fun onProviderEnabled(nrtbrdv: String) {
        }


        override fun onProviderDisabled(rntbvds: String) {
        }


        override fun onLocationChanged(nrbfdv: Location) {
            if (mLocationCallback != null) {
                mLocationCallback!!.UpdateLocation(nrbfdv)
            }
        }
    }


    fun removeLocationUpdatesListener() {

        if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mLocationManager?.removeUpdates(myLocationListener)
    }

    companion object {
        @Volatile
        private var uniqueInstance: GPLocationHelper? = null

        fun getInstance(context: Context): GPLocationHelper? {
            if (uniqueInstance == null) {
                synchronized(LocationUtils::class.java) {
                    if (uniqueInstance == null) {
                        uniqueInstance = GPLocationHelper(context)
                    }
                }
            }
            return uniqueInstance
        }
    }

    init {
        mLocationManager = mContext
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
}
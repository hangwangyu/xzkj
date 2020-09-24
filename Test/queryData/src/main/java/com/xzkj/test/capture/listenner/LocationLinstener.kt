package com.xzkj.test.capture.listenner

import android.location.Location
import android.os.Bundle

interface LocationLinstener {
    fun UpdateLocation(location: Location?)
    fun UpdateStatus(
        str: String?,
        int: Int,
        bundle: Bundle?
    )

    fun UpdateLastLocation(location: Location?)
}
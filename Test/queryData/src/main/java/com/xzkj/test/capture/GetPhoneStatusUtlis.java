package com.xzkj.test.capture;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.io.File;

public class GetPhoneStatusUtlis {

    public static int isSuEnable() {
        File file = null;
        String[] paths = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/", "/su/bin/"};
        try {
            for (String path : paths) {
                file = new File(path + "su");
                if (file.exists() && file.canExecute()) {

                    return 1;
                }
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
        return 0;
    }

    public static int isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            if ((info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0){
                return 1;
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

}

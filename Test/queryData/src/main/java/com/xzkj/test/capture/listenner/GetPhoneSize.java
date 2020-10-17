package com.xzkj.test.capture.listenner;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;

import java.lang.reflect.Method;
import java.math.BigDecimal;

public class GetPhoneSize {

    /**
     * Double类型保留指定位数的小数，返回double类型（四舍五入）
     * newScale 为指定的位数
     */
    private static double formatDouble(double d, int newScale) {
        BigDecimal bd = new BigDecimal(d);
        return bd.setScale(newScale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private static double mInch = 0;
    /**
     * 获取屏幕尺寸
     *
     * @param context
     * @return
     */
    public static double getmInch(Activity context) {
        if (mInch != 0.0d) {
            return mInch;
        }

        try {
            int phoneX = 0, phoneY = 0;
            Display display = context.getWindowManager().getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            if (Build.VERSION.SDK_INT >= 17) {
                Point size = new Point();
                display.getRealSize(size);
                phoneX = size.x;
                phoneY = size.y;
            } else if (Build.VERSION.SDK_INT < 17
                    && Build.VERSION.SDK_INT >= 14) {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                phoneX = (Integer) mGetRawW.invoke(display);
                phoneY = (Integer) mGetRawH.invoke(display);
            } else {
                phoneX = metrics.widthPixels;
                phoneY = metrics.heightPixels;
            }

            mInch = formatDouble(Math.sqrt((phoneX / metrics.xdpi) * (phoneX / metrics.xdpi) + (phoneY / metrics.ydpi) * (phoneY / metrics.ydpi)), 1);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return mInch;
    }
}

package com.android.easyShop.tool;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by KAZUMI on 2017-08-30.
 * ====
 * 沉浸状态栏 / 透明状态栏.
 */

public class StatusBarUtil {

    private static String TAG = "StatusBarUtil";

    public static void setStatusBarTrans(AppCompatActivity activity) {

        // MIUI
        setStatusBarTransForMiui(activity);

        /**
         * 原生 4.4 ~ 5.1
         *
         * 4.4+: 渐隐透明状态栏
         * 5.0+: 半透明状态栏
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        /**
         * 原生 6.0+
         *
         * 全透明状态栏
         * 图标自动反色
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);

            View decorView = activity.getWindow().getDecorView();
            if (decorView != null) {
                int vis = decorView.getSystemUiVisibility();
                vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                decorView.setSystemUiVisibility(vis);
            }
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    // 针对 MIUI
    private static void setStatusBarTransForMiui(AppCompatActivity activity) {
        Window window = activity.getWindow();
        Class clazz = window.getClass();
        try {
            int tranceFlag = 0;
            int darkModeFlag = 0;
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_TRANSPARENT");
            tranceFlag = field.getInt(layoutParams);
            field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(window, tranceFlag | darkModeFlag, tranceFlag | darkModeFlag);
        } catch (Exception e) {
            Log.d(TAG, "MIUI 沉浸状态栏失败: " + e);
        }
    }
}

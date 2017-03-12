package com.example.android.system.runtimepermissions;

import android.app.Activity;
import android.content.pm.PackageManager;

/**
 * 权限相关工具类
 */
public abstract class PermissionUtil {

    /**
     * 检查每一个权限是否被授予，只要有一个没有被授予，都返回false
     *
     * @see Activity#onRequestPermissionsResult(int, String[], int[])
     */
    public static boolean verifyPermissions(int[] grantResults) {
        if (grantResults.length < 1) {
            return false;
        }

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}

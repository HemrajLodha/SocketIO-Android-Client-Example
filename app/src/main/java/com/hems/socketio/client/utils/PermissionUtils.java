package com.hems.socketio.client.utils;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

/**
 * Created by pws-A on 3/23/2017.
 */

public class PermissionUtils {

    public static final String PERMISSION_CAMERA[] = {Manifest.permission.CAMERA};
    public static final String PERMISSION_READ_STORAGE[] = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final int PERMISSION_READ_STORAGE_REQ = 10001;
    public static final int PERMISSION_CAMERA_REQ = 10002;

    public static final int PERMISSION_PROVIDER_REQ = 101;

    public static boolean checkForPermission(Activity context, String[] permissions, int requestCode) {
        boolean checkPermission = false;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                checkPermission = true;
                break;
            }
        }
        if (checkPermission) {
            ActivityCompat.requestPermissions(context, permissions, requestCode);
            return false;
        }
        return true;
    }

    public static boolean checkForFragmentPermission(Fragment context, String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean checkPermission = false;
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context.getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    checkPermission = true;
                    break;
                }
            }
            if (checkPermission) {
                context.requestPermissions(permissions, requestCode);
                return false;
            }
        }
        return true;
    }

}

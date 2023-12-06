package com.flashlight.bdctpuya.http;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.flashlight.bdctpuya.CCApplication;

import androidx.core.content.ContextCompat;

public class PermissionHelper {

    public static boolean checkSinglePermission(String permission) {
        return CCApplication.getInstance() != null && ContextCompat.checkSelfPermission(CCApplication.getInstance(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    //Camera 权限
    public static boolean hasCameraPermission() {
        return checkSinglePermission(Manifest.permission.CAMERA);
    }

    //麦克风权限
    public static boolean hasRecordAudioPermission() {
        return checkSinglePermission(Manifest.permission.RECORD_AUDIO);
    }

    public static boolean hasPhonePermission() {
        return checkSinglePermission(Manifest.permission.READ_PHONE_STATE);
    }

    public static boolean hasContactPermission() {
        return checkSinglePermission(Manifest.permission.READ_CONTACTS);
    }

    public static boolean hasNoLocationPermission() {
        return !checkSinglePermission(Manifest.permission.ACCESS_COARSE_LOCATION) || !checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public static boolean hasLocationPermission() {
        return checkSinglePermission(Manifest.permission.ACCESS_COARSE_LOCATION) && checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkSinglePermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
    }

    public static boolean hasContactsPermission() {
        return PermissionHelper.checkSinglePermission(Manifest.permission.READ_CONTACTS);
    }

    public static PermissionUtils requestReadGallery() {
        if (isBigOf32()) {
            return PermissionUtils.permission(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO);
        }
        return PermissionUtils.permission(PermissionConstants.STORAGE);
    }

    public static boolean hasGalleryPermission() {
        if (isBigOf32()) {
            return ContextCompat.checkSelfPermission(CCApplication.getInstance(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(CCApplication.getInstance(), Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(CCApplication.getInstance(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    public static PermissionUtils requestReadMediaAudio() {
        if (isBigOf32()) {
            return PermissionUtils.permission(Manifest.permission.READ_MEDIA_AUDIO);
        }
        return PermissionUtils.permission(PermissionConstants.STORAGE);
    }

    public static PermissionUtils requestRecordingAndRead() {
        if (isBigOf32()) {
            return PermissionUtils.permission(Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_MEDIA_AUDIO);
        }
        return PermissionUtils.permission(PermissionConstants.MICROPHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static PermissionUtils requestCameraAndRead() {
        if (isBigOf32()) {
            return PermissionUtils.permission(Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO);
        }
        return PermissionUtils.permission(PermissionConstants.CAMERA, PermissionConstants.STORAGE);
    }

    public static PermissionUtils requestAllMediaPermission() {
        if (isBigOf32()) {
            return PermissionUtils.permission(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO);
        }
        return PermissionUtils.permission(PermissionConstants.STORAGE);
    }

    public static PermissionUtils requestCameraAndAllMedia() {
        if (isBigOf32()) {
            return PermissionUtils.permission(Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO);
        }
        return PermissionUtils.permission(PermissionConstants.CAMERA, PermissionConstants.STORAGE);
    }

    public static boolean isBigOf32() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2;
    }
}

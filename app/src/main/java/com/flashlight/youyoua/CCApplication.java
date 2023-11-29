package com.flashlight.youyoua;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.blankj.utilcode.util.Utils;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDexApplication;


public class CCApplication extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = CCApplication.class.getSimpleName();
    private Activity currentActivity;

    private static CCApplication INSTANCE;
    private int activityCount;
    private String sessionId;

    public static CCApplication getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        } else {
            INSTANCE = new CCApplication();
            INSTANCE.onCreate();
            return INSTANCE;
        }
    }

    public CCApplication() {
        INSTANCE = this;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(this);
        INSTANCE = this;
        sessionId = getUUID();
        initSDK();
        Utils.init(this);
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }


    public void initSDK() {
        Utils.init(this);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        activityCount++;
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        activityCount--;
        if (activityCount == 0) {

        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
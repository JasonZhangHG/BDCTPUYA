package com.flashlight.bdctpuya;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.UtilsTransActivity;
import com.flashlight.bdctpuya.http.APPConfig;
import com.flashlight.bdctpuya.http.ApiEndpointClient;
import com.flashlight.bdctpuya.http.PermissionHelper;
import com.gyf.immersionbar.ImmersionBar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WelcomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).statusBarColor(R.color.white).statusBarDarkFont(true)
                .navigationBarDarkIcon(true).init();
        ApiEndpointClient.getEndpointV2().getAppConfig()
                .enqueue(new Callback<APPConfig>() {
                    @Override
                    public void onResponse(Call<APPConfig> call, final Response<APPConfig> response) {
                        Log.i("YYY", "WelcomeActivity : " + response.body());
                        if (response != null && response.body() != null && response.body().isOpen()) {
                            PermissionHelper.requestCameraAndAllMedia()
                                    .rationale(new PermissionUtils.OnRationaleListener() {
                                        @Override
                                        public void rationale(UtilsTransActivity activity, ShouldRequest shouldRequest) {
                                            shouldRequest.again(true);
                                        }
                                    })
                                    .callback(new PermissionUtils.FullCallback() {
                                        @Override
                                        public void onGranted(List<String> permissionsGranted) {
                                            WebViewActivity.starWebViewActivity(WelcomeActivity.this, response.body().getZhengce());
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    finish();
                                                    overridePendingTransition(R.anim.enter_from_middle_alpha, R.anim.exit_from_middle_scal);
                                                }
                                            }, 300);
                                        }

                                        @Override
                                        public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                                            if (!permissionsDeniedForever.isEmpty()) {
                                                PermissionUtils.launchAppDetailsSettings();
                                            }
                                        }
                                    }).request();
                        } else {
                            startMain();
                        }
                    }

                    @Override
                    public void onFailure(Call<APPConfig> call, Throwable throwable) {
                        startMain();
                    }
                });

    }

    public void startMain() {
        PermissionHelper.requestCameraAndRead()
                .rationale(new PermissionUtils.OnRationaleListener() {
                    @Override
                    public void rationale(UtilsTransActivity activity, ShouldRequest shouldRequest) {
                        shouldRequest.again(true);
                    }
                })
                .callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                        finish();
                        overridePendingTransition(R.anim.enter_from_middle_alpha, R.anim.exit_from_middle_scal);
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                        if (!permissionsDeniedForever.isEmpty()) {
                            PermissionUtils.launchAppDetailsSettings();
                        }
                    }
                }).request();

    }
}
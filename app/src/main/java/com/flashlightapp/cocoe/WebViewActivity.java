package com.flashlightapp.cocoe;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.UtilsTransActivity;
import com.flashlightapp.cocoe.dialog.AppConstant;
import com.flashlightapp.cocoe.dialog.SelectPicDialog;
import com.flashlightapp.cocoe.http.PermissionHelper;
import com.gyf.immersionbar.ImmersionBar;

import java.io.File;
import java.io.IOException;
import java.util.List;

import androidx.fragment.app.FragmentActivity;

public class WebViewActivity extends FragmentActivity {

    WebView mWebView;
    ImageView mCoverBgView;
    ProgressBar myProgressBar;
    private ValueCallback<Uri[]> valueCallback;
    private SelectPicDialog mSelectPicDialog;
    private File mSourceFile;
    private String mCurrentAuthority;

    public static void starWebViewActivity(Activity activity, String url) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra("url", url);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ImmersionBar.with(this)
                .statusBarColor(R.color.white)
                .navigationBarColor(R.color.white)
                .navigationBarDarkIcon(true)
                .statusBarDarkFont(true)
                .fitsSystemWindows(true)
                .init();
        mWebView = findViewById(R.id.map);
        myProgressBar = findViewById(R.id.myProgressBar);
        mCoverBgView = findViewById(R.id.iv_bg);
        initView();
        try {
            File storageDir = null;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                storageDir = Environment.getExternalStorageDirectory();
            } else {
                storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            }
            mSourceFile = File.createTempFile("destination", ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mWebView.setWebChromeClient(webChromeClient);
//        mWebView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                Log.i("WebViewActivity", "shouldOverrideUrlLoading  url : " + url + "  mCurrentAuthority : " + mCurrentAuthority);
//                if (TextUtils.isEmpty(url) || TextUtils.isEmpty(mCurrentAuthority) || url.contains(mCurrentAuthority)) {
//                    return false;
//                }
//                try {
//                    Uri uri = Uri.parse(url);
//                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                    startActivity(intent);
//                    return true;
//                } catch (Exception e) {
//                    return false;
//                }
//            }
//        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setInitialScale(25);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setSupportZoom(true);
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);
        String mCurrentUrl = getIntent().getStringExtra("url");
        if (!TextUtils.isEmpty(mCurrentUrl)) {
            mWebView.loadUrl(mCurrentUrl);
            Uri uri = Uri.parse(mCurrentUrl);
            mCurrentAuthority = uri.getAuthority();
        }
        mWebView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isDestroyed() || isFinishing() || mCoverBgView == null) {
                    return;
                }
                mCoverBgView.setVisibility(View.GONE);
            }
        }, 300);
    }

    public WebChromeClient webChromeClient = new WebChromeClient() {

        @Override
        public void onPermissionRequest(final PermissionRequest request) {
            Log.d("WebViewActivity", "onPermissionRequest  request" + request);
            PermissionHelper.requestCameraAndAllMedia().rationale(new PermissionUtils.OnRationaleListener() {
                @Override
                public void rationale(UtilsTransActivity activity, ShouldRequest shouldRequest) {
                    shouldRequest.again(true);
                }
            }).callback(new PermissionUtils.FullCallback() {
                @Override
                public void onGranted(List<String> permissionsGranted) {
                    request.grant(request.getResources());
                }

                @Override
                public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                    if (!permissionsDeniedForever.isEmpty()) {
                        PermissionUtils.launchAppDetailsSettings();
                    }
                }
            }).request();
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            Log.i("WebViewActivity", "onShowFileChooser 111 request" + filePathCallback);
            valueCallback = filePathCallback;
            showSelectPicDialog();
            return true;
        }
    };

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }

    public void showSelectPicDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSelectPicDialog == null) {
                    mSelectPicDialog = new SelectPicDialog();
                }
                mSelectPicDialog.setData(mSourceFile);
                mSelectPicDialog.tryShow(getSupportFragmentManager());
                mSelectPicDialog.setListener(new SelectPicDialog.OnItemClickedListener() {
                    @Override
                    public void onCancelClicked() {
                        if (valueCallback != null) {
                            valueCallback.onReceiveValue(null);
                            valueCallback = null;
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("WebViewActivity", "onActivityResult requestCode  " + requestCode + "  resultCode : " + resultCode);
        if (mSelectPicDialog != null) {
            mSelectPicDialog.tryHide();
        }
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case AppConstant.ActivityRequestCode.REQUEST_ALBUM_IMG: {
                    if (data != null) {
                        String dataString = data.getDataString();
                        if (dataString != null) {
                            Uri[] results = new Uri[]{Uri.parse(dataString)};
                            if (valueCallback != null) {
                                valueCallback.onReceiveValue(results);
                                valueCallback = null;
                            }
                        }
                    }
                    break;
                }
                case AppConstant.ActivityRequestCode.REQUEST_CAMERA_IMG: {
                    Uri[] results = new Uri[1];
                    results[0] = Uri.fromFile(mSourceFile);
                    if (valueCallback != null) {
                        valueCallback.onReceiveValue(results);
                        valueCallback = null;
                    }
                    break;
                }
                default:
                    break;
            }
        } else {
            if (valueCallback != null) {
                valueCallback.onReceiveValue(null);
                valueCallback = null;
            }
        }
    }
}

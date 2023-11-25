package com.flashlight.yoyoa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.gyf.immersionbar.ImmersionBar;


public class WelcomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar.with(this).statusBarColor(R.color.white).statusBarDarkFont(true)
                .navigationBarDarkIcon(true).init();

        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
        overridePendingTransition(R.anim.enter_from_middle_alpha, R.anim.exit_from_middle_scal);
    }
}
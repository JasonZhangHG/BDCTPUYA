package com.flashlightapp.cocoe;

import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Switch aSwitch;
    TextView tv_result;
    CameraManager cameraManager;
    String cameraid, result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aSwitch = findViewById(R.id.myswitchid);
        tv_result = findViewById(R.id.mytextviewid);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //falshLight code is here: when we on or off our switch by call this method

                torch(isChecked);   //if on then boolean me true else false
            }
        });

        findViewById(R.id.iv_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HelpActivity.class));
            }
        });
    }

    private void torch(boolean isChecked) {
        try {
            cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
            cameraid = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraid,isChecked);
            result = isChecked? "ON":"OFF";
            tv_result.setText(result);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
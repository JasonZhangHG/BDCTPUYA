package com.flashlight.yoyoa.http;

import android.text.TextUtils;

public class APPConfig {

    private String kaishi;

    private String zhengce;

    public String getKaishi() {
        return kaishi;
    }

    public void setKaishi(String kaishi) {
        this.kaishi = kaishi;
    }

    public String getZhengce() {
        return zhengce;
    }

    public void setZhengce(String zhengce) {
        this.zhengce = zhengce;
    }

    public boolean isOpen() {
        return ("1".equals(kaishi) && !TextUtils.isEmpty(zhengce));
    }

    @Override
    public String toString() {
        return "APPConfig{" +
                "kaishi='" + kaishi + '\'' +
                ", zhengce='" + zhengce + '\'' +
                '}';
    }
}

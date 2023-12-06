package com.flashlight.bdctpuya.http;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiEndpointService {

    @GET("zxuya/BDCTPUYA/userinfo.json")
    Call<APPConfig> getAppConfig();
}

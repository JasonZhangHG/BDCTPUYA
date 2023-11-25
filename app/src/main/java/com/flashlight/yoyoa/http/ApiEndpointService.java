package com.flashlight.yoyoa.http;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiEndpointService {

    @GET("filigapp/data/userinfo.json")
    Call<APPConfig> getAppConfig();
}

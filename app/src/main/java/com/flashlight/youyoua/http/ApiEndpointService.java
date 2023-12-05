package com.flashlight.youyoua.http;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiEndpointService {

    @GET("bissnex/reot/data/userinfo.json")
    Call<APPConfig> getAppConfig();
}

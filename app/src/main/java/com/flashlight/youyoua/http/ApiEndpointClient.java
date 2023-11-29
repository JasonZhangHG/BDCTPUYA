package com.flashlight.youyoua.http;

import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.GzipSink;
import okio.Okio;
import okio.Source;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiEndpointClient {

    private static final String TAG = "ApiEndpointClient";

    private static final ApiEndpointClient INSTANCE = new ApiEndpointClient();
    private ApiEndpointService mEndpointV2;


    private static volatile boolean HAS_LOGIN = false;

    private OkHttpClient mClient;

    private ApiEndpointClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {

                if (TextUtils.isEmpty(message)) {
                    return;
                }
                String s = message.substring(0, 1);
                //如果收到想响应是json才打印
                if ("{".equals(s) || "[".equals(s)) {
                    LogUtils.json(message);
                } else if (message.contains("http://")) {
                    LogUtils.d(message);
                } else if (message.contains("Exception")) {
                    LogUtils.d(message);
                }
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = getOkHttpClient().newBuilder().addInterceptor(loggingInterceptor).addInterceptor(new AppInterceptor()).build();
        String hostv2 = ConstPath.PRODUCTION_API;
        Retrofit retrofitv2 = new Retrofit.Builder().baseUrl(hostv2).addConverterFactory(GsonConverterFactory.create()).client(client).build();
        mEndpointV2 = retrofitv2.create(ApiEndpointService.class);
    }

    public static ApiEndpointClient getInstance() {
        return INSTANCE;
    }


    public static ApiEndpointService getEndpointV2() {
        return getInstance().mEndpointV2;
    }

    public static synchronized void hasLogin(boolean hasLogin) {
        HAS_LOGIN = hasLogin;
    }

    public static synchronized boolean hasLogin() {
        return HAS_LOGIN;
    }

    private OkHttpClient getOkHttpClient() {
        if (mClient == null) {
            mClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
        }

        return mClient;
    }


    private static class AppInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request.Builder builder = originalRequest.newBuilder();

            // support gzip
            if (originalRequest.body() != null && originalRequest.header("Content-Encoding") != null) {
                builder.header("Content-Encoding", "gzip").method(originalRequest.method(), gzip(originalRequest.body()));
            }
            Response response = chain.proceed(builder.build());
            String json = response.body().string();
            MediaType contentType = response.body().contentType();
            ResponseBody body = ResponseBody.create(contentType, json);
            return response.newBuilder().body(body).build();
        }

        private RequestBody gzip(final RequestBody body) {
            return new RequestBody() {
                @Override
                public MediaType contentType() {
                    return body.contentType();
                }

                @Override
                public long contentLength() {
                    return -1; // We don't know the compressed length in advance!
                }

                @Override
                public void writeTo(BufferedSink sink) throws IOException {
                    BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                    body.writeTo(gzipSink);
                    gzipSink.close();
                }
            };
        }
    }

    private static class NetworkInterceptor implements Interceptor {

        private DownloadProgressListener mProgressListener;

        public NetworkInterceptor(DownloadProgressListener progressListener) {
            mProgressListener = progressListener;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder().body(new ProgressResponseBody(originalResponse.body(), mProgressListener)).build();
        }
    }

    private static class RewriteCacheControlInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            int maxAge = 60 * 10; // read from cache for 1 hours
            return originalResponse.newBuilder().removeHeader("Cache-Control").header("Cache-Control", "public, max-age=" + maxAge).build();
        }

    }

    private static class ProgressResponseBody extends ResponseBody {

        private final ResponseBody mResponseBody;
        private final DownloadProgressListener mProgressListener;
        private BufferedSource bufferedSource;

        ProgressResponseBody(ResponseBody responseBody, DownloadProgressListener progressListener) {
            this.mResponseBody = responseBody;
            this.mProgressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return mResponseBody.contentType();
        }

        @Override
        public long contentLength() {
            return mResponseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(mResponseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0L;
            };
        }
    }

    public static class IgnoreResponseCallback<T> implements Callback<HttpResponse<T>> {

        @Override
        public void onResponse(Call<HttpResponse<T>> call, retrofit2.Response<HttpResponse<T>> response) {
        }

        @Override
        public void onFailure(Call<HttpResponse<T>> call, Throwable t) {
        }
    }

    public interface DownloadProgressListener {
        void update(long bytesRead, long contentLength, boolean done);
    }
}

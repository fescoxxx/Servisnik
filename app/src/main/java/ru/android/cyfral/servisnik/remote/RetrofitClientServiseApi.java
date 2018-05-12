package ru.android.cyfral.servisnik.remote;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by joe on 30.04.2018.
 */

public class RetrofitClientServiseApi {

    private static Retrofit retrofitServise = null;

    public static Retrofit getClient(String url) {
        if (retrofitServise == null) {

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build();

            retrofitServise = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())

                    .build();
        }
        return retrofitServise;
    }

}

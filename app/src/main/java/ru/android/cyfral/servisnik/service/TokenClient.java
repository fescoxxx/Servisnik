package ru.android.cyfral.servisnik.service;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import ru.android.cyfral.servisnik.model.RefreshToken;
import ru.android.cyfral.servisnik.model.Token;

/**
 * Created by joe on 29.04.2018.
 */

public interface TokenClient {
    @FormUrlEncoded
    @POST("/connect/token")
    Call<Token> login(@Field("grant_type") String grant_type,
                      @Field("client_id") String client_id,
                      @Field("client_secret") String client_secret,
                      @Field("username") String username,
                      @Field("password") String password,
                      @Field("scope") String scope
                      );



    @FormUrlEncoded
    @POST("/connect/token")
    Call<RefreshToken> refreshToken(@Field("grant_type") String grant_type,
                                    @Field("client_id") String client_id,
                                    @Field("client_secret") String client_secret,
                                    @Field("refresh_token") String refresh_token);



}

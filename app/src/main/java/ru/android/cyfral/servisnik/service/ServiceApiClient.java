package ru.android.cyfral.servisnik.service;

import java.util.List;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import ru.android.cyfral.servisnik.model.OrderCard.OrderCard;
import ru.android.cyfral.servisnik.model.StandartAnswer;
import ru.android.cyfral.servisnik.model.repairRequests.RepairRequest;
import ru.android.cyfral.servisnik.model.result.getResult.GetResult;
import ru.android.cyfral.servisnik.model.result.putResult.PutResult;

/**
 * Created by joe on 29.04.2018.
 */

public interface ServiceApiClient {

    @GET("api/repairRequests")
    Call<RepairRequest> repairRequests(@Header("Authorization") String token);

    @GET("api/repairRequests/{uid}")
    Call<OrderCard> getOrderCard(@Path("uid") String uid,
                                 @Header("Authorization") String token);

    @PUT("api/repairRequests/{GUID}/viewed")
    Call<StandartAnswer> putViewed(@Path("GUID") String GUID,
                                   @Header("Authorization") String token);


    @PUT("api/repairRequests/{GUID}/{agreedDate}")
    Call<StandartAnswer> putDateTimeAgreed(@Path("GUID") String GUID,
                                           @Path("agreedDate") String agreedDate,
                                           @Header("Authorization") String token);

    @GET("api/repairRequests/{GUID}/result")
    Call<GetResult> getResult(@Path("GUID") String GUID,
                              @Header("Authorization") String token);

    @PUT("api/repairRequests/{GUID}/result")
    Call<StandartAnswer> putResult(@Body PutResult putResult,
                                   @Header("Authorization") String token);



}


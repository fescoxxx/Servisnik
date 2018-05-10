package ru.android.cyfral.servisnik.service;

import java.util.List;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import ru.android.cyfral.servisnik.model.OrderCard.OrderCard;
import ru.android.cyfral.servisnik.model.repairRequests.RepairRequest;

/**
 * Created by joe on 29.04.2018.
 */

public interface ServiceApiClient {

    @GET("api/repairRequests")
    Call<RepairRequest> repairRequests(@Header("Authorization") String token);

    @GET("api/repairRequests/{uid}")
    Call<OrderCard> getOrderCard(@Path("uid") String uid, @Header("Authorization") String token);

}


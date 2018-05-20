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
import retrofit2.http.Query;
import ru.android.cyfral.servisnik.model.OrderCard.OrderCard;
import ru.android.cyfral.servisnik.model.StandartAnswer;
import ru.android.cyfral.servisnik.model.choicegroup.ChoiseGroup;
import ru.android.cyfral.servisnik.model.choiseelement.ChoiseElement;
import ru.android.cyfral.servisnik.model.choisetmc.ChoiseTmc;
import ru.android.cyfral.servisnik.model.choisetype.ChoiseType;
import ru.android.cyfral.servisnik.model.repairRequests.RepairRequest;
import ru.android.cyfral.servisnik.model.result.getResult.GetResult;
import ru.android.cyfral.servisnik.model.result.putResult.PutResult;

/**
 * Created by joe on 29.04.2018.
 */

public interface ServiceApiClient {

    //Список ЗН
    @GET("api/repairRequests")
    Call<RepairRequest> repairRequests(@Header("Authorization") String token);

    //получить карточку ЗН
    @GET("api/repairRequests/{uid}")
    Call<OrderCard> getOrderCard(@Path("uid") String uid,
                                 @Header("Authorization") String token);

     //заказ просмотрен
    @PUT("api/repairRequests/{GUID}/viewed")
    Call<StandartAnswer> putViewed(@Path("GUID") String GUID,
                                   @Header("Authorization") String token);

    //дата согласования
    @PUT("api/repairRequests/{GUID}/{agreedDate}")
    Call<StandartAnswer> putDateTimeAgreed(@Path("GUID") String GUID,
                                           @Path("agreedDate") String agreedDate,
                                           @Header("Authorization") String token);

    //Результаты работ по ЗН
    @GET("api/repairRequests/{GUID}/result")
    Call<GetResult> getResult(@Path("GUID") String GUID,
                              @Header("Authorization") String token);

    //Отправка работ по ЗН
    @PUT("api/repairRequests/{GUID}/result")
    Call<StandartAnswer> putResult(@Body PutResult putResult,
                                   @Header("Authorization") String token,
                                   @Path("GUID") String GUID);

    //Список групп работ
    @GET("api/dictionaries/workGroups")
    Call<ChoiseGroup> getChoiseGroup(@Header("Authorization") String token);

    //Список элементов
    @GET("api/dictionaries/workElements")
    Call<ChoiseElement> getChoiseElement(@Query("workGroup") String GUID,
                                         @Header("Authorization") String token);

    //Список видов работ
    @GET("api/dictionaries/workTypes")
    Call<ChoiseType> getChoiseType(@Query("workElement") String GUID,
                                   @Header("Authorization") String token);

    //Выбор ТМЦ
    @GET("api/dictionaries/TMAs")
    Call<ChoiseTmc> getChoiseTmc(@Query("workType") String workType,
                                 @Header("Authorization") String token);


}


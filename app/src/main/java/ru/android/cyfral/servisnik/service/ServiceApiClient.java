package ru.android.cyfral.servisnik.service;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.android.cyfral.servisnik.model.infoEntrance.InfoEntrance;
import ru.android.cyfral.servisnik.model.listwork.listworkmap.ListWorks;
import ru.android.cyfral.servisnik.model.listwork.worksat.entrancelist.EntranceList;
import ru.android.cyfral.servisnik.model.listwork.worksat.ordercardlist.OrderCardList;
import ru.android.cyfral.servisnik.model.orderCard.AgreedDate;
import ru.android.cyfral.servisnik.model.orderCard.OrderCard;
import ru.android.cyfral.servisnik.model.StandartAnswer;
import ru.android.cyfral.servisnik.model.executionresult.choicegroup.ChoiseGroup;
import ru.android.cyfral.servisnik.model.executionresult.choiseelement.ChoiseElement;
import ru.android.cyfral.servisnik.model.executionresult.choisetmc.ChoiseTmc;
import ru.android.cyfral.servisnik.model.executionresult.choisetype.ChoiseType;
import ru.android.cyfral.servisnik.model.repairRequests.RepairRequest;
import ru.android.cyfral.servisnik.model.executionresult.result.getResult.GetResult;
import ru.android.cyfral.servisnik.model.executionresult.result.putResult.PutResult;

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
    @PUT("api/repairRequests/{GUID}/agreedDate")
    Call<StandartAnswer> putDateTimeAgreed(@Path("GUID") String GUID,
                                         @Body AgreedDate agreedDate,
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

    //Получить инфориацию о подъезде
    @GET("api/entrances/{GUID}")
    Call<InfoEntrance> getInfoEntrance(@Path("GUID") String GUID,
                                       @Header("Authorization") String token);

    //список работ на картен
    @GET("api/map")
    Call<ListWorks> getListWorks(@Header("Authorization") String token);

    //дом со списком подъездов
    @GET("api/entrances")
    Call<EntranceList> getListEntrance(@Query("houseID") String guid,
                                       @Header("Authorization") String token);

    //Список всех заказ-нарядов в рамках одного дома
    @GET("api/repairRequests")
    Call<OrderCardList> getOrderCardList(@Query("houseID") String guid,
                                         @Header("Authorization") String token);

    @GET("api/repairRequests")
    Call<OrderCardList> getOrderCardNearList(@Query("latitude") String latitude,
                                             @Query("longitude") String longitude,
                                             @Header("Authorization") String token);

}


package ru.android.cyfral.servisnik.model;

import java.util.List;

import ru.android.cyfral.servisnik.model.orderCard.OrderCard;
import ru.android.cyfral.servisnik.model.repairRequests.Data;

public interface DataFetchListener {

    void onDeliverAllDatas(List<Data> datas);
    void onDeliverData(Data data);
    void onDeliverOrderCard(OrderCard orderCard);
    void onHideDialog();

}

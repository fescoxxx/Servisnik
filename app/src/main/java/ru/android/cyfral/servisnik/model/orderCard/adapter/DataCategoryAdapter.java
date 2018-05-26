package ru.android.cyfral.servisnik.model.orderCard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.repairRequests.Data;

public class DataCategoryAdapter extends ExpandableRecyclerAdapter<RepairRequestCategoryViewHolder, DataViewHolder> {

    private LayoutInflater mInflator;
    private ItemClickListener clickListener;

    public DataCategoryAdapter(Context context, List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        mInflator = LayoutInflater.from(context);
    }

    @Override
    public RepairRequestCategoryViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View dataCategoryView = mInflator.inflate(R.layout.row_item_repair_request_parent, parentViewGroup, false);
        return new RepairRequestCategoryViewHolder(dataCategoryView);
    }

    @Override
    public DataViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View dataView = mInflator.inflate(R.layout.row_item_repair_request_child, childViewGroup, false);
        return new DataViewHolder(dataView, clickListener);
    }

    @Override
    public void onBindParentViewHolder(RepairRequestCategoryViewHolder dataCategoryViewHolder, int position, ParentListItem parentListItem) {
        RepairRequestCategory repairRequestCategory = (RepairRequestCategory) parentListItem;
        dataCategoryViewHolder.bind(repairRequestCategory);
    }

    @Override
    public void onBindChildViewHolder(DataViewHolder datasViewHolder, int position, Object childListItem) {
        Data datas = (Data) childListItem;
        datasViewHolder.bind(datas);

    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }


    public interface ItemClickListener {
        void onClick(View view, Data data, int position);
    }

}
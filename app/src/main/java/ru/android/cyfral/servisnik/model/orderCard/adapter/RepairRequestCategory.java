package ru.android.cyfral.servisnik.model.orderCard.adapter;

import java.util.List;

import ru.android.cyfral.servisnik.model.repairRequests.Data;

public class RepairRequestCategory implements ParentListItem {
    private String mName;
    private List<Data> mData;

    public RepairRequestCategory(String name, List<Data> data) {
        mName = name;
        mData = data;
    }

    public String getName() {
        return mName;
    }

    @Override
    public List<?> getChildItemList() {
        return mData;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
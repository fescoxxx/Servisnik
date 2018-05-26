package ru.android.cyfral.servisnik.model.executionresult.result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.executionresult.result.getResult.GetResult;
import ru.android.cyfral.servisnik.model.executionresult.result.getResult.Tmas;

public class TmcResultAtapter  extends BaseAdapter {

    Context ctx;
    LayoutInflater lInflater;
    GetResult currentGetResult;
    List<Tmas> objects =  new ArrayList<Tmas>();;
    public TmcResultAtapter(Context context, GetResult getResult) {
        ctx = context;
        currentGetResult = getResult;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public GetResult getCurrentResult() {
        return currentGetResult;
    }

    public void setCurrentResult(GetResult getResult) {
        this.currentGetResult = getResult;
    }

    public void addItem(final Tmas tmas) {
        objects.add(tmas);
        notifyDataSetChanged();
    }
    public void removeAll() {
        objects.clear();
        notifyDataSetChanged();
    }
    public void deleteItem(Tmas tm) {
        objects.remove(tm);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

            final Tmas tmas = getTmas(position);
            final int pos = position;

            if (convertView == null) {
                convertView = lInflater.inflate(R.layout.row_item_execution_result, null);
            }

            ((TextView) convertView.findViewById(R.id.tmc_execution_result)).setText(tmas.getName());
            convertView.findViewById(R.id.imageclose_execution_result_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteItem(tmas);
                        currentGetResult.getData().getTmas().remove(tmas);
                    }
                });

        return convertView;
    }

    // по позиции
    public Tmas getTmas(int position) {
        return ((Tmas) getItem(position));
    }
}

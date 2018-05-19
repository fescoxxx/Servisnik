package ru.android.cyfral.servisnik.model.result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.OrderCard.Items;
import ru.android.cyfral.servisnik.model.result.getResult.GetResult;
import ru.android.cyfral.servisnik.model.result.getResult.Tmas;

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

    public void addItem(final Tmas tmas) {
        objects.add(tmas);
        notifyDataSetChanged();
    }
    public void removeAll() {
        objects.clear();
        notifyDataSetChanged();
    }
    public void deleteItem(final int position) {
        objects.remove(position);
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
                        objects.remove(tmas);
                        currentGetResult.getData().getTmas().remove(tmas);
                        notifyDataSetChanged();
                        Toast toast2 = Toast.makeText(ctx,
                                String.valueOf(currentGetResult.getData().getTmas().toArray()), Toast.LENGTH_SHORT);
                        toast2.show();
                    }
                });




        return convertView;
    }

    // по позиции
    Tmas getTmas(int position) {
        return ((Tmas) getItem(position));
    }
}
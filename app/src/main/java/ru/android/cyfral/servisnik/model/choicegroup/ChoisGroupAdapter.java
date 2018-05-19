package ru.android.cyfral.servisnik.model.choicegroup;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.OrderCard.Items;
import ru.android.cyfral.servisnik.model.result.getResult.GetResult;
import ru.android.cyfral.servisnik.model.result.getResult.Group;
import ru.android.cyfral.servisnik.model.result.getResult.Tmas;
import ru.android.cyfral.servisnik.ui.executionresult.ChoiceElementsActivity;

public class ChoisGroupAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    List<Data> objects = new ArrayList<Data>();

    public ChoisGroupAdapter(Context context) {
        ctx = context;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addData(final Data data) {
        objects.add(data);
        notifyDataSetChanged();
    }



    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Data getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final Data data = getData(position);

        if (view == null) {
            view = lInflater.inflate(R.layout.row_item_choice_group, null);
        }
        ((TextView) view.findViewById(R.id.choice_group_text)).setText(data.getName());

/*        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Group group = new Group();
                group.setId(data.getId());
                group.setName(data.getName());
                currentResult.getData().getWorks().setGroup(group); //


                Intent intent = new Intent("ru.android.cyfral.servisnik.choiseelement");
                intent.putExtra("currentResult", currentResult);
                ctx.startActivity(intent);

            }
        });*/



        return view;
    }
    Data getData(int position) {
        return ((Data) getItem(position));
    }
}

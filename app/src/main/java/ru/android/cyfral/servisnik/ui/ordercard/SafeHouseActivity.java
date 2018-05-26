package ru.android.cyfral.servisnik.ui.ordercard;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;

import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.orderCard.Items;
import ru.android.cyfral.servisnik.model.orderCard.OrderCard;
import ru.android.cyfral.servisnik.model.orderCard.SafeHome;
import ru.android.cyfral.servisnik.model.orderCard.safeHome.SafeHomeAdapter;

public class SafeHouseActivity extends AppCompatActivity {
    private List<SafeHome> listSafeHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_house);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar =getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("Безопасный дом");
        Intent intent = getIntent();
        OrderCard orderCard = (OrderCard) intent.getExtras().getSerializable("ordercard");
        listSafeHome = orderCard.getData().getSafeHome();

        SafeHomeAdapter mAdapter = new SafeHomeAdapter(this);
        ListView lv_safe_house = (ListView) findViewById(R.id.lv_safe_house);
        for (int i=0; i<listSafeHome.size(); i++) {
            Items itemTitle = new Items();
            itemTitle.setTitle(listSafeHome.get(i).getTitle());
            mAdapter.addSectionHeaderItem(itemTitle);
            List<Items> listItems = listSafeHome.get(i).getItems();
            if (!listItems.isEmpty()) {
                for (int x=0; x<listItems.size(); x++) {
                        Items item = new Items();
                        item.setTitle(listItems.get(x).getTitle());
                        item.setBody(listItems.get(x).getBody());
                        mAdapter.addItem(item);
                 }
            }

        }
        lv_safe_house.setAdapter(mAdapter);
   /*     for (int i=0; i<listSafeHome.size(); i++) {
           // настраиваем список
        try {
            ListView lv_safe_house = (ListView) findViewById(R.id.lv_safe_house);
            List<Items> item = orderCard.getData().getSafeHome().get(i).getItems();
            SafeHomeAdapter safeHomeAdapter = new SafeHomeAdapter(this, item);
            // настраиваем список

            View header = getLayoutInflater().inflate(R.layout.header_row_item_safe_house, null);
            TextView hdr = (TextView) header.findViewById(R.id.header_safe_house);
            lv_safe_house = (ListView) findViewById(R.id.lv_safe_house);
            hdr.setText(orderCard.getData().getSafeHome().get(i).getTitle());
            lv_safe_house.addHeaderView(header);
            lv_safe_house.setAdapter(safeHomeAdapter);
        } catch (NullPointerException ex) {}*/


    }

 /*          ListView lv_safe_house = (ListView) findViewById(R.id.lv_safe_house);
           View header = getLayoutInflater().inflate(R.layout.header_row_item_safe_house, null);
           TextView hdr = (TextView) header.findViewById(R.id.header_safe_house);
           lv_safe_house.addHeaderView(header);
           safeHomeAdapter = new SafeHomeAdapter(this, listSafeHome.get(0).getItems());*/
    //   }
         @Override
         public boolean onOptionsItemSelected(MenuItem item) {
             switch (item.getItemId()) {
                 case android.R.id.home:
                     this.finish();
                     return true;
                 default:
                     return super.onOptionsItemSelected(item);
             }
         }

}

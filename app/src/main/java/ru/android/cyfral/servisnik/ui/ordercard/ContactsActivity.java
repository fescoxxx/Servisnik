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
import ru.android.cyfral.servisnik.model.orderCard.Contacts;
import ru.android.cyfral.servisnik.model.orderCard.OrderCard;
import ru.android.cyfral.servisnik.model.orderCard.contacts.ContactsAdapter;

public class ContactsActivity extends AppCompatActivity {

    private List<Contacts> listContacts;
    private ContactsAdapter contactsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar =getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("Контакты");
        Intent intent = getIntent();
        OrderCard orderCard = (OrderCard) intent.getExtras().getSerializable("ordercard");

        listContacts = orderCard.getData().getContacts();
        contactsAdapter = new ContactsAdapter(this, listContacts);
        // настраиваем список
        ListView lv_contacts = (ListView) findViewById(R.id.lv_contacts);
        lv_contacts.setAdapter(contactsAdapter);
    }

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

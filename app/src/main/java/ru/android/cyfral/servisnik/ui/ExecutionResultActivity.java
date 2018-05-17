package ru.android.cyfral.servisnik.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import retrofit2.Call;
import ru.android.cyfral.servisnik.R;
import ru.android.cyfral.servisnik.model.Constants;
import ru.android.cyfral.servisnik.model.OrderCard.OrderCard;
import ru.android.cyfral.servisnik.model.result.getResult.GetResult;
import ru.android.cyfral.servisnik.remote.RetrofitClientServiseApi;
import ru.android.cyfral.servisnik.remote.RetrofitClientToken;
import ru.android.cyfral.servisnik.service.ServiceApiClient;
import ru.android.cyfral.servisnik.service.TokenClient;

public class ExecutionResultActivity extends AppCompatActivity {

    private LinearLayout mLinearLayout;
    private ProgressBar mProgressBar;
    private static Call<GetResult> getResultCall;
    private String guid;

    TokenClient tokenClient = RetrofitClientToken
            .getClient(Constants.HTTP.BASE_URL_TOKEN)
            .create(TokenClient.class);

    ServiceApiClient serviceApiClient = RetrofitClientServiseApi
            .getClient(Constants.HTTP.BASE_URL_REQUEST)
            .create(ServiceApiClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_execution_result);
        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayout_execution_result);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_execution_result);

        mProgressBar.setVisibility(View.VISIBLE);
        mLinearLayout.setVisibility(View.INVISIBLE);
        Intent intent = getIntent();
        OrderCard orderCard = (OrderCard) intent.getExtras().getSerializable("ordercard");
        guid = orderCard.getData().getId();
        getResult(guid);
    }

    private void getResult(String guid) {
        mProgressBar.setVisibility(View.INVISIBLE);
        mLinearLayout.setVisibility(View.VISIBLE);
        SharedPreferences sPref = getSharedPreferences(Constants.SETTINGS.MY_PREFS, MODE_PRIVATE);
        String token = sPref.getString(Constants.SETTINGS.TOKEN, "");
        serviceApiClient.getOrderCard(guid, "Bearer " + token);
        Toast toast = Toast.makeText(getApplicationContext(),
                guid, Toast.LENGTH_SHORT);
        toast.show();
    }
}

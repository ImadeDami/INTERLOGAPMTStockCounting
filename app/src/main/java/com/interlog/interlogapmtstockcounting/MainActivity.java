package com.interlog.interlogapmtstockcounting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.interlog.interlogapmtstockcounting.editor.MainEActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    EditText qty;
    TextView randomNos;
    Button viewEntries, submitBtn;
    ListView listView, listView2;
    AutoCompleteTextView autoVw, racLoc;
    DataBaseHelper dataBaseHelper;
    TextView textViewId, textViewUsername, textViewEmail, textViewGender;

    BroadcastReceiver broadcastReceiver;

    public static final int SYNC_STATUS_OK = 1;
    public static final int SYNC_STATUS_FAILED = 0;
    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "net.simplifiedcoding.datasaved";
    public static final String UI_UPDATE_BROADCAST = "com.interlog.ilstockinventory.uiupdatebroadcast";
    public static final String URL_SAVE_NAME = "http://interlog-ng.com/interlogmobile/consumables.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //if the user is not logged in
        //starting the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        textViewId = findViewById(R.id.textViewId);
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewGender = findViewById(R.id.textViewGender);

        viewEntries = findViewById(R.id.viewEntries);

        dataBaseHelper = new DataBaseHelper(this);

        //registering the broadcast receiver to update sync status
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));
        registerReceiver(new NetworkChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        dataBaseHelper.getData();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                dataBaseHelper.getData();
            }
        };

        final int random = new Random().nextInt(10000) + 299999;
        randomNos = findViewById(R.id.randomNos);
        randomNos.setText(Integer.toString(random));

        qty = findViewById(R.id.qty);
        racLoc = findViewById(R.id.racLoc);
        listView = findViewById(R.id.listView);
       // listView2 = findViewById(R.id.listView2);
        autoVw = findViewById(R.id.autoVw);
        submitBtn = findViewById(R.id.submitBtn);

        dataSubmit();

        viewEntries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (MainActivity.this, MainEActivity.class);
                startActivity(intent);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);

        Call<List<Items>> call = api.getItems();

        call.enqueue(new Callback<List<Items>>() {
            @Override
            public void onResponse(Call<List<Items>> call, Response<List<Items>> response) {

                List<Items> items = response.body();

                String[] itemNams = new String[items.size()];
                for (int i = 0; i < items.size(); i++) {
                    itemNams[i] = items.get(i).getItemName();
                }
                autoVw.setAdapter(
                        new ArrayAdapter<String>(
                                getApplicationContext(),
                                android.R.layout.simple_expandable_list_item_1,
                                itemNams
                        )
                );

            }

            @Override
            public void onFailure(Call<List<Items>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT);
            }
        });


        // item name auto complete

        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api api2 = retrofit2.create(Api.class);

        Call<List<Items>> call2 = api2.getItemNo();

        call2.enqueue(new Callback<List<Items>>() {
            @Override
            public void onResponse(Call<List<Items>> call2, Response<List<Items>> response) {

                List<Items> items2 = response.body();

                String[] itemNo = new String[items2.size()];
                for (int i = 0; i < items2.size(); i++) {
                    itemNo[i] = items2.get(i).getRackLocation();
                }
                racLoc.setAdapter(
                        new ArrayAdapter<String>(
                                getApplicationContext(),
                                android.R.layout.simple_expandable_list_item_1,
                                itemNo
                        )
                );

            }

            @Override
            public void onFailure(Call<List<Items>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT);
            }
        });




        //getting the current user
        User user = SharedPrefManager.getInstance(this).getUser();

        //setting the values to the textviews
        textViewId.setText(String.valueOf(user.getId()));
        textViewUsername.setText(user.getUsername());
        textViewEmail.setText(user.getEmail());
        textViewGender.setText(user.getGender());

        //when the user presses logout button
        //calling the logout method
        findViewById(R.id.buttonLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                SharedPrefManager.getInstance(getApplicationContext()).logout();
            }
        });

    }

    private void dataSubmit() {
        final String randomNum = randomNos.getText().toString();
        final String item = autoVw.getText().toString();
        final String quanty = qty.getText().toString();
        final String racLocat = racLoc.getText().toString();
        final String userid = textViewId.getText().toString();

        /*if (item.isEmpty()) {
            autoVw.setError("enter part desc");
            autoVw.requestFocus();
            return;
        } */
        if (quanty.isEmpty()) {
            qty.setError("enter quantity of items");
            qty.requestFocus();
            return;
        }
        if (racLocat.isEmpty()) {
            racLoc.setError("enter part number");
            racLoc.requestFocus();
            return;
        }

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getIM()
                .submitResponse(userid,randomNum, item, quanty, racLocat);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject obj = new JSONObject(String.valueOf(response));
                    if (!obj.getBoolean("error")) {
                        //if there is a success
                        //storing the name to sqlite with status synced
                        dataBaseHelper.addData(userid, randomNum, item, quanty, racLocat, SYNC_STATUS_OK);
                    } else {
                        //if there is some error
                        //saving the name to sqlite with status unsynced
                        dataBaseHelper.addData(userid, randomNum, item, quanty, racLocat, SYNC_STATUS_FAILED);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    // String s = response.body().toString();
                    Toast.makeText(MainActivity.this, "Submitted...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dataBaseHelper.addData(userid, randomNum, item, quanty, racLocat, SYNC_STATUS_FAILED);
                //Toast.makeText(SurveyActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(MainActivity.this, "data has been saved on phone and will submitted once there is internet connection", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitBtn:
                dataSubmit();
                break;
        }
    }

}

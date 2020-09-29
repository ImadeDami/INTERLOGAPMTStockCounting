package com.interlog.interlogapmtstockcounting;

import android.content.Intent;
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
    TextView randomNos, viewEntries;
    Button submitBtn;
    ListView listView;
    AutoCompleteTextView autoVw;
    DataBaseHelper dataBaseHelper;
    TextView textViewId, textViewUsername, textViewEmail, textViewGender;

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

        final int random = new Random().nextInt(10000) + 299999;
        randomNos = findViewById(R.id.randomNos);
        randomNos.setText(Integer.toString(random));

        qty = findViewById(R.id.qty);
        listView = findViewById(R.id.listView);
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
        final String userid = textViewId.getText().toString();

        if (item.isEmpty()) {
            autoVw.setError("enter item name");
            autoVw.requestFocus();
            return;
        }
        if (quanty.isEmpty()) {
            qty.setError("enter quantity of items");
            qty.requestFocus();
            return;
        }

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getIM()
                .submitResponse(userid,randomNum, item, quanty);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject obj = new JSONObject(String.valueOf(response));
                    if (!obj.getBoolean("error")) {
                        //if there is a success
                        //storing the name to sqlite with status synced
                        dataBaseHelper.addData(userid, randomNum, item, quanty);
                    } else {
                        //if there is some error
                        //saving the name to sqlite with status unsynced
                        dataBaseHelper.addData(userid, randomNum, item, quanty);
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
                dataBaseHelper.addData(userid, randomNum, item, quanty);
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

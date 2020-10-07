package com.interlog.interlogapmtstockcounting.editor;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.interlog.interlogapmtstockcounting.LoginActivity;
import com.interlog.interlogapmtstockcounting.R;
import com.interlog.interlogapmtstockcounting.SharedPrefManager;
import com.interlog.interlogapmtstockcounting.User;

import java.util.List;

public class MainEActivity extends AppCompatActivity implements MainView {

    private static final int INTENT_EDIT = 200;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefresh;

    MainPresenter presenter;
    MainAdapter adapter;
    MainAdapter.ItemClickListener itemClickListener;

    List<Note> note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maine);

        // call user id
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        TextView textViewId = findViewById(R.id.textViewId);
        //getting the current user
        User user = SharedPrefManager.getInstance(this).getUser();
        int usrId = user.getId();
        //setting the values to the textviews
        textViewId.setText(String.valueOf(user.getId()));
          /*String userID = textViewId.getText().toString();

            Call<ResponseBody> call = ApiClient
                    .getInstance()
                    .getNaSurvey()
                    .getNotes(userID);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            }); */

        swipeRefresh = findViewById(R.id.swipe_refresh);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager((this)));

        presenter = new MainPresenter(this);
        presenter.getData(usrId);
        System.out.println("userId: " + user.getId());
        swipeRefresh.setOnRefreshListener(
                () -> presenter.getData(usrId) //presenter.getData();
        );

        itemClickListener = ((view, position) -> {

            int id = note.get(position).getId();
            int userID = note.get(position).getUserID(); // new
            String itemName = note.get(position).getItemName();
            String quantity = note.get(position).getQuantity();
            String rackLocation = note.get(position).getRackLocation();
           // String color = note.get(position).getNote(); // example used int
            Toast.makeText(this, itemName, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, EditorActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("userID", userID); //new
            intent.putExtra("itemName", itemName);
            intent.putExtra("quantity", quantity);
            intent.putExtra("rackLocation", rackLocation);
            //intent.putExtra("color", color);
            startActivityForResult(intent, INTENT_EDIT);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        User user = SharedPrefManager.getInstance(this).getUser();

        int usrId = user.getId();
        if(requestCode == INTENT_EDIT && resultCode == RESULT_OK) {
            presenter.getData(usrId);
        }
    }

    @Override
    public void showLoading() {
        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void onGetResult(List<Note> notes) {
        adapter = new MainAdapter(this, notes, itemClickListener);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        note = notes;
    }

    @Override
    public void onErrorLoading(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

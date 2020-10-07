package com.interlog.interlogapmtstockcounting.editor;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPresenter {

    private MainView view;

    public MainPresenter(MainView view) {
        this.view = view;
    }

/**if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }
    //textViewId = findViewById(R.id.textViewId);
    //getting the current user
    User userID = SharedPrefManager.getInstance(this).getUser();
    //setting the values to the textviews
     //   textViewId.setText(String.valueOf(user.getId())); **/

    void getData(int usrId){
        view.showLoading();

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
       // ApiInterface apiInterface = ApiClient.getInstance().getNaSurvey();
        Map<String , String> data = new HashMap<>();
        data.put("userID",usrId+"");
        // Call<List<Note>> call = apiInterface.getNotes(data);
        Call<List<Note>> call = apiInterface.getNotes(usrId+"");
        // Call<List<Note>> call = ApiClient.getInstance().getNaSurvey().getNotes(userID);

        call.enqueue(new Callback<List<Note>>() {

            @Override
            public void onResponse(@NonNull Call<List<Note>> call, @NonNull Response<List<Note>> response) {
                System.out.println("call returned: " + response.toString());

                view.hideLoading();
                if(response.isSuccessful()){
                    System.out.println("response is successful: " + response.isSuccessful()+"");
                    System.out.println("response is message: " + response.message()+"");
                   if (response.body() != null){
                       System.out.println("response body: " + response.body()+"");

                       view.onGetResult(response.body());
                   }
                }
                else{
                    Log.d("infact: ","Call insuccessful");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Note>> call, @NonNull Throwable t) {
              System.out.println("Localised msg: " + t.getLocalizedMessage()+"");
                System.out.println("Cause: " + t.getCause()+"");
                System.out.println("Message: " + t.getMessage()+"");

                view.hideLoading();
                view.onErrorLoading(t.getLocalizedMessage());
            }
        });
    }

}

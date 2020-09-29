package com.interlog.interlogapmtstockcounting.editor;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://interlog-ng.com/";
    private static Retrofit retrofit;
    private static ApiClient mInstance; // new

    public static Retrofit getApiClient() {

        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

   public static synchronized ApiClient getInstance(){ //new down
        if(mInstance == null){
            mInstance = new ApiClient();
        }
        return mInstance;
    }

    public ApiInterface getNaSurvey() {
        return  retrofit.create(ApiInterface.class);
    }
}

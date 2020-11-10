package com.interlog.interlogapmtstockcounting;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Api {
    String BASE_URL = "http://interlog-ng.com/";

    @GET("interlogmobile/viewitems.php")
    Call<List<Items>> getItems();

    @GET("interlogmobile/viewpartno.php")
    Call<List<Items>> getItemNo();





    @FormUrlEncoded
    @POST("interlogmobile/consumables.php")
    Call<ResponseBody> submitResponse(
            @Field("userID") String userID,
            @Field("randomNumber") String randomNumber,
            @Field("itemName") String itemName,
            @Field("quantity") String quantity,
            @Field("rackLocation") String rackLocation);
}

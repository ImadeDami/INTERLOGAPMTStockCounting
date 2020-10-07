package com.interlog.interlogapmtstockcounting.editor;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {


    @FormUrlEncoded
    @POST("interlogmobile/savecon.php")
    Call<Note> saveNote(
            @Field("itemName") String itemName,
            @Field("quantity") String quantity,
            @Field("rackLocation") String rackLocation
    );

    @FormUrlEncoded
    @POST("interlogmobile/getcon.php" )
    Call<List<Note>> getNotes(
            //@QueryMap Map<String, String> options
            @Field("userID") String usrId
    );

    @FormUrlEncoded
    @POST("interlogmobile/updatecon.php")
    Call<Note> updateNote(
            @Field("id") int id,
            @Field("itemName") String itemName,
            @Field("quantity") String quantity,
            @Field("rackLocation") String rackLocation
    );

    @FormUrlEncoded
    @POST("interlogmobile/deletecon.php")
    Call<Note> deleteNote(@Field("id") int id
    );
}

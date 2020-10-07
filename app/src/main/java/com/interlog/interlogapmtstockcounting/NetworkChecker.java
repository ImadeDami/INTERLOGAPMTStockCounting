package com.interlog.interlogapmtstockcounting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.interlog.interlogapmtstockcounting.DataBaseHelper.TAG;

public class NetworkChecker extends BroadcastReceiver {
    private Context context;
    private DataBaseHelper db;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        db = new DataBaseHelper(context);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                //getting all the unsynced names
                Cursor cursor = db.getUnsyncedNames();
                if (cursor.moveToFirst()) {
                    do {
                        //calling the method to save the unsynced name to MySQL
                        userSignUp(
                                cursor.getInt(cursor.getColumnIndex(DataBaseHelper.COL1)),
                                cursor.getString(cursor.getColumnIndex(DataBaseHelper.COL2)),
                                cursor.getString(cursor.getColumnIndex(DataBaseHelper.COL3)),
                                cursor.getString(cursor.getColumnIndex(DataBaseHelper.COL4)),
                                cursor.getString(cursor.getColumnIndex(DataBaseHelper.COL5)),
                                cursor.getString(cursor.getColumnIndex(DataBaseHelper.COL6))
                        );
                    } while (cursor.moveToNext());
                }
                Log.d(TAG, "add data: adding ");
            }
        }
    }

    private void userSignUp(final int id, final String userid, final String randomNum, final String item, final String quanty, final String racLocat){
        /** do user registration using api call **/
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getIM()
                .submitResponse(userid, randomNum, item, quanty, racLocat);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject obj = new JSONObject(String.valueOf(response));
                    if (!obj.getBoolean("error")) {
                        //updating the status in sqlite
                        db.updateNameStatus(id, MainActivity.SYNC_STATUS_OK);

                        //sending the broadcast to refresh the list
                        context.sendBroadcast(new Intent(MainActivity.DATA_SAVED_BROADCAST));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Map<String, String> params = new HashMap<>();
                params.put("userid", userid);
                params.put("randomNum", randomNum);
                params.put("item", item);
                params.put("quanty", quanty);
                params.put("racLocat", racLocat);
                return;

            }
        });

    }
}
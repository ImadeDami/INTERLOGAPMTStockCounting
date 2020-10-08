package com.interlog.interlogapmtstockcounting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String TAG = "DataBaseHelper";
    public static final String TABLE_NAME = "interapmt2";
    public static final String COL1 = "id";
    public static final String COL2 = "USERID";
    public static final String COL3 = "RANDOMNUMBER";
    public static final String COL4 = "ITEMNAME";
    public static final String COL5 = "QUANTITY";
    public static final String COL6 = "RACKLOCATION";

    public static final String SYNC_STATUS = "syncstatus";

    public DataBaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "USERID TEXT, RANDOMNUMBER TEXT, ITEMNAME TEXT, QUANTITY TEXT, RACKLOCATION TEXT, syncstatus integer)";
        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    public boolean addData(String userid, String randomNum, String item, String quanty, String racLocat, int sync_status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, userid);
        contentValues.put(COL3, randomNum);
        contentValues.put(COL4, item);
        contentValues.put(COL5, quanty);
        contentValues.put(COL6, racLocat);
        contentValues.put(SYNC_STATUS, sync_status);
        long result = db.insert(TABLE_NAME, null, contentValues);

        //if data is inserted correctly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);

        return data;
    }

    public boolean updateNameStatus(int id, int sync_status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SYNC_STATUS, sync_status);
        db.update(TABLE_NAME, contentValues, COL1 + "=" + id, null);
        db.close();
        return true;
    }

    /*
     * this method is for getting all the unsynced name
     * so that we can sync it with database
     * */
    public Cursor getUnsyncedNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + SYNC_STATUS + " = 0;";
        Cursor c = db.rawQuery(sql, null);
        return c;

    }

}
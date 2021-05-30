package com.sunmoon.withtalk;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "chat.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    //데이터베이스가 생성될 때 호출됨
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE CHAT (id integer, senderId text);");//테이블 생성
        //Cursor cursor = db.rawQuery("select id, senderId from chatroom", null);
        //sqlDB = dbHelper.getWritableDatabase();
        //sqlDB.execSQL("INSERT INTO CHAT VALUES (1, "CHO"));
        //sqlDB.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        println("onUpgrade 호출됨: " + oldVersion + " => " + newVersion);

        if (newVersion > 1) {
            db.execSQL("DROP TABLE IF EXISTS emp");
        }
    }

    public void println(String data) {
        Log.d("DatabaseHelper", data);
    }
}

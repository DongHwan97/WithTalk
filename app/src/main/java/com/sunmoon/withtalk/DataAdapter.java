package com.sunmoon.withtalk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataAdapter {
    protected static final String TAG = "DataAdapter";

    // TODO : TABLE 이름을 명시해야함
    protected static final String TABLE_NAME = "friend";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;

    public DataAdapter(Context context) {
        this.mContext = context;
        mDbHelper = new DataBaseHelper(mContext);
    }

    public DataAdapter createDatabase() {
        try {
            mDbHelper.createDataBase();
        } catch (IOException mIOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }

        return this;
    }

    public DataAdapter open() {
        mDbHelper.openDataBase();
        mDbHelper.close();
        mDb = mDbHelper.getReadableDatabase();

        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public List getTableData() {
        // Table 이름 -> antpool_bitcoin 불러오기
        String sql ="SELECT * FROM " + TABLE_NAME;

        // 모델 넣을 리스트 생성
        List userList = new ArrayList<Friend>();

        // TODO : 모델 선언
        Friend friend = null;

        Cursor mCur = mDb.rawQuery(sql, null);
        if (mCur!=null) {
            // 칼럼의 마지막까지
            while( mCur.moveToNext()) {
                // TODO : 커스텀 모델 생성
                friend = new Friend();

                // TODO : Record 기술
                // id, name, account, privateKey, secretKey, Comment
                friend.name =mCur.getString(0);
                friend.id = mCur.getString(1);

                // 리스트에 넣기
                userList.add(friend);
            }

        }

        return userList;
    }

    public void insertFriend(Friend friend) {
        String name = friend.name;
        String id = friend.id;

        String sql = "INSERT INTO Friend VALUES ('" + name + "','" + id + "');";

        try {
            mDb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Friend> flist = getTableData();
        for ( Friend f : flist ) {
            Log.d("내부디비친구목록", "아이디 : " + f.id + " 이름 : " + f.name);
        }
    }
}

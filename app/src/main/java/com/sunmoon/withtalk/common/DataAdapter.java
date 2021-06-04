package com.sunmoon.withtalk.common;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataAdapter {
    protected static final String TAG = "DataAdapter";

    // TODO : TABLE 이름을 명시해야함
    protected static final String FRIEND_TABLE = "FRIEND";
    protected static final String CHATROOM_TABLE = "CHATROOM";

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

    public List selectAllFriend() {
        String sql = "SELECT * FROM " + FRIEND_TABLE + ";";

        List userList = new ArrayList<Friend>();
        Friend friend = null;

        Cursor mCur = mDb.rawQuery(sql, null);
        if (mCur != null) {
            // 칼럼의 마지막까지
            while( mCur.moveToNext()) {
                friend = new Friend();

                // TODO : Record 기술
                friend.id = mCur.getString(0);
                friend.name =mCur.getString(1);

                // 리스트에 넣기
                userList.add(friend);
            }
        }

        return userList;
    }

    public List searchRegistFriend(String name) {
        //내부DB에서 이름으로 친구 검색
        String sql = "SELECT * FROM " + FRIEND_TABLE + " WHERE NAME = '" + name + "';";

        List userList = new ArrayList<Friend>();
        Friend friend = null;
        Cursor mCur = mDb.rawQuery(sql, null);
        if (mCur != null) {
            while(mCur.moveToNext()) {
                friend = new Friend();

                friend.id = mCur.getString(0);
                friend.name = mCur.getString(1);

                userList.add(friend);
            }
        }

        return userList;
    }

    public void insertFriend(Friend friend) {
        String id = friend.id;
        String name = friend.name;

        String sql = "INSERT INTO " + FRIEND_TABLE + " VALUES ('" + id + "','" + name + "');";

        try {
            mDb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteFriend(String id) {
        //내부DB에서 친구 삭제
        String sql = "DELETE FROM " + FRIEND_TABLE + " WHERE ID = '" + id + "';";

        Log.d("DELETE FRIEND", sql);
        try {
            mDb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteAllFriend() {
        String sql = "DELETE FROM " + FRIEND_TABLE + ";";

        try {
            mDb.execSQL(sql);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public List selectAllChatroom() {
        String sql ="SELECT * FROM " + CHATROOM_TABLE;

        List<Chatroom> chatroomList = new ArrayList<Chatroom>();
        Chatroom chatroom = null;

        Cursor mCur = mDb.rawQuery(sql, null);
        if (mCur != null) {
            while (mCur.moveToNext()) {
                chatroom = new Chatroom();

                chatroom.no = mCur.getInt(0);
                chatroom.name = mCur.getString(1);
                chatroom.memberCount = mCur.getInt(2);
                chatroom.type = mCur.getString(3);

                chatroomList.add(chatroom);
            }
        }

        return chatroomList;
    }

    public List searchRegistChatroom(String name) {
        //내부DB에서 이름으로 대화방 검색
        String sql = "SELECT * FROM " + CHATROOM_TABLE + " WHERE NAME = '" + name + "';";

        List chatroomList = new ArrayList<Chatroom>();
        Chatroom chatroom = null;

        Cursor mCur = mDb.rawQuery(sql, null);
        if (mCur != null) {
            while(mCur.moveToNext()) {
                chatroom = new Chatroom();

                chatroom.no = mCur.getInt(0);
                chatroom.name = mCur.getString(1);
                chatroom.memberCount = mCur.getInt(2);
                chatroom.type = mCur.getString(3);

                chatroomList.add(chatroom);
            }
        }

        return chatroomList;
    }

    public void insertChatroom(Chatroom chatroom) {
        int no = chatroom.no;
        String name = chatroom.name;
        int memberCount = chatroom.memberCount;
        String type = chatroom.type;

        String sql = "INSERT INTO " + CHATROOM_TABLE + " VALUES ('" + no + "','" + name + "','" + memberCount + "','" + type + "');";

        try {
            mDb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteChatroom(int no) {
        String sql = "DELETE FROM " + CHATROOM_TABLE + " WHERE NO = " + no + ";";

        try {
            mDb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteAllChatroom() {
        String sql = "DELETE FROM " + CHATROOM_TABLE + ";";

        try {
            mDb.execSQL(sql);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkExistChatroom(int no) {
        String sql = "SELECT COUNT(*) FROM " + CHATROOM_TABLE + " WHERE NO = " + no + ";";

        Boolean isExist = null;


        return isExist;
    }

    public void updateNameChatroom(int no, String name) {

    }
}

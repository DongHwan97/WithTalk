package com.sunmoon.withtalk.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataAdapter {
    protected static final String TAG = "DataAdapter";

    protected static final String MESSAGE_TABLE = "MESSAGE_DB";

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

    public void insertMessage(MessageDB messageDB) {//내부 디비 삽입
        int seqNo = messageDB.getSeqNo();
        String senderId = messageDB.getSenderId();
        String contents = messageDB.getContents();
        String sendTime = messageDB.getSendTime();
        int chatRoomNo = messageDB.getChatRoomNo();
        String isRead = messageDB.getIsRead();

        String sql = "INSERT INTO " + MESSAGE_TABLE + " VALUES ('" + seqNo + "','" + senderId + "','" + contents + "','"
                + sendTime + "','" + chatRoomNo + "','" + isRead + "');";

        Log.e("insertMessage", sql);
        try {
            mDb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public List selectOneChatRoomMessages(String chatRoomNo) {// 채팅방 들어가서 내부 디비 조회
        String sql = "SELECT * FROM " + MESSAGE_TABLE + " WHERE CHATROOM_NO = " + chatRoomNo + ";";

        List messageList = new ArrayList<MessageDB>();
        MessageDB messageDB = null;

        Cursor mCur = mDb.rawQuery(sql, null);
        if (mCur != null) {
            while(mCur.moveToNext()) {
                messageDB = new MessageDB();

                messageDB.setSeqNo(mCur.getInt(0));
                messageDB.setSenderId(mCur.getString(1));
                messageDB.setContents(mCur.getString(2));
                messageDB.setSendTime(mCur.getString(3));
                messageDB.setChatRoomNo(mCur.getInt(4));
                messageDB.setIsRead(mCur.getString(5));

                messageList.add(messageDB);
            }
        }

        return messageList;
    }
    public List<MessageDB> selectUnReadMessage() {// 안 읽은 메시지 찾기
        String sql = "SELECT * FROM " + MESSAGE_TABLE + " WHERE IS_READ = 'false' ORDER BY CHATROOM_NO;";

        List<MessageDB> messageList = new ArrayList();
        MessageDB messageDB = null;

        Cursor mCur = mDb.rawQuery(sql, null);
        if (mCur != null) {
            while(mCur.moveToNext()) {
                messageDB = new MessageDB();

                messageDB.setSeqNo(mCur.getInt(0));
                messageDB.setSenderId(mCur.getString(1));
                messageDB.setContents(mCur.getString(2));
                messageDB.setSendTime(mCur.getString(3));
                messageDB.setChatRoomNo(mCur.getInt(4));
                messageDB.setIsRead(mCur.getString(5));

                messageList.add(messageDB);
            }
        }

        return messageList;
    }


    public void updateReadMessage (String chatRoomNo) {// 읽은 메시지 처리
        String sql = "UPDATE " + MESSAGE_TABLE + " SET IS_READ = 'true' WHERE IS_READ = 'false' AND CHATROOM_NO = " + chatRoomNo + ";";

        Log.e("insertMessage", sql);
        try {
            mDb.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

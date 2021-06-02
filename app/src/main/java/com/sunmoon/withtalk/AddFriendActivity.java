package com.sunmoon.withtalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class AddFriendActivity extends AppCompatActivity {

    ImageButton searchAddFriendButton;
    EditText searchAddFriendEdit;
    LinearLayout inflateLayout;
    View listLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        inflateLayout = findViewById(R.id.searchAddFriendLayout);

        searchAddFriendButton = findViewById(R.id.searchAddFriendButton);
        searchAddFriendEdit = findViewById(R.id.searchAddFriendEdit);

        searchAddFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String friendPhoneNo = searchAddFriendEdit.getText().toString();//검색 데이터 전송 받고
                if ((friendPhoneNo.length() > 10)) {
                    sendToSearchFriend(friendPhoneNo);
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    receiveFromSearchFriend();
                } else {
                    Util.startToast(getApplicationContext(), "올바른 연락처를 입력해주세요.");
                }
            }
        });
    }

    public void sendToSearchFriend(String friendPhoneNo) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"" + "friend" + "\",");
        sb.append("\"method\":\"" + "searchFriend" + "\",");
        sb.append("\"phoneNo\":\"" + friendPhoneNo + "\"");
        sb.append("}");

        ConnectSocket.sendQueue.offer((sb.toString()));
    }

    public void receiveFromSearchFriend() {
        ArrayList<String> lists = JsonHandler.messageReceived();

        String status = lists.get(0);
        String id = lists.get(1);
        String name = lists.get(2);

        if ("r200".equals(status)) {
            inflateLayout.removeView(listLayout);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listLayout = inflater.inflate(R.layout.friendlistlayout, inflateLayout, false);
            TextView friendNameText = (TextView) listLayout.findViewById(R.id.friendNameText);
            ImageButton friendAddButton = (ImageButton) listLayout.findViewById(R.id.friendAddButton);
            friendAddButton.setVisibility(View.VISIBLE);
            friendNameText.setText(name);
            inflateLayout.addView(listLayout);

            String friendId = id;
            friendAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//친구추가
                    sendToInsertFriend(friendId);
                    try {
                        Thread.sleep(300);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    receiveFromInsertFriend(name, friendId);
                }
            });
        } else {
            Util.startToast(this, "해당 유저가 존재하지 않습니다.");
        }
    }

    public void sendToInsertFriend(String friendId) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("\"type\":\"" + "friend" + "\",");
        builder.append("\"method\":\"" + "insertFriend" + "\",");
        builder.append("\"memberId\":\"" + MainActivity.id + "\",");
        builder.append("\"friendId\":\"" + friendId + "\",");
        builder.append("}");

        ConnectSocket.sendQueue.offer((builder.toString()));
    }

    public void receiveFromInsertFriend(String name, String friendId) {
        ArrayList<String> lists = JsonHandler.messageReceived();
        String status = lists.get(0);

        if ("r200".equals(status)) {

            Friend friend = new Friend(friendId, name);


            DataAdapter mDbHelper = new DataAdapter(getApplicationContext());
            mDbHelper.createDatabase();
            mDbHelper.open();

            mDbHelper.insertFriend(friend);

            // db 닫기
            mDbHelper.close();
            /*
            DataBaseHelper myDB = new DataBaseHelper(this);
            myDB.open();
            myDB.create();
            myDB.insertColumn(name, friendId);
            */
            Util.startToast(getApplicationContext(), "친구추가 되었습니다.");
        } else {
            Util.startToast(getApplicationContext(), "친구추가 실패.");
        }
    }
}
package com.sunmoon.withtalk.friend;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunmoon.withtalk.chatroom.ChatActivity;
import com.sunmoon.withtalk.common.ConnectSocket;
import com.sunmoon.withtalk.common.MainActivity;
import com.sunmoon.withtalk.R;
import com.sunmoon.withtalk.common.Util;

import java.util.ArrayList;

public class SearchFriendActivity extends AppCompatActivity {

    ImageButton searchFriendButton;
    EditText searchFriendEdit;
    LinearLayout inflateLayout;
    View listLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        inflateLayout = findViewById(R.id.searchFriendLayout);

        searchFriendButton = findViewById(R.id.searchFriendButton);
        searchFriendEdit = findViewById(R.id.searchFriendEdit);

        String senderId = MainActivity.id;

        searchFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateLayout.removeAllViews();
                String friendName = searchFriendEdit.getText().toString();//검색 데이터 전송 받고
                if ((friendName.length() > 0)) {
                    sendSearchFriend(senderId, friendName);
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    receiveSearchFriend();
                } else {
                    Util.startToast(getApplicationContext(), "이름을 입력해 주세요");
                }
            }
        });
    }

    public void sendSearchFriend(String senderId, String friendName) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"" + "friend" + "\",");
        sb.append("\"method\":\"" + "searchRegistFriend" + "\",");
        sb.append("\"senderId\":\"" + senderId + "\",");
        sb.append("\"searchName\":\"" + friendName + "\"");
        sb.append("}");

        ConnectSocket.sendQueue.offer((sb.toString()));
    }

    public void receiveSearchFriend() {
        ArrayList<String> lists = ConnectSocket.JsonHandler.messageReceived();

        String status = lists.get(0);

        if ("r200".equals(status)) {
            for (int i = 1; i < lists.size(); i = i + 2) {
                String friendId = lists.get(i);
                String name = lists.get(i + 1);

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                listLayout = inflater.inflate(R.layout.friendlistlayout, inflateLayout, false);
                TextView friendNameText = (TextView) listLayout.findViewById(R.id.friendNameText);
                friendNameText.setText(name);
                inflateLayout.addView(listLayout);
                listLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showDialog(friendId);
                        return true;
                    }
                });
            }
        } else {
            Util.startToast(getApplicationContext(), "해당하는 친구가 존재하지 않습니다.");
        }
    }

    public void showDialog(String friendId) {
        final CharSequence[] items = {"1:1 대화", "친구 삭제"};
        AlertDialog.Builder friendBuilder = new AlertDialog.Builder(this);
        friendBuilder.setTitle("친구 관리");

        friendBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        moveActivity(ChatActivity.class);
                        break;
                    case 1:
                        sendDeleteFriend(friendId);
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        receiveDeleteFriend(friendId);
                        break;
                }
            }
        });

        friendBuilder.show();
    }

    public void sendDeleteFriend(String friendId) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"" + "friend" + "\",");
        sb.append("\"method\":\"" + "delete" + "\",");
        sb.append("\"memberId\":\"" + MainActivity.id + "\",");
        sb.append("\"friendId\":\"" + friendId + "\"");
        sb.append("}");
        ConnectSocket.sendQueue.offer((sb.toString()));
        Log.d("------------", sb.toString());
    }

    public void receiveDeleteFriend(String friendId) {
        ArrayList<String> lists = ConnectSocket.JsonHandler.messageReceived();

        String status = lists.get(0);
        Log.d("++++++++++", status);
        if ("r200".equals(status)) {
            Util.startToast(this, friendId + "가 삭제되었습니다.");
        } else {
            Util.startToast(this, "실패했습니다.");
        }
    }

    private void moveActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}
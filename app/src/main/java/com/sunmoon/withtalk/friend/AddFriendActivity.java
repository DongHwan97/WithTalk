package com.sunmoon.withtalk.friend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunmoon.withtalk.R;
import com.sunmoon.withtalk.common.ConnectSocket;
import com.sunmoon.withtalk.common.MainActivity;
import com.sunmoon.withtalk.common.Util;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class AddFriendActivity extends AppCompatActivity {

    ImageButton searchAddFriendButton;
    EditText searchAddFriendEdit;
    LinearLayout inflateLayout;
    View listLayout;

    Context mContext;

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
                String friendPhoneNo = searchAddFriendEdit.getText().toString();
                if ((friendPhoneNo.length() > 10)) {
                    sendToSearchFriend(friendPhoneNo);
                } else {
                    Util.startToast(getApplicationContext(), "올바른 연락처를 입력해주세요.");
                }
            }
        });

        this.mContext = getApplicationContext();
        messaging.start();
    }

    Thread messaging = new Thread(new Runnable() {
        @Override
        public void run() {
            String receivedMessage;

            try {while(!Thread.currentThread().isInterrupted()) {
                receivedMessage = ConnectSocket.receiveQueue.peek();

                Thread.sleep(100);
                if (receivedMessage != null) {
                    JSONObject jsonObject = new JSONObject(receivedMessage);

                    if (jsonObject.getString("method").equals("searchFriend")) {
                        ConnectSocket.receiveQueue.poll();
                        if (jsonObject.getString("status").equals("r200")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String friendId = jsonObject.getString("id");
                                        String friendName = jsonObject.getString("name");

                                        inflateLayout.removeView(listLayout);
                                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        listLayout = inflater.inflate(R.layout.friendlistlayout, inflateLayout, false);
                                        TextView friendNameText = (TextView) listLayout.findViewById(R.id.friendNameText);
                                        ImageButton friendAddButton = (ImageButton) listLayout.findViewById(R.id.friendAddButton);
                                        friendAddButton.setVisibility(View.VISIBLE);
                                        friendNameText.setText(friendName);
                                        inflateLayout.addView(listLayout);

                                        friendAddButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {//친구추가
                                                sendToInsertFriend(friendId);
                                            }
                                        });

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Util.startToast(mContext, "실패했습니다.");
                                }
                            });
                        }
                    }
                    else if (jsonObject.getString("method").equals("insertFriend")) {
                        ConnectSocket.receiveQueue.poll();
                        if (jsonObject.getString("status").equals("r200")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Util.startToast(mContext, "추가되었습니다.");
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Util.startToast(mContext, "실패했습니다.");
                                }
                            });
                        }
                    }
                }
            }} catch (JSONException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    });

    public void sendToSearchFriend(String friendPhoneNo) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"friend\",");
        sb.append("\"method\":\"searchFriend\",");
        sb.append("\"phoneNo\":\"" + friendPhoneNo + "\"");
        sb.append("}");

        ConnectSocket.sendQueue.offer((sb.toString()));
    }

    public void sendToInsertFriend(String friendId) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("\"type\":\"friend\",");
        builder.append("\"method\":\"insertFriend\",");
        builder.append("\"memberId\":\"" + MainActivity.id + "\",");
        builder.append("\"friendId\":\"" + friendId + "\"");
        builder.append("}");

        ConnectSocket.sendQueue.offer((builder.toString()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        messaging.interrupt();
    }
}
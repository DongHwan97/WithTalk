package com.sunmoon.withtalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;


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
                searchFriend();
            }
        });

    }

    public void searchFriend(){
        String friendPhoneNo = searchAddFriendEdit.getText().toString();//검색 데이터 전송 받고
        if ((friendPhoneNo.length() > 10) ) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"type\":\"" + "friend" + "\",");
            sb.append("\"method\":\"" + "search" + "\",");
            sb.append("\"phoneNo\":\"" + friendPhoneNo + "\",");
            sb.append("}");

            ConnectSocket.sendQueue.offer((sb.toString()));
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 결과 받기
            String result = ConnectSocket.receiveQueue.poll();
            try {
                 JSONObject json = new JSONObject(result);

                String method = json.getString("method");
                String status = json.getString("status");
                String id = json.getString("id");
                String name = json.getString("name");
                String phoneNo = json.getString("phoneNo");
                if ("search".equals(method) && "r200".equals(status)) {
                    inflateLayout.removeView(listLayout);
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    listLayout =  inflater.inflate(R.layout.friendlistlayout, inflateLayout, false);
                    TextView friendNameText = (TextView)listLayout.findViewById(R.id.friendNameText);
                    ImageButton friendAddButton = (ImageButton)listLayout.findViewById(R.id.friendAddButton);
                    friendAddButton.setVisibility(View.VISIBLE);
                    friendNameText.setText(name);
                    inflateLayout.addView(listLayout);

                    String friendId = id;
                    friendAddButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {//친구추가

                            StringBuilder builder = new StringBuilder();
                            builder.append("{");
                            builder.append("\"type\":\"" + "friend" + "\",");
                            builder.append("\"method\":\"" + "insertFriend" + "\",");
                            builder.append("\"memberId\":\"" + MainActivity.id + "\",");
                            builder.append("\"friendId\":\"" + friendId + "\",");
                            builder.append("}");

                            ConnectSocket.sendQueue.offer((builder.toString()));
                            try {
                                Thread.sleep(1000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            String insertResult = ConnectSocket.receiveQueue.poll();
                            try {
                                JSONObject jsonObject = new JSONObject(insertResult);
                                String insertMethod = jsonObject.getString("method");
                                String insertStatus = jsonObject.getString("status");
                                if ("insertFriend".equals(insertMethod) && "r200".equals(insertStatus)) {
                                    Util.startToast(getApplicationContext(),"친구추가 되었습니다.");
                                }else{
                                    Util.startToast(getApplicationContext(),"친구추가 실패.");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }});
                }
                else {
                    Util.startToast(this, "해당 유저가 존재하지 않습니다.");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            }else{
            Util.startToast(this, "연락처를 입력해 주세요.");
        }
    }
}
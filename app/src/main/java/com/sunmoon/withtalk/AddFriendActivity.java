package com.sunmoon.withtalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.Image;
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

import org.w3c.dom.Text;

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

    public void searchFriend() {
        String friendPhoneNo = searchAddFriendEdit.getText().toString();//검색 데이터 전송 받고
        if ((friendPhoneNo.length() > 10)) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"type\":\"" + "friend" + "\",");
            sb.append("\"method\":\"" + "searchFriend" + "\",");
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
            JsonParser parser = new JsonParser();
            JsonElement json = parser.parse(result);

            String method = json.getAsJsonObject().get("method").toString();
            String status = json.getAsJsonObject().get("status").toString();
            String id = json.getAsJsonObject().get("id").toString();
            String name = json.getAsJsonObject().get("name").toString();
            String phoneNo = json.getAsJsonObject().get("phoneNo").toString();
            name = name.substring(1, name.length() - 1);

            Log.e("searchFriend: ", "이름:" + name);
            if ("\"searchFriend\"".equals(method) && "\"r200\"".equals(status)) {
                inflateLayout.removeView(listLayout);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                listLayout = inflater.inflate(R.layout.friendlistlayout, inflateLayout, false);
                TextView friendNameText = (TextView) listLayout.findViewById(R.id.friendNameText);
                ImageButton friendAddButton = (ImageButton) listLayout.findViewById(R.id.friendAddButton);
                friendAddButton.setVisibility(View.VISIBLE);
                friendNameText.setText(name);
                inflateLayout.addView(listLayout);
                friendAddButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addFriend();
                    }
                });
            } else {
                Util.startToast(this, "해당 유저가 존재하지 않습니다.");
            }
        } else {
            Util.startToast(this, "연락처를 입력해 주세요.");
        }
    }

    public void addFriend() {
        Util.startToast(this, "친구추가 되었습니다.");
    }

}
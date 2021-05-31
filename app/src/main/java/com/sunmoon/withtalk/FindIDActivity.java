package com.sunmoon.withtalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class FindIDActivity extends AppCompatActivity {
    EditText findIDNameText, findIDPhoneText;
    Button findIDButton;
    TextView resultIDText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);
        findIDNameText = findViewById(R.id.findIDNameText);
        findIDPhoneText = findViewById(R.id.findIDPhoneText);
        findIDButton = findViewById(R.id.findIDButton);
        resultIDText = findViewById(R.id.resultIDText);

        findIDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = findIDNameText.getText().toString();
                String phone = findIDPhoneText.getText().toString();

                if ((name.length() > 1) && (phone.length() > 10)) {
                    sendToServer(name, phone);
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    receiveFromServer();
                } else {
                    Util.startToast(getApplicationContext(),"입력하지 않은 정보가 있습니다.");
                }
            }
        });
    }

    public void sendToServer(String name, String phone) {
        //서버에 보내기
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"member\",");
        sb.append("\"method\":\"findId\",");
        sb.append("\"name\":\"" + name + "\",");
        sb.append("\"phoneNo\":\"" + phone + "\"");
        sb.append("}");

        ConnectSocket.sendQueue.offer(sb.toString());
    }

    public void receiveFromServer() {
        //결과 받기
        List<String> lists = JsonHandler.messageReceived();

        String status = lists.get(0);
        String id = lists.get(1);

        if ("r200".equals(status)) {
            resultIDText.setText("아이디는 " + id + "입니다.");
        } else {
            Util.startToast(this, "회원가입되지 않은 사용자 입니다.");
        }
    }
}
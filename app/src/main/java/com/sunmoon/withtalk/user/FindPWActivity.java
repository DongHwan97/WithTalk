package com.sunmoon.withtalk.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sunmoon.withtalk.R;
import com.sunmoon.withtalk.common.JsonHandler;
import com.sunmoon.withtalk.common.Util;
import com.sunmoon.withtalk.common.ConnectSocket;

import java.util.List;

public class FindPWActivity extends AppCompatActivity {

    EditText findPWIDText, findPWNameText, findPWPhoneText;
    Button findPWButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);
        findPWIDText = findViewById(R.id.findPWIDText);
        findPWNameText = findViewById(R.id.findPWNameText);
        findPWPhoneText = findViewById(R.id.findPWPhoneText);
        findPWButton = findViewById(R.id.findPWButton);

        findPWButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = findPWIDText.getText().toString();
                String name = findPWNameText.getText().toString();
                String phone = findPWPhoneText.getText().toString();

                if ((id.length() > 6) && (name.length() > 1) && (phone.length() > 10)) {
                    sendToServer(id, name, phone);
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    receiveFromServer();
                } else {
                    Util.startToast(getApplicationContext(), "입력하지 않은 정보가 있습니다.");
                }
            }
        });
    }

    public void sendToServer(String id, String name, String phone) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"member\",");
        sb.append("\"method\":\"auth\",");
        sb.append("\"id\":\"" + id + "\",");
        sb.append("\"name\":\"" + name + "\",");
        sb.append("\"phoneNo\":\"" + phone + "\"");
        sb.append("}");

        ConnectSocket.sendQueue.offer(sb.toString());
    }

    public void receiveFromServer() {
        List<String> lists = JsonHandler.messageReceived();
        String status = lists.get(0);

        String id = findPWIDText.getText().toString();

        if ("r200".equals(status)) {
            Util.startToast(this, "인증 성공하셨습니다.");
            Intent intent = new Intent(this, ResetPWActivity.class);
            intent.putExtra("id", id);

            startActivity(intent);
        } else {
            Util.startToast(this, "회원가입 되지 않은 사용자이거나 정보가 틀립니다.");
        }
    }
}
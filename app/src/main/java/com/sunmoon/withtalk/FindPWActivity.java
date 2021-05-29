package com.sunmoon.withtalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
                auth();
            }
        });
    }

    public void auth() {
        String id = findPWIDText.getText().toString();
        String name = findPWNameText.getText().toString();
        String phone = findPWPhoneText.getText().toString();
        if ((id.length() > 6) && (name.length() > 1) && (phone.length() > 10)) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"type\":\"member\",");
            sb.append("\"method\":\"auth\",");
            sb.append("\"id\":\"" + id + "\",");
            sb.append("\"name\":\"" + name + "\",");
            sb.append("\"phoneNo\":\"" + phone + "\"");
            sb.append("}");

            ConnectSocket.sendQueue.offer(sb.toString());

            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //회원 가입 성공 시 받은 메시지 {"method":"auth","status":"r200"}
            String msg = ConnectSocket.receiveQueue.poll();

            JsonParser parser = new JsonParser();
            JsonObject jsonObject = (JsonObject) parser.parse(msg);

            String method = jsonObject.get("method").toString();
            String status = jsonObject.get("status").toString();


            if ("\"auth\"".equals(method) && "\"r200\"".equals(status)) {
                Util.startToast(this, "인증 성공하셨습니다.");
                Intent intent = new Intent(this, ResetPWActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
            } else {
                Util.startToast(this, "회원가입되지 않은 사용자 이거나 정보가 틀립니다.");
            }
        }else{
            Util.startToast(this, "입력하지 않은 정보가 있습니다.");
        }
    }

}
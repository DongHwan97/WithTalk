package com.sunmoon.withtalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
                findID();
            }
        });
    }

    public void findID(){
        String name = findIDNameText.getText().toString();
        String phone = findIDPhoneText.getText().toString();
        if ((name.length() > 1) && (phone.length() > 10)) {
            //서버에 보내기
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"type\":\"member\",");
            sb.append("\"method\":\"findId\",");
            sb.append("\"name\":\"" + name + "\",");
            sb.append("\"phoneNo\":\"" + phone + "\"");
            sb.append("}");

            Log.d("++++++++++++++++", sb.toString());
            ConnectSocket.sendQueue.offer(sb.toString());

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //결과 받기
            String result = ConnectSocket.receiveQueue.poll();

//        Log.d("----------------", result);

            JsonParser parser = new JsonParser();
            JsonElement json = parser.parse(result);

            String method = json.getAsJsonObject().get("method").toString();
            String status = json.getAsJsonObject().get("status").toString();
            String id = json.getAsJsonObject().get("id").toString();

            if ("\"findId\"".equals(method) && "\"r200\"".equals(status)) {
                resultIDText.setText("아이디는 " + id + "입니다.");
            }else{
                Util.startToast(this,"회원가입되지 않은 사용자입니다.");
            }
        }else{
            Util.startToast(this,"입력하지 않은 정보가 있습니다.");
        }
    }
}
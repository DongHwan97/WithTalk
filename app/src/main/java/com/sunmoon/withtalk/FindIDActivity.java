package com.sunmoon.withtalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

        //서버에 보내기
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"" + "\"member\",");
        sb.append("\"method\":\"findId\",");
        sb.append("\"name\":\"" + name + "\",");
        sb.append("\"phoneNo\":\"" + phone + "\"");
        sb.append("}");

        ConnectSocket.sendQueue.offer(sb.toString());

        //결과 받기
        String result = ConnectSocket.receiveQueue.poll();

        JsonParser parser = new JsonParser();
        JsonObject json = (JsonObject)parser.parse(result);

        String method = json.get("method").toString();
        String id = json.get("id").toString();

        if ("findId".equals(method)) {
            resultIDText.setText(result);
        }
    }
}
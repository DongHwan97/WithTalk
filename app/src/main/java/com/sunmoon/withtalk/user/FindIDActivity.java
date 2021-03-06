package com.sunmoon.withtalk.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sunmoon.withtalk.R;
import com.sunmoon.withtalk.common.Util;
import com.sunmoon.withtalk.common.ConnectSocket;

import org.json.JSONException;
import org.json.JSONObject;

public class FindIDActivity extends AppCompatActivity {

    EditText findIDNameText, findIDPhoneText;
    Button findIDButton;
    TextView resultIDText;

    Context mContext;

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
                } else {
                    Util.startToast(getApplicationContext(),"입력하지 않은 정보가 있습니다.");
                }
            }
        });

        this.mContext = getBaseContext();
        messaging.start();
    }

    Thread messaging = new Thread(new Runnable() {
        @Override
        public void run() {
            String receivedMessage;
            JSONObject jsonObject;

            try {while(!Thread.currentThread().isInterrupted()) {
                receivedMessage = ConnectSocket.receiveQueue.peek();

                Thread.sleep(100);
                if (receivedMessage != null) {
                    jsonObject = new JSONObject(receivedMessage);

                    if (jsonObject.getString("method").equals("findId")) {
                        ConnectSocket.receiveQueue.poll();
                        if (jsonObject.getString("status").equals("r200")) {
                            String id = jsonObject.getString("id");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    resultIDText.setText("아이디는 " + id + "입니다.");
                                }
                            });

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Util.startToast(mContext, "회원가입되지 않은 사용자 입니다.");
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

    public void sendToServer(String name, String phone) {//정보 전송
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"member\",");
        sb.append("\"method\":\"findId\",");
        sb.append("\"name\":\"" + name + "\",");
        sb.append("\"phoneNo\":\"" + phone + "\"");
        sb.append("}");

        ConnectSocket.sendQueue.offer(sb.toString());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        messaging.interrupt();
    }
}
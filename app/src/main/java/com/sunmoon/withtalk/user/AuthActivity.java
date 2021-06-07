package com.sunmoon.withtalk.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sunmoon.withtalk.R;
import com.sunmoon.withtalk.common.Util;
import com.sunmoon.withtalk.common.ConnectSocket;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthActivity extends AppCompatActivity {

    EditText authIDText, authNameText, authPhoneText;
    Button authButton;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        authIDText = findViewById(R.id.authIDText);
        authNameText = findViewById(R.id.authNameText);
        authPhoneText = findViewById(R.id.authPhoneText);
        authButton = findViewById(R.id.authButton);

        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = authIDText.getText().toString();
                String name = authNameText.getText().toString();
                String phone = authPhoneText.getText().toString();

                if ((id.length() > 6) && (name.length() > 1) && (phone.length() > 10)) {
                    sendToServer(id, name, phone);
                } else {
                    Util.startToast(getApplicationContext(), "입력하지 않은 정보가 있습니다.");
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

                    if (jsonObject.getString("method").equals("auth")) {
                        ConnectSocket.receiveQueue.poll();
                        if (jsonObject.getString("status").equals("r200")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Util.startToast(mContext, "인증 성공하셨습니다.");

                                    Intent intent = new Intent(mContext, ResetPWActivity.class);
                                    intent.putExtra("id", authIDText.getText().toString());


                                    startActivity(intent);
                                }
                            });
                            messaging.interrupt();
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Util.startToast(mContext, "회원가입 되지 않은 사용자이거나 정보가 틀립니다.");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        messaging.interrupt();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        String id = authIDText.getText().toString();
        String name = authNameText.getText().toString();
        String phone = authPhoneText.getText().toString();

        if ((id.length() > 6) && (name.length() > 1) && (phone.length() > 10)) {
            onBackPressed();
        }

    }
}
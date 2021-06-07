package com.sunmoon.withtalk.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sunmoon.withtalk.R;
import com.sunmoon.withtalk.common.ConnectSocket;
import com.sunmoon.withtalk.common.Util;

import org.json.JSONException;
import org.json.JSONObject;


public class SignUpActivity extends AppCompatActivity {

    EditText signUpIDText, signUpNameText, signUpPhoneText, signUpPWText, signUpConfirmPW;
    Button signUpButton;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpIDText = findViewById(R.id.signUpIDText);
        signUpNameText = findViewById(R.id.signUpNameText);
        signUpPhoneText = findViewById(R.id.signUpPhoneText);
        signUpPWText = findViewById(R.id.signUpPWText);
        signUpConfirmPW = findViewById(R.id.signUpConfirmPW);
        signUpButton = findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = signUpIDText.getText().toString();
                String name = signUpNameText.getText().toString();
                String phone = signUpPhoneText.getText().toString();
                String pw = signUpPWText.getText().toString();
                String confirmPw = signUpConfirmPW.getText().toString();

                if ((id.length() > 6) && (name.length() > 1) && (phone.length() > 10) && (pw.length() > 7) && (confirmPw.length() > 7)) {//제약조건
                    if (!pw.equals(confirmPw)) {
                        Util.startToast(getApplicationContext(), "비밀번호가 일치하지 않습니다.");
                    } else {
                        sendToServer(id, name, phone, pw);//성공 시 서버로 전송
                    }
                } else {
                    Util.startToast(getApplicationContext(), "입력하지 않은 정보가 있거나 아이디 및 비밀번호가 8자리 이상이 아닙니다.");
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

                    if (jsonObject.getString("method").equals("signUp")) {
                        ConnectSocket.receiveQueue.poll();
                        if (jsonObject.getString("status").equals("r200")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Util.startToast(mContext, "회원가입에 성공했습니다.");
                                    onBackPressed();
                                    messaging.interrupt();
                                }
                            });

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Util.startToast(mContext, "회원가입에 실패했습니다.");
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

    public void sendToServer(String id, String name, String phone, String pw) {//회원 정보 보내기
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"member\",");
        sb.append("\"method\":\"signUp\",");
        sb.append("\"id\":\"" + id + "\",");
        sb.append("\"name\":\"" + name + "\",");
        sb.append("\"password\":\"" + pw + "\",");
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
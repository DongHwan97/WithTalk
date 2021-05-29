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

public class SignUpActivity extends AppCompatActivity {
    EditText signUpIDText, signUpNameText, signUpPhoneText, signUpPWText, signUpConfirmPW;
    Button signUpButton;

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
                signUp();
            }
        });
    }

    public void signUp() {
        String id = signUpIDText.getText().toString();
        String name = signUpNameText.getText().toString();
        String phone = signUpPhoneText.getText().toString();
        String pw = signUpPWText.getText().toString();
        String confirmPw = signUpConfirmPW.getText().toString();

        if ((id.length() > 7) && (name.length() > 1) && (phone.length() > 10) && (pw.length() > 7) && (confirmPw.length() > 7)) {
            if (!pw.equals(confirmPw)) {
                Util.startToast(this, "비밀번호가 일치하지 않습니다.");
            } else {
                //회원 정보 보내기
                StringBuilder sb = new StringBuilder();
                sb.append("{");
                sb.append("\"type\":\"member\",");
                sb.append("\"method\":\"signUp\",");
                sb.append("\"id\":\"" + id + "\",");
                sb.append("\"name\":\"" + name + "\",");
                sb.append("\"password\":\"" + pw + "\",");
                sb.append("\"phone_no\":\"" + phone + "\"");
                sb.append("}");

                ConnectSocket.sendQueue.offer(sb.toString());

                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //회원 가입 성공 시 받은 메시지 {"method":"signUp","status":"r200"}
                String msg = ConnectSocket.receiveQueue.poll();

                JsonParser parser = new JsonParser();
                JsonObject jsonObject = (JsonObject) parser.parse(msg);
                Log.e("signUp: ", msg);
                String method = jsonObject.get("method").toString();
                Log.e("signUp: ", method);
                String status = jsonObject.get("status").toString();
                Log.e("signUp: ", status);

                if ("\"signUp\"".equals(method) && "\"r200\"".equals(status)) {
                    Util.startToast(this, "회원가입에 성공하셨습니다.");
                    moveActivity(LoginActivity.class);
                }
            }
        } else {
            Util.startToast(this, "입력하지 않은 항목이 있습니다.");
        }
    }

    private void moveActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}
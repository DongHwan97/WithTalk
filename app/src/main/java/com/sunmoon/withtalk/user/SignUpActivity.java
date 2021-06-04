package com.sunmoon.withtalk.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sunmoon.withtalk.common.DataAdapter;
import com.sunmoon.withtalk.R;
import com.sunmoon.withtalk.common.ConnectSocket;
import com.sunmoon.withtalk.common.Friend;
import com.sunmoon.withtalk.common.JsonHandler;
import com.sunmoon.withtalk.common.Util;
import com.sunmoon.withtalk.user.LoginActivity;

import java.util.ArrayList;
import java.util.List;

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
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        receiveFromServer();//회원가입 정보 받기
                    }
                } else {
                    Util.startToast(getApplicationContext(), "입력하지 않은 정보가 있거나 아이디 및 비밀번호가 8자리 이상이 아닙니다.");
                }
            }
        });
    }

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

    public void receiveFromServer() {//회원가입 정보 받기
        List<String> lists = JsonHandler.messageReceived();
        String status = lists.get(0);

        if ("r200".equals(status)) {
            DataAdapter mDbHelper = new DataAdapter(this);
            mDbHelper.createDatabase();
            mDbHelper.open();
            mDbHelper.deleteAllFriend();

            List list = new ArrayList<Friend>();
            list = mDbHelper.selectAllFriend();
            Friend friend = null;
            for (int i = 0; i < list.size(); i++) {
                friend = (Friend) list.get(i);
            }
            mDbHelper.close();
            Util.startToast(this, "회원가입에 성공했습니다.");
            moveActivity(LoginActivity.class);//로그인 화면으로 이동
        } else {
            Util.startToast(this, "회원가입에 실패했습니다.");
        }
    }

    private void moveActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}
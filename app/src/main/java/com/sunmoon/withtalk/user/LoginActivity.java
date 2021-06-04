package com.sunmoon.withtalk.user;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.FileObserver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.sunmoon.withtalk.common.JsonHandler;
import com.sunmoon.withtalk.common.MainActivity;
import com.sunmoon.withtalk.R;
import com.sunmoon.withtalk.common.NotificationService;
import com.sunmoon.withtalk.common.Util;
import com.sunmoon.withtalk.common.ConnectSocket;

import java.io.File;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private EditText loginIDText, loginPWText;
    private CheckBox loginAutoCheck;
    private Button loginButton;

    ConnectSocket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.loginMoveSignUp).setOnClickListener(onClickListener);
        findViewById(R.id.loginMoveFindID).setOnClickListener(onClickListener);
        findViewById(R.id.loginMoveFindPW).setOnClickListener(onClickListener);
        findViewById(R.id.loginButton).setOnClickListener(onClickListener);

        loginButton = (Button) findViewById(R.id.loginButton);
        loginIDText = (EditText) findViewById(R.id.loginIDText);
        loginPWText = (EditText) findViewById(R.id.loginPWText);
        loginAutoCheck = (CheckBox) findViewById(R.id.loginAutoCheck);

        //socket = new ConnectSocket();//소켓 연결


        Intent intent = new Intent(LoginActivity.this, NotificationService.class);
        startService(intent);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.loginButton:

                    String id = loginIDText.getText().toString();
                    String pw = loginPWText.getText().toString();

                    if (id.length() > 6 && pw.length() > 7) {
                        sendToServer(id, pw);
                        try {
                            Thread.sleep(300);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        receiveFromServer();
                    } else {
                        Util.startToast(getApplicationContext(), "입력하지 않은 정보가 있습니다.");
                    }
                    break;
                case R.id.loginMoveFindID://아이디 찾기
                    moveActivity(FindIDActivity.class);
                    break;
                case R.id.loginMoveSignUp://회원가입
                    moveActivity(SignUpActivity.class);
                    break;
                case R.id.loginMoveFindPW://비밀번호 초기화
                    moveActivity(FindPWActivity.class);
                    break;
            }
        }
    };

    public void sendToServer(String id, String pw) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"" + "common" + "\",");
        sb.append("\"method\":\"" + "login" + "\",");
        sb.append("\"id\":\"" + id + "\",");
        sb.append("\"password\":\"" + pw + "\"");
        sb.append("}");

        ConnectSocket.sendQueue.offer((sb.toString()));
    }

    public void receiveFromServer() {
        ArrayList<String> lists = JsonHandler.messageReceived();

        String status = lists.get(0);

        if ("r200".equals(status)) {
            Util.startToast(this, "로그인 성공했습니다.");

            Intent intent = new Intent(this, MainActivity.class);
            String id = loginIDText.getText().toString();

            intent.putExtra("id", id);
            startActivity(intent);
        } else {
            Util.startToast(this, "로그인에 실패했습니다.");
        }
    }

    private void moveActivity(Class c) {//이동
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {//뒤로가기 버튼 누를 시 종료
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
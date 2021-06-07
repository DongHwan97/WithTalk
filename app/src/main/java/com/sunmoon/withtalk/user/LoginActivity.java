package com.sunmoon.withtalk.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


import com.sunmoon.withtalk.common.MainActivity;
import com.sunmoon.withtalk.R;
import com.sunmoon.withtalk.common.NotificationService;
import com.sunmoon.withtalk.common.Util;
import com.sunmoon.withtalk.common.ConnectSocket;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;


public class LoginActivity extends AppCompatActivity {

    private EditText loginIDText, loginPWText;
    private CheckBox loginAutoCheck;
    private Button loginButton;

    Context mContext;

    public String AUTO_LOGIN_FILE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.loginButton);
        loginIDText = (EditText) findViewById(R.id.loginIDText);
        loginPWText = (EditText) findViewById(R.id.loginPWText);
        loginAutoCheck = (CheckBox) findViewById(R.id.loginAutoCheck);

        findViewById(R.id.loginMoveSignUp).setOnClickListener(onClickListener);
        findViewById(R.id.loginMoveFindID).setOnClickListener(onClickListener);
        findViewById(R.id.loginMoveAuth).setOnClickListener(onClickListener);
        findViewById(R.id.loginButton).setOnClickListener(onClickListener);
        loginAutoCheck.setOnClickListener(onClickListener);

        Intent intent = new Intent(LoginActivity.this, NotificationService.class);
        startService(intent);

        messaging.start();

        this.mContext = getBaseContext();
        ConnectSocket.mContext = this.mContext;

        AUTO_LOGIN_FILE =  mContext.getDataDir().getAbsolutePath() + "/login.lo";
        File f = new File(AUTO_LOGIN_FILE);

        try {
            if (f.exists()) {// 자동로그인 성공기록
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line = br.readLine();
                loginIDText.setText(line.split(",")[0]);
                loginPWText.setText(line.split(",")[1]);
                loginButton.performClick();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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

                case R.id.loginMoveAuth://비밀번호 초기화
                    moveActivity(AuthActivity.class);
                    break;

                case R.id.loginAutoCheck:
                    if (!loginAutoCheck.isChecked()) {
                        File f = new File(AUTO_LOGIN_FILE);
                        if (f.exists()) {
                            f.delete();
                        }
                    }

            }
        }
    };

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

                    if (jsonObject.getString("method").equals("login")) {
                        ConnectSocket.receiveQueue.poll();
                        if (jsonObject.getString("status").equals("r200")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Util.startToast(mContext, "로그인 성공");
                                        File f = new File(AUTO_LOGIN_FILE);
                                        BufferedWriter bw = new BufferedWriter(new FileWriter(f));

                                        String id = loginIDText.getText().toString();
                                        String pw = loginPWText.getText().toString();
                                        bw.write(id + "," + pw);
                                        bw.flush();

                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                                        intent.putExtra("id", id);
                                        startActivity(intent);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            messaging.interrupt();
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Util.startToast(mContext, "로그인 실패");
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
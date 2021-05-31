package com.sunmoon.withtalk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;

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

        socket = new ConnectSocket();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.loginButton:

                    sendToServer();
                    try {
                        Thread.sleep(150);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    receiveFromServer();

                    break;
                case R.id.loginMoveFindID:
                    moveActivity(FindIDActivity.class);
                    break;
                case R.id.loginMoveSignUp:
                    moveActivity(SignUpActivity.class);
                    break;
                case R.id.loginMoveFindPW:
                    moveActivity(FindPWActivity.class);
                    break;
            }
        }
    };

    public void sendToServer() {
        String id = loginIDText.getText().toString();
        String pw = loginPWText.getText().toString();
        if ((id.length() > 6) && (pw.length() > 7)) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"type\":\"" + "common" + "\",");
            sb.append("\"method\":\"" + "login" + "\",");
            sb.append("\"id\":\"" + id + "\",");
            sb.append("\"password\":\"" + pw + "\"");
            sb.append("}");

            ConnectSocket.sendQueue.offer((sb.toString()));
        } else {
            Util.startToast(getApplicationContext(), "입력하지 않은 정보가 있습니다.");
        }
    }

    public void receiveFromServer() {
        String[] list = JsonHandler.messageReceived();
        String status = list[0];

        if ("r200".equals(status)) {
            Util.startToast(this, "로그인 성공하셨습니다.");

            Intent intent = new Intent(this, MainActivity.class);
            String id = loginIDText.getText().toString();

            intent.putExtra("id", id);
            startActivity(intent);
        } else {
            Util.startToast(this, "에헤헤 뵹신 !");
        }
    }

    private void moveActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
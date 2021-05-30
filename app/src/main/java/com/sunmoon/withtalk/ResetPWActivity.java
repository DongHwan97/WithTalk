package com.sunmoon.withtalk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ResetPWActivity extends Activity {

    EditText newPWText, newPWConfirmText;
    Button resetPWButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pw);

        newPWText = findViewById(R.id.newPWText);
        newPWConfirmText = findViewById(R.id.newPWConfirmText);
        resetPWButton = findViewById(R.id.resetPWButton);

        resetPWButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToServer();
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                receiveFromServer();
            }
        });
    }

    public void sendToServer() {
        String pw = newPWText.getText().toString();
        String confirmPw = newPWConfirmText.getText().toString();
        Intent intent = getIntent();
        String id = (String) intent.getSerializableExtra("id");

        if (pw.equals(confirmPw) && (pw.length() > 7)) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"type\":\"member\",");
            sb.append("\"method\":\"resetPassword\",");
            sb.append("\"id\":\"" + id + "\",");
            sb.append("\"newPassword\":\"" + pw + "\"");
            sb.append("}");

            ConnectSocket.sendQueue.offer(sb.toString());
        } else {
            Util.startToast(this, "비밀번호가 일치하지 않거나 8자리 이상이 아닙니다.");
        }
    }

    public void receiveFromServer() {
        String[] list = JsonHandler.messageReceived();
        String status = list[0];
        if ("r200".equals(status)) {
            Util.startToast(this, "비밀번호 재설정이 성공하였습니다.");
            moveActivity(LoginActivity.class);
        } else {
            Util.startToast(this, "비밀번호 재설정 실패 !");
        }
    }


    private void moveActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}
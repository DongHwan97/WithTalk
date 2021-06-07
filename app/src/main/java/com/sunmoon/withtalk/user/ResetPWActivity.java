package com.sunmoon.withtalk.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sunmoon.withtalk.R;
import com.sunmoon.withtalk.common.ConnectSocket;
import com.sunmoon.withtalk.common.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class ResetPWActivity extends Activity {

    EditText newPWText, newPWConfirmText;
    Button resetPWButton;

    Context mContext;

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
                String pw = newPWText.getText().toString();
                String confirmPw = newPWConfirmText.getText().toString();
                Intent intent = getIntent();
                String id = intent.getStringExtra("id");
                if (pw.equals(confirmPw) && (pw.length() > 7)) {
                    sendToServer(id, pw);
                } else {
                    Util.startToast(getApplicationContext(), "비밀번호가 일치하지 않거나 8자리 이상이 아닙니다.");
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

                    if (jsonObject.getString("method").equals("resetPassword")) {
                        ConnectSocket.receiveQueue.poll();
                        if (jsonObject.getString("status").equals("r200")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Util.startToast(mContext, "비밀번호 재설정이 성공하였습니다.");

                                    onBackPressed();
                                }
                            });
                            messaging.interrupt();

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Util.startToast(mContext, "비밀번호 재설정 실패 !");
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
        sb.append("\"type\":\"member\",");
        sb.append("\"method\":\"resetPassword\",");
        sb.append("\"id\":\"" + id + "\",");
        sb.append("\"newPassword\":\"" + pw + "\"");
        sb.append("}");
        ConnectSocket.sendQueue.offer(sb.toString());
    }
}

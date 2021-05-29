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
                resetPW();
            }
        });
    }

    public void resetPW(){
        String pw = newPWText.getText().toString();
        String confirmPw = newPWConfirmText.getText().toString();
        Intent intent = getIntent();
        String id = (String) intent.getSerializableExtra("id");
        Log.e("auth: ", "로그입니다"+id);
        if (pw.equals(confirmPw)) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"type\":\"member\",");
            sb.append("\"method\":\"resetPassword\",");
            sb.append("\"id\":\"" + id + "\",");
            sb.append("\"newPassword\":\"" + pw + "\",");
            sb.append("}");

            ConnectSocket.sendQueue.offer(sb.toString());

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //회원 가입 성공 시 받은 메시지 {"method":"auth","status":"r200"}
            String msg = ConnectSocket.receiveQueue.poll();

            JsonParser parser = new JsonParser();
            JsonObject jsonObject = (JsonObject) parser.parse(msg);

            String method = jsonObject.get("method").toString();
            Log.e("resetPassword: ", "로그입니다"+method);
            String status = jsonObject.get("status").toString();
            Log.e("resetPassword: ", "로그입니다"+status);

            if ("\"resetPassword\"".equals(method) && "\"r200\"".equals(status)) {
                Util.startToast(this, "비밀번호가 초기화 되었습니다.");
                moveActivity(LoginActivity.class);
            }
        }else{
            Util.startToast(this, "비밀번호가 일치하지 않습니다.");
        }
    }


    private void moveActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}

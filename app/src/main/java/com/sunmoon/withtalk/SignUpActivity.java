package com.sunmoon.withtalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

    public void signUp(){
        String id, name, phone, pw, confirmPw;
        id = signUpIDText.getText().toString();
        name = signUpNameText.getText().toString();
        phone = signUpPhoneText.getText().toString();
        pw = signUpPWText.getText().toString();
        confirmPw = signUpConfirmPW.getText().toString();

        if((id.length()>6)&&(name.length()>1)&&(phone.length()>10)&&(pw.length()>7)&&(confirmPw.length()>7)) {
            if(!pw.equals(confirmPw)){
                Util.startToast(this, "비밀번호가 일치하지 않습니다.");
            }else{
                //회원가입 메시지 전송
                Util.startToast(this, "회원가입에 성공하셨습니다.");
                moveActivity(LoginActivity.class);
            }
        }else{
            Util.startToast(this, "입력하지 않은 항목이 있습니다.");
        }
    }
    private void moveActivity(Class c){
        Intent intent = new Intent(this,c);
        startActivity(intent);
    }
}
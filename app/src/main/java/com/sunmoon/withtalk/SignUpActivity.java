package com.sunmoon.withtalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignUpActivity extends AppCompatActivity {
    EditText idEditText, nameEditText, phoneEditText, pwEditText, confirmEditText;
    Button signUpButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        idEditText = findViewById(R.id.idEditText);
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        pwEditText = findViewById(R.id.pwEditText);
        confirmEditText = findViewById(R.id.confirmEditText);
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
        id = idEditText.getText().toString();
        name = nameEditText.getText().toString();
        phone = phoneEditText.getText().toString();
        pw = pwEditText.getText().toString();
        confirmPw = confirmEditText.getText().toString();

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
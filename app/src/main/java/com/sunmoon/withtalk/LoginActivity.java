package com.sunmoon.withtalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    private EditText editIDText, editPWText;
    private TextView moveSignUpText, moveFindIDText, moveResetPWText;
    private CheckBox autoLoginBox;
    private Button loginButton;
    private String id, pw;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

/*        SharedPreferences auto = getSharedPreferences("auto", MODE_PRIVATE);

        id = auto.getString("ID",null);
        pw = auto.getString("PW",null);

        if(id != null &&pw!=null){
            if(id.equals("12345678") && pw.equals("12345678")){
                Util.startToast(this,"자동로그인 성공");
                moveActivity(MainActivity.class);
            }
        }*/



        findViewById(R.id.moveSignUp).setOnClickListener(onClickListener);
        findViewById(R.id.moveFindID).setOnClickListener(onClickListener);
        findViewById(R.id.moveResetPW).setOnClickListener(onClickListener);
        findViewById(R.id.loginButton).setOnClickListener(onClickListener);
        loginButton = (Button) findViewById(R.id.loginButton);
        moveSignUpText = (TextView)findViewById(R.id.moveSignUp);
        moveFindIDText = (TextView)findViewById(R.id.moveFindID);
        moveResetPWText = (TextView)findViewById(R.id.moveResetPW);
        editIDText = (EditText) findViewById(R.id.editID);
        editPWText = (EditText)findViewById(R.id.editPW);
        autoLoginBox = (CheckBox)findViewById(R.id.autoLogin);


    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.loginButton:

                    login();
                    break;
                case R.id.moveFindID: moveActivity(FindIDActivity.class);
                    break;
                case R.id.moveSignUp: moveActivity(SignUpActivity.class);
                    break;
                case R.id.moveResetPW: moveActivity(ResetPWActivity.class);
                    break;
            }
        }
    };

    private void login(){//로그인
        id = editIDText.getText().toString();
        pw = editPWText.getText().toString();

        if((id.length()<8)&&(pw.length()<8)){
            Util.startToast(this,"아이디와 비밀번호는 8자 이상입니다.");
            //로그인 추가작성필요 TO DO
        }else{
            Util.startToast(this,"로그인에 성공하였습니다.");
            moveActivity(MainActivity.class);
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    private void moveActivity(Class c){
        Intent intent = new Intent(this,c);
        startActivity(intent);
    }
}
package com.sunmoon.withtalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText editIDText, editPWText;
    private CheckBox autoLogin;
    private Button loginButton;
    String id, pw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.moveSignUp).setOnClickListener(onClickListener);
        findViewById(R.id.moveFindID).setOnClickListener(onClickListener);
        findViewById(R.id.moveResetPW).setOnClickListener(onClickListener);
        findViewById(R.id.loginButton).setOnClickListener(onClickListener);

        loginButton = (Button) findViewById(R.id.loginButton);

        editIDText = (EditText) findViewById(R.id.editID);
        editPWText = (EditText) findViewById(R.id.editPW);
        autoLogin = (CheckBox) findViewById(R.id.autoLogin);

    }
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.loginButton:
                        moveActivity(MainActivity.class);
                        break;
                    case R.id.moveFindID:
                        moveActivity(FindIDActivity.class);
                        break;
                    case R.id.moveSignUp:
                        moveActivity(SignUpActivity.class);
                        break;
                    case R.id.moveResetPW:
                        moveActivity(ResetPWActivity.class);
                        break;
                }
            }
        };


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
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
    private EditText editIDText, editPWText;
    private CheckBox autoLogin;
    private Button loginButton;

    static Socket socket;
    SocketManager socketManager;
    static Writer writer;
    //    JsonReader reader;
    static BufferedReader reader;

    String ipAddress = "192.168.25.6";
    int portNum = 5050;
    int status;

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
                    if (login() == 200) {
                        moveActivity(MainActivity.class);
                    }
                    break;
                case R.id.moveFindID:
                    moveActivity(FindIDActivity.class);
                    break;
                case R.id.moveSignUp:
                    moveActivity(SignUpActivity.class);
                    break;
                case R.id.moveResetPW:
                    moveActivity(FindPWActivity.class);
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        manager = SocketManager.getInstance();
    }

    public int login() {
        String id = editIDText.getText().toString();
        String pw = editPWText.getText().toString();

        if (id.length() > 2 && pw.length() > 2) {
            JsonObject sendJson = new JsonObject();
            sendJson.addProperty("method", "login");
            sendJson.addProperty("id", id);
            sendJson.addProperty("password", pw);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendInfo(sendJson);
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    receiveInfo();
                }
            }).start();

            if (status == 200) {
                return status;
            } else {
                Toast.makeText(this.getApplicationContext(), "잘못쳤다!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this.getApplicationContext(), "멍충이!", Toast.LENGTH_LONG).show();
        }
        return 404;
    }

    public void sendInfo(JsonObject sendJson) {
        try {
            socket = new Socket(ipAddress, portNum);

            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            writer.write(sendJson.toString());
            writer.flush();
            Log.d("Send", sendJson.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int receiveInfo() {
        JsonParser parser = new JsonParser();

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while(reader.read() != -1) {
                String msg = reader.readLine();

                JsonObject resultJson = (JsonObject) parser.parse(msg);
                status = Integer.parseInt(String.valueOf(resultJson.get("status")));
                Log.d("Status!!!!!!!!!!!!!", String.valueOf(status));
//                JsonElement jsonElement1 = resultJson.get("result");
//                result = jsonElement1.getAsString(); //OK
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
//                    in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        reader = new JsonReader(new InputStreamReader(socket.getInputStream()));
//        //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        Log.i("wow", " 되나,..?");
//
//        while (reader.hasNext()) {
//            String name = reader.nextName();
//            if (name.equals("status")) {
//                status = reader.nextInt();
//                Log.d("우와!!!!!!!!!", Integer.toString(status));
//
//                return status;
//            }
//        }
        return status;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    private void moveActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
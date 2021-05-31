package com.sunmoon.withtalk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.graphics.fonts.Font;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

//전송 구현 스레드 구현
public class ChatActivity extends AppCompatActivity {

    SpeechRecognizer speechRecognizer;
    Intent intent;
    LinearLayout chatLayout;
    ImageButton chatSendButton;
    EditText chatContentText;
    TextView chatRoomText;
    ScrollView scrollView;

    Context mContext;

    DatabaseHelper dbHelper;
    SQLiteDatabase sqlDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent idIntent = getIntent();
        String friendName = idIntent.getStringExtra("friendName");
        String friendId = idIntent.getStringExtra("friendId");

        mContext = this;

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO}, 1);

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getApplicationContext().getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        scrollView = findViewById(R.id.scrollView);
        chatLayout = findViewById(R.id.chatLayout);
        chatSendButton = findViewById(R.id.chatSendButton);
        chatContentText = findViewById(R.id.chatContentText);
        chatRoomText = findViewById(R.id.chatRoomText);

        chatRoomText.setText(friendName);
        ConnectSocket.chatRoomID = chatRoomText.getText().toString();

        // 내부DB가져옴



        chatLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.e("TTS", "터치이벤트");
                    //TO DO STT사용
                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                    speechRecognizer.setRecognitionListener(recognitionListener);
                    speechRecognizer.startListening(intent);
                }
                return true;
            }
        });

        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send(chatContentText.getText().toString());
            }
        });

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll((ScrollView.FOCUS_DOWN));
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    LinearLayout tLayout = new LinearLayout(mContext);

                    TextView dateText = new TextView(mContext);
                    TextView contentText = new TextView(mContext);

                    if (ConnectSocket.receiveQueue.peek() != null) {

                        String data = ConnectSocket.receiveQueue.poll();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dateText.setText("00:09");
                                contentText.setText(data.split("#")[1]);

                                if (data.split("#")[0].charAt(0) == 'b') {
                                    tLayout.setGravity(Gravity.RIGHT);
                                    tLayout.addView(dateText);
                                    tLayout.addView(contentText);

                                } else {
                                    tLayout.addView(contentText);
                                    tLayout.addView(dateText);
                                }

                                chatLayout.addView(tLayout);

                            }
                        });

                    }
                }

                /*
                while ((msg_data = msgList.poll()) != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LinearLayout tLayout = new LinearLayout(mContext);

                            TextView dateText = new TextView(mContext);
                            TextView contentText = new TextView(mContext);

                            dateText.setText("00:09");
                            contentText.setText(msg_data);

                            //msg_data = "a#안녕" -> 상대방
                            // "b#굳" -> 내꺼
                            if (msg_data.split("#")[0].charAt(0) == 'b') {
                                tLayout.setGravity(Gravity.RIGHT);
                                tLayout.addView(dateText);
                                tLayout.addView(contentText);

                            } else {
                                tLayout.addView(contentText);
                                tLayout.addView(dateText);
                            }

                            chatLayout.addView(tLayout);
                        }
                    });
                }
                */
            }
        }).start();


        }
        RecognitionListener recognitionListener = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {
                String message;

                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        message = "오디오 에러";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        message = "클라이언트 에러";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        message = "퍼미션 없음";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        message = "네트워크 에러";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        message = "네트웍 타임아웃";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = "찾을 수 없음";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = "RECOGNIZER가 바쁨";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        message = "서버가 이상함";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        message = "말하는 시간초과";
                        break;
                    default:
                        message = "알 수 없는 오류임";
                        break;
                }
                Util.startToast(getApplicationContext(), "에러가 발생했습니다. : "+message);
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches =
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                for(int i = 0; i < matches.size() ; i++){
                    chatContentText.setText(matches.get(i));
                    send(chatContentText.getText().toString());
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        };

    public void send(String message){
        Log.e("sendmessage", message);
        ConnectSocket.sendQueue.offer("b#"+message);
        chatContentText.setText(null);
    }
}
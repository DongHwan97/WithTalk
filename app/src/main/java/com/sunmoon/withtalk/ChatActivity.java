package com.sunmoon.withtalk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
//전송 구현 스레드 구현
public class ChatActivity extends AppCompatActivity {

    SpeechRecognizer speechRecognizer;
    Intent intent;
    LinearLayout chatLayout;
    ImageButton sendButton;
    EditText contentEditText;
    TextView chatRoomText;

    int[] index = {0,1,0,1,1,1,1,1,1,1};
    String[] text = {"a","b", "abc", "sdf", "a","b", "abc", "sdf", "a","b", "abc", "sdf", "a","b", "abc", "sdf"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO}, 1);
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getApplicationContext().getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        chatLayout = findViewById(R.id.chatLayout);
        sendButton = findViewById(R.id.sendButton);
        contentEditText = findViewById(R.id.contentEditText);
        chatRoomText = findViewById(R.id.chatRoomText);

        chatRoomText.setText("박정우");

        for (int i = 0; i < index.length; i++) {
            LinearLayout tLayout = new LinearLayout(this);

            TextView dateText = new TextView(this);
            TextView contentText = new TextView(this);

            dateText.setText("00:09");
            contentText.setText(text[i]);

            if (index[i] == 1) {
                tLayout.setGravity(Gravity.RIGHT);
                tLayout.addView(dateText);
                tLayout.addView(contentText);

            } else {
                tLayout.addView(contentText);
                tLayout.addView(dateText);
            }

            chatLayout.addView(tLayout);
        }

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

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send(contentEditText.getText().toString());
            }
        });
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
                    contentEditText.setText(matches.get(i));
                    send(contentEditText.getText().toString());
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
    };

    public String send(String message){
        Log.e("sendmessage", contentEditText.getText().toString());
        //JSON서버에 보내고
        return message;
    }
}
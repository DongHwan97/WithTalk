package com.sunmoon.withtalk.chatroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sunmoon.withtalk.common.ConnectSocket;
import com.sunmoon.withtalk.db.DataAdapter;
import com.sunmoon.withtalk.friend.FriendList;
import com.sunmoon.withtalk.common.MainActivity;
import com.sunmoon.withtalk.R;
import com.sunmoon.withtalk.db.MessageDB;
import com.sunmoon.withtalk.common.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ChatActivity extends AppCompatActivity {

    SpeechRecognizer speechRecognizer;
    Intent intent;
    LinearLayout chatLayout;
    ImageButton chatSendButton;
    EditText chatContentText;
    TextView chatRoomText;
    ScrollView scrollView;

    Context mContext;

    public TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent idIntent = getIntent();
        String friendName = idIntent.getStringExtra("friendName");
        String friendId = idIntent.getStringExtra("friendId");

        mContext = this;

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status== TextToSpeech.SUCCESS){
                    tts.setLanguage(Locale.KOREAN);
                }else{
                    Log.e("TTS", "TTS생성 실패");
                }
            }
        });

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO}, 1);

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getApplicationContext().getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        scrollView = findViewById(R.id.chatScrollView);
        chatLayout = findViewById(R.id.chatLayout);
        chatSendButton = findViewById(R.id.chatSendButton);
        chatContentText = findViewById(R.id.chatContentText);
        chatRoomText = findViewById(R.id.chatRoomText);
        chatRoomText.setText(friendName == null ? "(이름 없음)": friendName);

        chatLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                    speechRecognizer.setRecognitionListener(recognitionListener);
                    speechRecognizer.startListening(intent);
                    Log.d( "onTouch: ","터치댐");
                }
                return true;
            }
        });

        chatSendButton.setOnClickListener(new View.OnClickListener() {//채팅전송
            @Override
            public void onClick(View v) {
                sendChat(chatContentText.getText().toString());
            }
        });

        receiveChat.start();
        selectMessage.start();
     }

    RecognitionListener recognitionListener = new RecognitionListener() {//STT
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
                    message = "권한 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "에러";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버 에러";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "시간초과 에러";
                    break;
                default:
                    message = "알 수 없는 오류";
                    break;
            }
            Util.startToast(getApplicationContext(), message+"가 발생했습니다.");
        }

        @Override
        public void onResults(Bundle results) {//음성 인식 결과
            ArrayList<String> result =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for(int i = 0; i < result.size() ; i++){
                chatContentText.setText(result.get(i));
                sendChat(chatContentText.getText().toString());
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    };


    Thread selectMessage = new Thread(new Runnable() {
        @Override
        public void run() {
            DataAdapter mDbHelper = new DataAdapter(mContext);
            mDbHelper.createDatabase();
            mDbHelper.open();

            mDbHelper.updateReadMessage(FriendList.chatRoomNo);

            List<MessageDB> list = mDbHelper.selectOneChatRoomMessages(FriendList.chatRoomNo);
            MessageDB messageDB;

            if (list.size() != 0) {
                for (int i = 0; i < list.size(); i++) {
                    messageDB = list.get(i);

                    String senderId = messageDB.getSenderId();
                    String contents = messageDB.getContents();
                    String sendTime = messageDB.getSendTime();
                    String isRead = messageDB.getIsRead();


                    LinearLayout tLayout = new LinearLayout(mContext);

                    TextView dateText = new TextView(mContext);
                    TextView contentText = new TextView(mContext);
                    contentText.setTextSize(24);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dateText.setText(sendTime);
                            contentText.setText(contents);

                            if ( senderId.equals(MainActivity.id)) {
                                tLayout.setGravity(Gravity.RIGHT);
                                tLayout.addView(dateText);
                                tLayout.addView(contentText);

                            } else {
                                tLayout.addView(contentText);
                                tLayout.addView(dateText);
                            }

                            chatLayout.addView(tLayout);

                            scrollView.post(new Runnable() {//풀스크린일때 새로생기면 아래로 떙김
                                @Override
                                public void run() {
                                    scrollView.fullScroll((ScrollView.FOCUS_DOWN));
                                }
                            });

                        }
                    });
                }
            }
            // db 닫기
            mDbHelper.close();
        }
    });

    public void sendChat(String message){

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"" + "chat" + "\",");
        sb.append("\"method\":\"" + "sendChat" + "\",");
        sb.append("\"senderId\":\"" + MainActivity.id + "\",");
        sb.append("\"chatRoomNo\":" + FriendList.chatRoomNo + ",");
        sb.append("\"contents\":\"" + message + "\"");
        sb.append("}");
        ConnectSocket.sendQueue.offer((sb.toString()));
        chatContentText.setText(null);

    }

    Thread receiveChat = new Thread(new Runnable() {
        @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                try {
                Thread.sleep(50);
                if(ConnectSocket.toChatQueue.peek()!=null){
                    String receivedMessage = ConnectSocket.toChatQueue.poll();
                    LinearLayout tLayout = new LinearLayout(mContext);

                    TextView dateText = new TextView(mContext);
                    TextView contentText = new TextView(mContext);
                    contentText.setTextSize(24);

                    JSONObject json = new JSONObject(receivedMessage);
                    String contents = json.getString("contents");
                    String sendTime = json.getString("sendTime");
                    String senderId = json.getString("senderId");
                    tts.speak(contents + " "+sendTime, TextToSpeech.QUEUE_FLUSH, null);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dateText.setText(sendTime);
                            contentText.setText(contents);

                            if ( senderId.equals(MainActivity.id)) {
                                tLayout.setGravity(Gravity.RIGHT);
                                tLayout.addView(dateText);
                                tLayout.addView(contentText);

                            } else {
                                tLayout.addView(contentText);
                                tLayout.addView(dateText);
                            }

                            chatLayout.addView(tLayout);

                            scrollView.post(new Runnable() {//풀스크린일때 새로생기면 아래로 떙김
                                @Override
                                public void run() {
                                    scrollView.fullScroll((ScrollView.FOCUS_DOWN));
                                }
                            });
                        }
                    });
                }} catch (JSONException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(receiveChat.isAlive()){
            receiveChat.interrupt();
        }
        ConnectSocket.toChatQueue.clear();
        FriendList.chatRoomNo ="0";
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }
    }
}
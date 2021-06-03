package com.sunmoon.withtalk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class groupChatRoomActivity extends AppCompatActivity {

    SpeechRecognizer speechRecognizer;
    Intent intent;
    LinearLayout chatLayout;
    ImageButton chatSendButton;
    EditText chatContentText;
    TextView chatRoomText;
    ScrollView scrollView;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_room);
        Intent idIntent = getIntent();
        String friendName = idIntent.getStringExtra("friendName");;
        String friendId = idIntent.getStringExtra("friendId");
        Intent idIntent2 = getIntent();
        String friendList = idIntent2.getStringExtra("friendList");
        FriendList.chatRoomId = idIntent2.getStringExtra("chatRoomId");
        Intent ttsIntent = getIntent();
        String ttsName = ttsIntent.getStringExtra("ttsName");

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

        if(friendName==null&&ttsName==null){
            chatRoomText.setText(friendList);
        }else if(friendList==null&&ttsName==null){
            chatRoomText.setText(friendName);
        }else{
            chatRoomText.setText(ttsName);
        }



        createChatRoom(friendId);



        // 내부DB가져옴




        chatLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //TO DO STT사용
                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                    speechRecognizer.setRecognitionListener(recognitionListener);
                    speechRecognizer.startListening(intent);
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

        scrollView.post(new Runnable() {//풀스크린일때 새로생기면 아래로 떙김
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
                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String result = ConnectSocket.receiveQueue.poll();

                        try {
                            JSONObject json = new JSONObject(result);
                            String method = json.getString("method");
                            String contents = json.getString("contents");
                            String chatRoomNo = json.getString("chatRoomNo");
                            String senderId = json.getString("senderId");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //dateText.setText(Long.toString(System.currentTimeMillis()));
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

                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
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

    public void createChatRoom(String friendId){
        ArrayList<String> list = new ArrayList<>();
        list.add("\""+MainActivity.id+"\"");
        list.add("\""+friendId+"\"");

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"" + "chatRoom" + "\",");
        sb.append("\"method\":\"" + "create" + "\",");
        sb.append("\"senderId\":\"" + MainActivity.id + "\",");
        sb.append("\"receiverId\":" + list + ",");
        sb.append("\"chatRoomName\":\"" + null + "\"");
        sb.append("}");
        ConnectSocket.sendQueue.offer((sb.toString()));
        Log.e("asd", "createChatRoom: "+sb.toString() );
        try {
            Thread.sleep(300);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 결과 받기
        String result = ConnectSocket.receiveQueue.poll();
        Log.e("asd", "createChatRoom: "+result );
        try {
            JSONObject json = new JSONObject(result);
            String method = json.getString("method");
            String status = json.getString("status");
            if ("create".equals(method)&&"r200".equals(status)) {
                Util.startToast(this,"대화방생성");
            }else{
                Util.startToast(this,"대화방생성 실패");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendChat(String message){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"" + "chat" + "\",");
        sb.append("\"method\":\"" + "sendChat" + "\",");
        sb.append("\"senderId\":\"" + MainActivity.id + "\",");
        sb.append("\"chatRoomNo\":" + FriendList.chatRoomId + ",");
        sb.append("\"contents\":\"" + message + "\"");
        sb.append("}");
        ConnectSocket.sendQueue.offer((sb.toString()));
        chatContentText.setText(null);
    }
}
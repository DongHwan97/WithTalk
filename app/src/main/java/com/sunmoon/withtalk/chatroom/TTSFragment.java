package com.sunmoon.withtalk.chatroom;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.sunmoon.withtalk.R;
import com.sunmoon.withtalk.common.FriendList;

import java.util.ArrayList;


public class TTSFragment extends Fragment {

    TextView nameView;
    ConstraintLayout constView;
    Intent intent;
    SpeechRecognizer speechRecognizer;

    public static TTSFragment newInstance() {
        TTSFragment fragment = new TTSFragment();
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_tts, container, false);
        nameView = (TextView)rootView.findViewById(R.id.nameView);
        constView = (ConstraintLayout)rootView.findViewById(R.id.constView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);


        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO},1);

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getActivity().getApplicationContext().getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        constView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN){
                    Log.e("TTS", "터치이벤트");
                    //TO DO STT사용
                    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
                    speechRecognizer.setRecognitionListener(listener);
                    speechRecognizer.startListening(intent);
                }
                return true;
            }
        });
    }

    RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rms) {

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
            Toast.makeText(getContext() , message+"가 발생했습니다.",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {//음성 인식 결과
            ArrayList<String> result =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for(int i = 0; i < result.size() ; i++){
                String sttResult = result.get(i);
                nameView.setText(sttResult);
                Intent intent = new Intent(getContext(), ChatActivity.class);//수정필요
                for(String key : FriendList.FRIEND_LIST.keySet()){
                    String friendName = FriendList.FRIEND_LIST.get(key);
                    if(sttResult.contains(friendName)){
                        intent.putExtra("friendName", friendName);
                        startActivity(intent);
                    }
                }

            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    };

}
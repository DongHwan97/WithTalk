package com.sunmoon.withtalk;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;


public class ChatroomListFragment extends Fragment {

    private TextToSpeech tts;

    public static ChatroomListFragment newInstance() {
        ChatroomListFragment fragment = new ChatroomListFragment();
        return fragment;
    }
    @Nullable
    @Override
    public View getView() {
        changeFragment();
        return super.getView();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatroom_list, container, false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        setTTS();
    }
    // 글자 읽어주기
    private void setTTS() {
        tts = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status== TextToSpeech.SUCCESS){
                    tts.setLanguage(Locale.KOREAN);
                }else{
                    Log.e("TTS", "Initialization Failed");
                }
            }
        });
    }
    public void changeFragment(){
        tts.speak("채팅 목록 화면", TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onStop(){
        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }
        super.onStop();
    }
}
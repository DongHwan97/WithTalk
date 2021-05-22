package com.sunmoon.withtalk;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.Locale;


public class FriendListFragment extends Fragment {

    private TextToSpeech tts;
int id;
    public static FriendListFragment newInstance() {
        FriendListFragment fragment = new FriendListFragment();
        return fragment;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        id=1;
        setTTS();
    }
    @Nullable
    @Override
    public View getView() {
        //changeFragment();
        Log.e("TTS", "친구목록");
        if(id==1){
            changeFragment();
        }
        return super.getView();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friend_list, container, false);
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
        tts.speak("친구 목록 화면", TextToSpeech.QUEUE_FLUSH, null);
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
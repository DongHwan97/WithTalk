package com.sunmoon.withtalk;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.Locale;

public class SettingFragment extends Fragment {

    private TextToSpeech tts;

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        setTTS();
    }
    @Nullable
    @Override
    public View getView() {
        changeFragment();
        return super.getView();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_setting, container, false);
        ImageButton logOutButton = (ImageButton)rootView.findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.startToast(getActivity(), "로그아웃");
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
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
        tts.speak("세팅 화면", TextToSpeech.QUEUE_FLUSH, null);
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
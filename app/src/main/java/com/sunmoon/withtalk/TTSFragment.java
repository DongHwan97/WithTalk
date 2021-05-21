package com.sunmoon.withtalk;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class TTSFragment extends Fragment {

    public TextToSpeech tts;

    public static TTSFragment newInstance() {
        TTSFragment fragment = new TTSFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tts, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
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

    @Nullable
    @Override
    public View getView() {
        changeFragment();
        return super.getView();
    }

    public void changeFragment(){
        tts.speak("미확인메시지 확인 화면", TextToSpeech.QUEUE_FLUSH, null);
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
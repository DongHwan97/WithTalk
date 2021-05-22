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
import android.widget.Button;

import java.util.Locale;


public class ChatroomListFragment extends Fragment {

    Button btn;

    public static ChatroomListFragment newInstance() {
        ChatroomListFragment fragment = new ChatroomListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_chatroom_list, container, false);
        btn = (Button) rootView.findViewById(R.id.button12);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveActivity(ChatroomActivity.class);
            }
        });
    }

    private void moveActivity(Class c){
        Intent intent = new Intent(getContext(), c);
        startActivity(intent);
    }
}
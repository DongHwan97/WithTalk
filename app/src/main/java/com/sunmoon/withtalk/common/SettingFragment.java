package com.sunmoon.withtalk.common;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.sunmoon.withtalk.R;
import com.sunmoon.withtalk.user.LoginActivity;

import java.util.List;

public class SettingFragment extends Fragment {

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_setting, container, false);
        ImageButton logOutButton = (ImageButton)rootView.findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToServer();
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                receiveFromServer();
            }
        });

        return rootView;
    }

    public void sendToServer() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"common\",");
        sb.append("\"method\":\"logout\",");
        sb.append("\"senderId\":\"" + MainActivity.id + "\"");
        sb.append("}");

        ConnectSocket.sendQueue.offer(sb.toString());
    }

    public void receiveFromServer() {
        List<String> lists = ConnectSocket.JsonHandler.messageReceived();

        String status = lists.get(0);
        if ("r200".equals(status)) {
            Util.startToast(getContext(), "로그아웃");
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
    }
}
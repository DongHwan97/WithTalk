package com.sunmoon.withtalk;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ChatroomListFragment extends Fragment {

    public static ChatroomListFragment newInstance() {
        ChatroomListFragment fragment = new ChatroomListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatroom_list, container, false);
    }
}
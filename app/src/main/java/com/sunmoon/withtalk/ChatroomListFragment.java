package com.sunmoon.withtalk;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ChatroomListFragment extends Fragment {

    ViewGroup rootView;
    TextView nameText, dateText;
    View list_layout;
    LinearLayout inflateLayout;

    public static ChatroomListFragment newInstance() {
        ChatroomListFragment fragment = new ChatroomListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_chatroom_list, container, false);
        inflateLayout = (LinearLayout)rootView.findViewById(R.id.chatlist_layout);

        for(int i=0;i<10;i++){
            list_layout = inflater.inflate(R.layout.list_layout,inflateLayout,false);
            list_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveActivity(ChatActivity.class);
                }
            });
            nameText = (TextView)list_layout.findViewById(R.id.nameText);
            dateText = (TextView)list_layout.findViewById(R.id.dateText);
            nameText.setText(Integer.toString(i)+"채팅방");
            dateText.setText(Integer.toString(i)+"날짜");
            inflateLayout.addView(list_layout);
        }


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inflateLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN) {
                        moveActivity(ChatActivity.class);
                }
                return true;
            }
        });
    }
    private void moveActivity(Class c){// String name이름 별로 구별하면서 이름 보내서 텍스트 세팅

        Intent intent = new Intent(getActivity(),c);
        startActivity(intent);
    }
}
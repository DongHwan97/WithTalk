package com.sunmoon.withtalk;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SearchChatRoomActivity extends AppCompatActivity {

    ImageButton searchChatRoomButton;
    EditText searchChatRoomEdit;
    LinearLayout inflateLayout;
    View listLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_chatroom);
        inflateLayout = findViewById(R.id.searchChatRoomListLayout);

        searchChatRoomButton = findViewById(R.id.searchChatRoomButton);
        searchChatRoomEdit = findViewById(R.id.searchChatRoomEdit);


        searchChatRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchChatRoom();
            }
        });
    }

    public void searchChatRoom(){
        inflateLayout.removeView(listLayout);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        searchChatRoomEdit.getText().toString();//검색 데이터 전송 받고

        listLayout =  inflater.inflate(R.layout.chatroomlistlayout, inflateLayout, false);
        TextView ChatRoomNameText = (TextView)listLayout.findViewById(R.id.chatRoomNameText);
        TextView ChatRoomDateText = (TextView)listLayout.findViewById(R.id.chatRoomDateText);
        ChatRoomNameText.setText("채팅방");
        ChatRoomDateText.setText("날짜");

        inflateLayout.addView(listLayout);
    }
}
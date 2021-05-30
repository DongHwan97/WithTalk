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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


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

        inflateLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDialog();
                return true;
            }
        });
    }

    public void searchChatRoom() {
        String chatRoomName = searchChatRoomEdit.getText().toString();

        if (chatRoomName.length() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"type\":\"chatRoom\",");
            sb.append("\"method\":\"searchChatRoom\",");
            sb.append("\"name\":\"" + chatRoomName + "\"");
            sb.append("}");

            ConnectSocket.sendQueue.offer(sb.toString());

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            while (ConnectSocket.receiveQueue.peek() != null) {
                String result = ConnectSocket.receiveQueue.poll(); //내부 디비에서 찾아야하나...

                JsonParser parser = new JsonParser();
                JsonElement json = parser.parse(result);

                String method = json.getAsJsonObject().get("method").toString();
                String status = json.getAsJsonObject().get("status").toString();

                if ("searchChatRoom".equals(method) && "r200".equals(status)) {
                    inflateLayout.removeView(listLayout);
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    listLayout =  inflater.inflate(R.layout.chatroomlistlayout, inflateLayout, false);
                    TextView ChatRoomNameText = (TextView)listLayout.findViewById(R.id.chatRoomNameText);
                    ChatRoomNameText.setText(chatRoomName);

                    inflateLayout.addView(listLayout);

                    break;
                }
            }
        }
    }

    public void showDialog() {

    }
}
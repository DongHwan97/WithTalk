package com.sunmoon.withtalk.chatroom;

import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunmoon.withtalk.R;
import com.sunmoon.withtalk.common.ConnectSocket;
import com.sunmoon.withtalk.friend.FriendList;
import com.sunmoon.withtalk.common.MainActivity;
import com.sunmoon.withtalk.common.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SearchChatRoomActivity extends AppCompatActivity {

    ImageButton searchChatRoomButton;
    EditText searchChatRoomEdit;
    LinearLayout inflateLayout;
    View listLayout;

    LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_chatroom);
        inflateLayout = findViewById(R.id.searchChatRoomListLayout);

        searchChatRoomButton = findViewById(R.id.searchChatRoomButton);
        searchChatRoomEdit = findViewById(R.id.searchChatRoomEdit);

        searchChatRoomEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchChatRoomButton.performClick();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchChatRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateLayout.removeAllViews();

                String chatRoomNameS = searchChatRoomEdit.getText().toString();//검색 데이터 전송 받고
                try {
                    for (String key : ChatRoomList.CHATROOMLIST_ALL.keySet()) {
                        JSONObject jChat = ChatRoomList.CHATROOMLIST_ALL.get(key);

                        String chatRoomName = jChat.getString("chatRoomName");
                        String sendTime = jChat.getString("sendTime");
                        if (chatRoomName.contains(chatRoomNameS)) {

                            JSONArray memberIdList = jChat.getJSONArray("memberIdList");

                            inflateLayout.removeView(listLayout);
                            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                            listLayout = inflater.inflate(R.layout.chatroomlistlayout, inflateLayout, false);
                            TextView chatRoomNameText = (TextView) listLayout.findViewById(R.id.chatRoomNameText);
                            TextView chatRoomDateText = (TextView) listLayout.findViewById(R.id.chatRoomDateText);

                            chatRoomDateText.setText(sendTime);
                            chatRoomNameText.setText(chatRoomName);

                            /*if (jChat.getString("chatRoomType").equals("DM")) {
                                chatRoomDateText.setText("");
                            } else {
                                chatRoomDateText.setText(""+memberIdList.length());
                            }*/

                            listLayout.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    showDialog(key);
                                    return true;
                                }
                            });
                            listLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    moveChatRoom(chatRoomName, key);
                                }
                            });

                            inflateLayout.addView(listLayout);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        this.mInflater = getLayoutInflater();
    }

    public void moveChatRoom(String friendName, String chatRoomNo){
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("friendName", friendName);
        FriendList.chatRoomNo = chatRoomNo;
        startActivity(intent);
    }

    public void showDialog(String chatRoomNo) {
        final CharSequence[] items = {"대화방 이름 변경", "대화방 나가기"};
        AlertDialog.Builder chatRoomBuilder = new AlertDialog.Builder(this);
        chatRoomBuilder.setTitle("대화방 관리");
        chatRoomBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("onClick: ", Integer.toString(which) + "입니다");
                switch (which){
                    case 0:
                        changeChatRoomName(chatRoomNo);
                        break;

                    case 1:
                        sendExitChatRoom(chatRoomNo);
                        break;
                }
            }
        });
        chatRoomBuilder.show();
    }

    TextView listNameText;
    public void changeChatRoomName(String chatRoomNo){
        final EditText editText = new EditText(this);
        AlertDialog.Builder changeChatRoomNameDialog = new AlertDialog.Builder(this);
        changeChatRoomNameDialog.setTitle("대화방 이름 변경");
        changeChatRoomNameDialog.setView(editText);
        changeChatRoomNameDialog.setPositiveButton("입력", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listNameText.setText(editText.getText().toString());
                Util.startToast(getApplicationContext(),"변경되었습니다");
            }
        });
        changeChatRoomNameDialog.show();
    }

    public void sendExitChatRoom(String chatRoomNo) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"" + "chatRoom" + "\",");
        sb.append("\"method\":\"" + "exit" + "\",");
        sb.append("\"senderId\":\"" + MainActivity.id + "\",");
        sb.append("\"chatRoomNo\":\"" + chatRoomNo + "\"");
        sb.append("}");
        ConnectSocket.sendQueue.offer((sb.toString()));
    }


}
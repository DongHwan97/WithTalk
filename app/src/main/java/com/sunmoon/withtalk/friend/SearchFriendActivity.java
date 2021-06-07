package com.sunmoon.withtalk.friend;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunmoon.withtalk.R;
import com.sunmoon.withtalk.chatroom.ChatActivity;
import com.sunmoon.withtalk.chatroom.ChatRoomList;
import com.sunmoon.withtalk.common.ConnectSocket;
import com.sunmoon.withtalk.common.MainActivity;
import com.sunmoon.withtalk.common.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchFriendActivity extends AppCompatActivity {

    ImageButton searchFriendButton;
    EditText searchFriendEdit;
    LinearLayout inflateLayout;
    View listLayout;

    LayoutInflater mInfater;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        inflateLayout = findViewById(R.id.searchFriendLayout);
        searchFriendButton = findViewById(R.id.searchFriendButton);
        searchFriendEdit = findViewById(R.id.searchFriendEdit);
        mInfater = getLayoutInflater();

        searchFriendEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchFriendButton.performClick();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateLayout.removeAllViews();

                String friendName = searchFriendEdit.getText().toString();//검색 데이터 전송 받고
                for (String key : FriendList.FRIEND_LIST.keySet()) {
                    if (FriendList.FRIEND_LIST.get(key).contains(friendName)) {
                        String friendId = key;
                        String name = FriendList.FRIEND_LIST.get(key);

                        listLayout = mInfater.inflate(R.layout.friendlistlayout, inflateLayout, false);
                        TextView friendNameText = (TextView) listLayout.findViewById(R.id.friendNameText);
                        friendNameText.setText(name);
                        inflateLayout.addView(listLayout);
                        listLayout.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                showDialog(friendId, name);
                                return true;
                            }
                        });
                    }
                }
            }
        });

        this.mContext = getApplicationContext();
        messaging.start();
    }

    Thread messaging = new Thread(new Runnable() {
        @Override
        public void run() {
            String receivedMessage;

            try {while(!Thread.currentThread().isInterrupted()) {
                receivedMessage = ConnectSocket.receiveQueue.peek();

                Thread.sleep(100);
                if (receivedMessage != null) {
                    JSONObject jsonObject = new JSONObject(receivedMessage);

                    if (jsonObject.getString("method").equals("delete")) {
                        ConnectSocket.receiveQueue.poll();
                        if (jsonObject.getString("status").equals("r200")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refreshFriendList();
                                    Util.startToast(mContext, "삭제되었습니다.");
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Util.startToast(mContext, "실패했습니다.");
                                }
                            });
                        }


                    }
                }
            }} catch (JSONException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    });

    public void showDialog(String friendId, String friendName) {
        final CharSequence[] items = {"1:1 대화", "친구 삭제"};
        AlertDialog.Builder friendBuilder = new AlertDialog.Builder(this);
        friendBuilder.setTitle("친구 관리");
        friendBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        moveChatRoom(friendName, friendId);
                        break;
                    case 1:
                        sendDeleteFriend(friendId);
                        break;
                }
            }
        });

        friendBuilder.show();
    }

    public void moveChatRoom(String friendName, String friendId) {
        Intent intent = new Intent(this, ChatActivity.class);
        if(ChatRoomList.CHATROOMLIST_DM.get(friendId)==null){
            createChatRoom(friendId);
        }
        if(ChatRoomList.CHATROOMLIST_DM.get(friendId)==null){
            return;
        }
        intent.putExtra("friendName", friendName);
        intent.putExtra("friendId", friendId);
        FriendList.chatRoomNo=ChatRoomList.CHATROOMLIST_DM.get(friendId);
        startActivity(intent);
    }

    public void createChatRoom(String friendId){
        ArrayList<String> list = new ArrayList<>();
        list.add("\""+MainActivity.id+"\"");
        list.add("\""+friendId+"\"");

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"" + "chatRoom" + "\",");
        sb.append("\"method\":\"" + "create" + "\",");
        sb.append("\"senderId\":\"" + MainActivity.id + "\",");
        sb.append("\"receiverId\":" + list + ",");
        sb.append("\"chatRoomName\":" + null + ",");
        sb.append("\"chatRoomType\":\"" + "DM" + "\"");
        sb.append("}");
        ConnectSocket.sendQueue.offer((sb.toString()));

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String result = ConnectSocket.receiveQueue.poll();

        try {
            JSONObject json = new JSONObject(result);
            String method = json.getString("method");
            String status = json.getString("status");
            if ("create".equals(method)&&"r200".equals(status)) {
                int chatRoomNo = json.getInt("chatRoomNo");
                ChatRoomList.CHATROOMLIST_DM.put(friendId,(""+chatRoomNo));
                Util.startToast(this,"대화방생성");

            }else{
                Util.startToast(this,"대화방생성 실패");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void refreshFriendList() {
        sendSelectAllFriend();
    }

    public void sendSelectAllFriend() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"friend\",");
        sb.append("\"method\":\"selectAllFriend\",");
        sb.append("\"id\":\"" + MainActivity.id + "\"");
        sb.append("}");

        ConnectSocket.sendQueue.offer((sb.toString()));
    }

    public void sendDeleteFriend(String friendId) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"friend\",");
        sb.append("\"method\":\"delete\",");
        sb.append("\"memberId\":\"" + MainActivity.id + "\",");
        sb.append("\"friendId\":\"" + friendId + "\"");
        sb.append("}");
        ConnectSocket.sendQueue.offer((sb.toString()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        messaging.interrupt();
    }
}

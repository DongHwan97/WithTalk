package com.sunmoon.withtalk.chatroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunmoon.withtalk.R;
import com.sunmoon.withtalk.common.ChatRoomList;
import com.sunmoon.withtalk.common.ConnectSocket;
import com.sunmoon.withtalk.common.DataAdapter;
import com.sunmoon.withtalk.common.Friend;
import com.sunmoon.withtalk.common.FriendList;
import com.sunmoon.withtalk.common.JsonHandler;
import com.sunmoon.withtalk.common.MainActivity;
import com.sunmoon.withtalk.common.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InviteFriendActivity extends AppCompatActivity {

    ImageButton inviteSearchFriendButton;
    EditText inviteFriendEdit;
    LinearLayout inflateLayout;
    View listLayout;
    LayoutInflater inflater;
    CheckBox inviteCheckBox;
    TextView friendNameText;
    Button inviteButton;
    ArrayList<String> friendList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);

        inflateLayout = findViewById(R.id.searchInviteFriendLayout);

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inviteSearchFriendButton = findViewById(R.id.inviteSearchFriendButton);
        inviteFriendEdit = findViewById(R.id.inviteFriendEdit);
        String friendName;

        for(String key : FriendList.FRIEND_LIST.keySet()){
            friendName = FriendList.FRIEND_LIST.get(key);
            Log.e("onCreate: ","이름: "+ friendName);
            listLayout = inflater.inflate(R.layout.invitefriendlayout, inflateLayout, false);
            friendNameText = (TextView) listLayout.findViewById(R.id.inviteFriendNameText);
            inviteCheckBox = (CheckBox) listLayout.findViewById(R.id.inviteCheckBox);
            friendNameText.setText(friendName);

            inflateLayout.addView(listLayout);
        }

        inviteSearchFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateLayout.removeAllViews();
                String friendName = inviteFriendEdit.getText().toString();//검색 데이터 전송 받고
                if ((friendName.length() > 0)) {
                    sendSearchFriend(MainActivity.id, friendName);
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    receiveSearchFriend();
                } else {
                    Util.startToast(getApplicationContext(), "이름을 입력해 주세요");
                }
            }
        });

        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Onclick", "onClick: "+friendList);
            }
        });

        inviteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                friendList = new ArrayList();
                if(inviteCheckBox.isChecked()){
                    friendList.add(friendNameText.getText().toString());
                }
            }
        });

    }

    public void sendSearchFriend(String senderId, String friendName) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"friend\",");
        sb.append("\"method\":\"searchRegistFriend\",");
        sb.append("\"senderId\":\"" + senderId + "\",");
        sb.append("\"searchName\":\"" + friendName + "\"");
        sb.append("}");

        ConnectSocket.sendQueue.offer((sb.toString()));
    }

    public void receiveSearchFriend() {
        ArrayList<String> lists = JsonHandler.messageReceived();
        String status = lists.get(0);

        if ("r200".equals(status)) {
            for (int i = 1; i < lists.size(); i = i + 2) {
                String friendId = lists.get(i);
                String name = lists.get(i + 1);
                Log.d( "receiveSearchFriend: ",  name);
                listLayout = inflater.inflate(R.layout.invitefriendlayout, inflateLayout, false);
                friendNameText = (TextView) listLayout.findViewById(R.id.inviteFriendNameText);
                inviteCheckBox = (CheckBox) listLayout.findViewById(R.id.inviteCheckBox);
                friendNameText.setText(name);

                inflateLayout.addView(listLayout);

            }
        } else {
            Util.startToast(getApplicationContext(), "해당하는 친구가 존재하지 않습니다.");
        }
    }


    public void createChatRoom(ArrayList<String> list){
        ArrayList<String> receiverId = new ArrayList<>();
        receiverId.add("\""+MainActivity.id+"\"");
        receiverId.add("\""+list+"\"");

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"" + "chatRoom" + "\",");
        sb.append("\"method\":\"" + "create" + "\",");
        sb.append("\"senderId\":\"" + MainActivity.id + "\",");
        sb.append("\"receiverId\":" + receiverId + ",");
        sb.append("\"chatRoomName\":" + null + ",");
        sb.append("\"chatRoomType\":\"" + "DM" + "\"");
        sb.append("}");
        ConnectSocket.sendQueue.offer((sb.toString()));

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 결과 받기
        String result = ConnectSocket.receiveQueue.poll();
        Log.e("asd", "createChatRoom: "+result );
        try {
            JSONObject json = new JSONObject(result);
            String method = json.getString("method");
            String status = json.getString("status");
            if ("create".equals(method)&&"r200".equals(status)) {
                int chatRoomNo = json.getInt("chatRoomNo");
                //ChatRoomList.CHATROOMLIST.put(friendId,(""+chatRoomNo));
                Log.e("asd", "createChatRoom: "+chatRoomNo );
                Util.startToast(this,"대화방생성");

            }else{
                Util.startToast(this,"대화방생성 실패");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
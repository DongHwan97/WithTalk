package com.sunmoon.withtalk.friend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunmoon.withtalk.common.ChatRoomList;
import com.sunmoon.withtalk.common.JsonHandler;
import com.sunmoon.withtalk.R;
import com.sunmoon.withtalk.chatroom.ChatActivity;
import com.sunmoon.withtalk.common.ConnectSocket;
import com.sunmoon.withtalk.common.Friend;
import com.sunmoon.withtalk.common.FriendList;
import com.sunmoon.withtalk.common.MainActivity;
import com.sunmoon.withtalk.common.Util;
import com.sunmoon.withtalk.common.DataAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendListFragment extends Fragment {
    ViewGroup rootView;
    TextView friendNameText;
    View list_layout;
    LinearLayout inflateLayout;
    ImageButton moveSearchFriendButton, moveAddFriendButton, refreshButton;

    public static FriendListFragment newInstance() {
        FriendListFragment fragment = new FriendListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_friend_list, container, false);
        inflateLayout = (LinearLayout) rootView.findViewById(R.id.friend_layout);

        moveSearchFriendButton = (ImageButton) rootView.findViewById(R.id.moveSearchFriendButton);
        moveAddFriendButton = (ImageButton) rootView.findViewById(R.id.moveAddFriendButton);
        refreshButton = (ImageButton) rootView.findViewById(R.id.friendListRefreshButton);

        sendSelectAllFriend();
        try {
            Thread.sleep(300);
        } catch (Exception e) {
            e.printStackTrace();
        }
        receiveSelectAllFriend(inflater);


        return rootView;
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

    public void receiveSelectAllFriend(LayoutInflater inflater) {
        ArrayList<String> lists = JsonHandler.messageReceived();
        String status = lists.get(0);

        if ("r200".equals(status) && lists.size() != 1) {
            for (int i = 1; i < lists.size(); i = i + 2) {
                String friendId = lists.get(i);
                String name = lists.get(i + 1);

                FriendList.FRIEND_LIST.put(friendId, name);
                list_layout = inflater.inflate(R.layout.friendlistlayout, inflateLayout, false);
                friendNameText = (TextView) list_layout.findViewById(R.id.friendNameText);
                friendNameText.setText(name);
                list_layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showDialog(friendId, name);
                        return true;
                    }
                });

                inflateLayout.addView(list_layout);
            }
        } else {
            Util.startToast(getContext(), "새로운 친구를 추가해주세요!");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        moveSearchFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveActivity(SearchFriendActivity.class);
            }
        });

        moveAddFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveActivity(AddFriendActivity.class);
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
    }

    public void showDialog(String friendId, String friendName) {
        final CharSequence[] items = {"1:1 대화", "친구 삭제"};
        AlertDialog.Builder friendBuilder = new AlertDialog.Builder(getContext());
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

                        try {
                            Thread.sleep(300);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        receiveDeleteFriend(friendId);
                        break;
                }
            }
        });

        friendBuilder.show();
    }

    public void moveChatRoom(String friendName, String friendId) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        if(ChatRoomList.CHATROOMLIST.get(friendId)==null){
            createChatRoom(friendId);
        }
        if(ChatRoomList.CHATROOMLIST.get(friendId)==null){
            return;
        }
        intent.putExtra("friendName", friendName);
        intent.putExtra("friendId", friendId);
        intent.putExtra("chatRoomNo", ChatRoomList.CHATROOMLIST.get(friendId));
        Log.e( "moveChatRoom: ", "로그입니당"+ChatRoomList.CHATROOMLIST.get(friendId));
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
            Thread.sleep(300);
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
                ChatRoomList.CHATROOMLIST.put(friendId,(""+chatRoomNo));
                Log.e("asd", "createChatRoom: "+chatRoomNo );
                Util.startToast(getContext(),"대화방생성");

            }else{
                Util.startToast(getContext(),"대화방생성 실패");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendDeleteFriend(String friendId) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"" + "friend" + "\",");
        sb.append("\"method\":\"" + "delete" + "\",");
        sb.append("\"memberId\":\"" + MainActivity.id + "\",");
        sb.append("\"friendId\":\"" + friendId + "\"");
        sb.append("}");
        Log.d("Friend2", friendId);

        ConnectSocket.sendQueue.offer((sb.toString()));
    }

    public void receiveDeleteFriend(String friendId) {
        ArrayList<String> lists = JsonHandler.messageReceived();
        String status = lists.get(0);

        if ("r200".equals(status)) {

            Util.startToast(getContext(), friendId + "가 삭제되었습니다.");
            Log.d("Friend4", friendId);
            refresh();
        } else {
            Util.startToast(getContext(), "실패했습니다.");
        }
    }

    public void refresh() {
        inflateLayout.removeAllViews();

        sendSelectAllFriend();
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<String> lists = JsonHandler.messageReceived();
        String status = lists.get(0);

        if ("r200".equals(status) && lists.size() != 1) {
            for (int i = 1; i < lists.size(); i = i + 2) {
                String friendId = lists.get(i);
                String name = lists.get(i + 1);

                LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
                list_layout = inflater.inflate(R.layout.friendlistlayout, inflateLayout, false);
                friendNameText = (TextView) list_layout.findViewById(R.id.friendNameText);
                friendNameText.setText(name);
                list_layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showDialog(friendId, name);
                        return true;
                    }
                });
                inflateLayout.addView(list_layout);
            }
        } else {
            Util.startToast(getContext(), "새로운 친구를 등록해주세요!");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void moveActivity(Class c) {
        Intent intent = new Intent(getContext(), c);
        startActivity(intent);
    }
}
package com.sunmoon.withtalk.friend;

import android.app.AlertDialog;
import android.content.Context;
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

import com.sunmoon.withtalk.chatroom.ChatRoomList;
import com.sunmoon.withtalk.R;
import com.sunmoon.withtalk.chatroom.ChatActivity;
import com.sunmoon.withtalk.common.ConnectSocket;
import com.sunmoon.withtalk.common.MainActivity;
import com.sunmoon.withtalk.common.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FriendListFragment extends Fragment {
    ViewGroup rootView;
    TextView friendNameText;
    View list_layout;
    LinearLayout inflateLayout;
    ImageButton moveSearchFriendButton, moveAddFriendButton, refreshButton;
    
    Context mContext;
    LayoutInflater mInflater;

    public static FriendListFragment newInstance() {
        FriendListFragment fragment = new FriendListFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mContext = getContext();
        messaging.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_friend_list, container, false);
        inflateLayout = (LinearLayout) rootView.findViewById(R.id.friend_layout);

        this.mInflater = inflater;

        moveSearchFriendButton = (ImageButton) rootView.findViewById(R.id.moveSearchFriendButton);
        moveAddFriendButton = (ImageButton) rootView.findViewById(R.id.moveAddFriendButton);
        refreshButton = (ImageButton) rootView.findViewById(R.id.friendListRefreshButton);

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
                refreshFriendList();
            }
        });
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

                    if (jsonObject.getString("method").equals("selectAllFriend")) {
                        ConnectSocket.receiveQueue.poll();
                        if (jsonObject.getString("status").equals("r200")) {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                    inflateLayout.removeAllViews();
                                    FriendList.FRIEND_LIST.clear();

                                    String friendList = jsonObject.getString("friendList");
                                    JSONArray jfriendArray =  new JSONArray(friendList);

                                    JSONObject jfriend;

                                    for (int i = 0; i < jfriendArray.length(); i++) {
                                        jfriend = (JSONObject) jfriendArray.get(i);

                                        String name = jfriend.getString("name");
                                        String friendId = jfriend.getString("id");

                                        FriendList.FRIEND_LIST.put(friendId, name);

                                        list_layout = mInflater.inflate(R.layout.friendlistlayout, inflateLayout, false);
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
                                    }} catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            Util.startToast(getContext(), "검색된 친구가 없습니다.");
                        }
                    }
                    else if (jsonObject.getString("method").equals("delete")) {
                        ConnectSocket.receiveQueue.poll();
                        if (jsonObject.getString("status").equals("r200")) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Util.startToast(getContext(), "삭제되었습니다.");
                                }
                            });

                            refreshFriendList();
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Util.startToast(getContext(), "실패했습니다.");
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
                        break;
                }
            }
        });
        friendBuilder.show();
    }

    public void moveChatRoom(String friendName, String friendId) {

        if(ChatRoomList.CHATROOMLIST_DM.get(friendId)==null){
            createChatRoom(friendId);

        }
        else {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("friendName", friendName);
            intent.putExtra("friendId", friendId);
            FriendList.chatRoomNo=ChatRoomList.CHATROOMLIST_DM.get(friendId);
            startActivity(intent);
        }
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

    }
    
    public void sendDeleteFriend(String friendId) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"" + "friend" + "\",");
        sb.append("\"method\":\"" + "delete" + "\",");
        sb.append("\"memberId\":\"" + MainActivity.id + "\",");
        sb.append("\"friendId\":\"" + friendId + "\"");
        sb.append("}");

        ConnectSocket.sendQueue.offer((sb.toString()));
    }

    public void refreshFriendList() {
        sendSelectAllFriend();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshFriendList();
    }

    private void moveActivity(Class c) {
        Intent intent = new Intent(getContext(), c);
        startActivity(intent);
    }
}
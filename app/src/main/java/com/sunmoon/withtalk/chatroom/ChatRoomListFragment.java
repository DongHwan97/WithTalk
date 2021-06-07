package com.sunmoon.withtalk.chatroom;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunmoon.withtalk.common.ConnectSocket;
import com.sunmoon.withtalk.friend.FriendList;
import com.sunmoon.withtalk.common.MainActivity;
import com.sunmoon.withtalk.R;
import com.sunmoon.withtalk.common.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ChatRoomListFragment extends Fragment {

    ViewGroup rootView;
    TextView listNameText, listDateText, countText;
    View list_layout;
    LinearLayout inflateLayout;
    ImageButton moveSearchChatRoom, refreshButton;

    LayoutInflater mInflater;

    Context mContext;

    public static ChatRoomListFragment newInstance() {
        ChatRoomListFragment fragment = new ChatRoomListFragment();
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
        this.mInflater = inflater;
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_chatroom_list, container, false);
        inflateLayout = (LinearLayout)rootView.findViewById(R.id.chatlist_layout);

        moveSearchChatRoom = (ImageButton)rootView.findViewById(R.id.moveSearchChatRoom);
        refreshButton = (ImageButton) rootView.findViewById(R.id.chatRoomListRefreshButton);


        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshChatRoomList();
            }
        });

        refreshButton.performClick();

        return rootView;
    }

    public void sendSelectAllChatRoom() {
        StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"type\":\"" + "chatRoom" + "\",")
                .append("\"method\":\"" + "selectAllChatRoom" + "\",")
                .append("\"id\":\"").append(MainActivity.id)
                .append("\"")
                .append("}");

        ConnectSocket.sendQueue.offer((sb.toString()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        moveSearchChatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveActivity(SearchChatRoomActivity.class);
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

                    if (jsonObject.getString("method").equals("selectAllChatRoom")) {
                        ConnectSocket.receiveQueue.poll();
                        if (jsonObject.getString("status").equals("r200")) {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        inflateLayout.removeAllViews();
                                        ChatRoomList.CHATROOMLIST_DM.clear();
                                        ChatRoomList.CHATROOMLIST_ALL.clear();

                                        JSONArray jChatRoomArray = jsonObject.getJSONArray("chatRoomList");

                                        JSONObject jChatRoom;

                                        for (int i = 0; i < jChatRoomArray.length(); i++) {
                                            jChatRoom = (JSONObject) jChatRoomArray.get(i);

                                            String chatRoomNo = jChatRoom.getString("chatRoomNo");
                                            String chatRoomName = jChatRoom.getString("chatRoomName");
                                            JSONArray memberIdList = jChatRoom.getJSONArray("memberIdList");
                                            String sendTime = jChatRoom.getString("sendTime");


                                            list_layout = mInflater.inflate(R.layout.chatroomlistlayout, inflateLayout, false);
                                            listDateText = (TextView) list_layout.findViewById(R.id.chatRoomDateText);
                                            listNameText = (TextView) list_layout.findViewById(R.id.chatRoomNameText);
                                            //countText = (TextView) list_layout.findViewById(R.id.chatRoomCountText);
                                            listDateText.setText(sendTime);
                                            listNameText.setText(chatRoomName);

                                            if (jChatRoom.getString("chatRoomType").equals("DM")) {
                                                //countText.setText("");
                                                ChatRoomList.CHATROOMLIST_DM.put(chatRoomName, chatRoomNo);

                                            } else {
                                                //countText.setText(""+memberIdList.length());
                                            }

                                            ChatRoomList.CHATROOMLIST_ALL.put(chatRoomNo, jChatRoom);

                                            list_layout.setOnLongClickListener(new View.OnLongClickListener() {
                                                @Override
                                                public boolean onLongClick(View v) {
                                                    showDialog(chatRoomNo);
                                                    return true;
                                                }
                                            });

                                            list_layout.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    moveChatRoom(chatRoomName, chatRoomNo);
                                                }
                                            });

                                            inflateLayout.addView(list_layout);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                    else if (jsonObject.getString("method").equals("exit")) {
                        ConnectSocket.receiveQueue.poll();
                        if (jsonObject.getString("status").equals("r200")) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Util.startToast(getContext(), "삭제되었습니다.");
                                }
                            });

                            refreshChatRoomList();
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Util.startToast(getContext(), "실패했습니다.");
                                }
                            });
                        }
                    }
                    else if (jsonObject.getString("method").equals("create")) {
                        ConnectSocket.receiveQueue.poll();
                        if (jsonObject.getString("status").equals("r200")) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        int chatRoomNo = jsonObject.getInt("chatRoomNo");
                                        if (jsonObject.getString("senderId").equals(MainActivity.id)) {
                                            Intent intent = new Intent(getContext(), ChatActivity.class);

                                            intent.putExtra("friendName", jsonObject.getString("chatRoomName"));

                                            FriendList.chatRoomNo = ""+chatRoomNo;
                                            startActivity(intent);
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    refreshChatRoomList();
                                }
                            });

                            refreshChatRoomList();
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

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

    public void showDialog(String chatRoomNo){
        final CharSequence[] items = {"대화방 이름 변경", "대화방 나가기"};
        AlertDialog.Builder chatRoomBuilder = new AlertDialog.Builder(getActivity());
        chatRoomBuilder.setTitle("대화방 관리");
        chatRoomBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    public void changeChatRoomName(String chatRoomNo){
        final EditText editText = new EditText(getContext());
        AlertDialog.Builder changeChatRoomNameDialog = new AlertDialog.Builder(getContext());
        changeChatRoomNameDialog.setTitle("대화방 이름 변경");
        changeChatRoomNameDialog.setView(editText);
        changeChatRoomNameDialog.setPositiveButton("입력", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listNameText.setText(editText.getText().toString());
                Util.startToast(getContext(),"변경되었습니다");
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

    public void moveChatRoom(String friendName, String chatRoomNo){
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("friendName", friendName);
        FriendList.chatRoomNo = chatRoomNo;
        startActivity(intent);
    }

    private void moveActivity(Class c){// String name이름 별로 구별하면서 이름 보내서 텍스트 세팅
        Intent intent = new Intent(getActivity(),c);
        startActivity(intent);
    }



    public void refreshChatRoomList(){
        sendSelectAllChatRoom();
    }

}
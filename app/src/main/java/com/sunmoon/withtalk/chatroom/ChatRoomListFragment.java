package com.sunmoon.withtalk.chatroom;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunmoon.withtalk.common.ConnectSocket;
import com.sunmoon.withtalk.common.MainActivity;
import com.sunmoon.withtalk.R;
import com.sunmoon.withtalk.common.Util;

import java.util.ArrayList;


public class ChatRoomListFragment extends Fragment {

    ViewGroup rootView;
    TextView listNameText, listDateText, countText;
    View list_layout;
    LinearLayout inflateLayout;
    ImageButton moveSearchChatRoom, refreshButton;

    public static ChatRoomListFragment newInstance() {
        ChatRoomListFragment fragment = new ChatRoomListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_chatroom_list, container, false);
        inflateLayout = (LinearLayout)rootView.findViewById(R.id.chatlist_layout);

        moveSearchChatRoom = (ImageButton)rootView.findViewById(R.id.moveSearchChatRoom);
        refreshButton = (ImageButton) rootView.findViewById(R.id.chatRoomListRefreshButton);
        sendSelectAllChatRoom();

        try {
            Thread.sleep(300);
        } catch (Exception e) {
            e.printStackTrace();
        }

        receiveSelectAllChatRoom(inflater);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh(inflater);
            }
        });

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

    public void receiveSelectAllChatRoom(LayoutInflater inflater) {
        ArrayList<String> lists = ConnectSocket.JsonHandler.messageReceived();

        String status = lists.get(0);

        if ("r200".equals(status)) {
            for (int i = 1; i < lists.size(); i = i + 3) {
                String chatRoomNo = lists.get(i);
                String memberList = lists.get(i + 1);
                String count = lists.get(i+2);

                list_layout = inflater.inflate(R.layout.chatroomlistlayout,inflateLayout,false);
                listNameText = (TextView)list_layout.findViewById(R.id.chatRoomNameText);
                listDateText = (TextView)list_layout.findViewById(R.id.chatRoomDateText);
                countText = (TextView)list_layout.findViewById(R.id.chatRoomCountText);

                listNameText.setText(memberList);
                listDateText.setText("00:00");
                if(count.length()==2){
                    countText.setText("");
                }else{
                    countText.setText(count);
                }

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
                        moveChatRoom(memberList.toString(), chatRoomNo);
                    }
                });
                inflateLayout.addView(list_layout);
            }
        }
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

    public void showDialog(String chatRoomNo){
        final CharSequence[] items = {"대화방 이름 변경", "대화방 나가기"};
        AlertDialog.Builder chatRoomBuilder = new AlertDialog.Builder(getActivity());
        chatRoomBuilder.setTitle("대화방 관리");
        chatRoomBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("onClick: ", Integer.toString(which) + "입니다");
                switch (which){
                    case 0: changeChatRoomName(chatRoomNo);
                        break;
                    case 1:
                        sendExitChatRoom(chatRoomNo);
                        try {
                            Thread.sleep(300);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        receiveDeleteChatRoom();
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

    public void receiveDeleteChatRoom() {
        ArrayList<String> lists = ConnectSocket.JsonHandler.messageReceived();

        String status = lists.get(0);

        if ("r200".equals(status)) {
            Util.startToast(getContext(), "대화방 나가기 성공");

        } else {
            Util.startToast(getContext(), "실패했습니다.");
        }
    }

    public void refresh(LayoutInflater inflater){
        inflateLayout.removeAllViews();
        sendSelectAllChatRoom();
        try {
            Thread.sleep(300);
        } catch (Exception e) {
            e.printStackTrace();
        }
        receiveSelectAllChatRoom(inflater);
    }



    public void moveChatRoom(String friendList,String chatRoomId){
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("friendList", friendList);
        intent.putExtra("chatRoomId", chatRoomId);
        startActivity(intent);
    }

    private void moveActivity(Class c){// String name이름 별로 구별하면서 이름 보내서 텍스트 세팅

        Intent intent = new Intent(getActivity(),c);
        startActivity(intent);
    }
}
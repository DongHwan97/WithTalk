package com.sunmoon.withtalk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class ChatRoomListFragment extends Fragment {

    ViewGroup rootView;
    TextView listNameText, listDateText;
    View list_layout;
    LinearLayout inflateLayout;
    ImageButton moveSearchChatRoom;

    public static ChatRoomListFragment newInstance() {
        ChatRoomListFragment fragment = new ChatRoomListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_chatroom_list, container, false);
        inflateLayout = (LinearLayout)rootView.findViewById(R.id.chatlist_layout);

        moveSearchChatRoom = (ImageButton)rootView.findViewById(R.id.moveSearchChatRoom);
        for(int i=0;i<10;i++){
            list_layout = inflater.inflate(R.layout.chatroomlistlayout,inflateLayout,false);
            listNameText = (TextView)list_layout.findViewById(R.id.chatRoomNameText);
            listDateText = (TextView)list_layout.findViewById(R.id.chatRoomDateText);
            listNameText.setText(Integer.toString(i)+"채팅방");
            listDateText.setText(Integer.toString(i)+"날짜");
            inflateLayout.addView(list_layout);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        inflateLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDialog();
                return true;
            }
        });

        inflateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveActivity(ChatActivity.class);
            }
        });

        moveSearchChatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveActivity(SearchChatRoomActivity.class);
            }
        });
    }

    public void showDialog(){
        final CharSequence[] items = {"대화방 이름 변경", "대화방 나가기"};
        AlertDialog.Builder chatRoomBuilder = new AlertDialog.Builder(getActivity());
        chatRoomBuilder.setTitle("대화방 관리");
        chatRoomBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0: changeChatRoomName();
                        break;
                    case 1: deleteChatRoom();
                        break;
                }
            }
        });
        chatRoomBuilder.show();
    }
    public void changeChatRoomName(){
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
    public void deleteChatRoom(){

    }
    private void moveActivity(Class c){// String name이름 별로 구별하면서 이름 보내서 텍스트 세팅

        Intent intent = new Intent(getActivity(),c);
        startActivity(intent);
    }

}
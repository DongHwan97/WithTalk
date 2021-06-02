package com.sunmoon.withtalk;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


public class ChatRoomListFragment extends Fragment {

    ViewGroup rootView;
    TextView listNameText, listDateText, countText;
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


        for (String key : FriendList.FRIEND_LIST.keySet()) {
            Log.d("adfa", "key : " + key + " value : " + FriendList.FRIEND_LIST.get(key));
        }


        StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"type\":\"" + "chatRoom" + "\",")
                .append("\"method\":\"" + "selectAllChatRoom" + "\",")
                .append("\"id\":\"").append(MainActivity.id)
                .append("\"")
                .append("}");

        ConnectSocket.sendQueue.offer((sb.toString()));

        try {
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String result = ConnectSocket.receiveQueue.poll();

        try {
            JSONObject json = new JSONObject(result);
            JSONArray jsonArray = json.getJSONArray("chatRoomList");
            String method = json.getString("method");
            String status = json.getString("status");

            for(int i=0; i<jsonArray.length();i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                String chatRoomNo = obj.getString("chatRoomNo");
                JSONArray memberIdList = obj.getJSONArray("memberIdList");
                String chatRoomType = obj.getString("chatRoomType");

                StringBuilder memberList_str = new StringBuilder();

                for (int j=0; j<memberIdList.length();j++){
                    if(!MainActivity.id.equals(memberIdList.getString(j))){
                        memberList_str.append(FriendList.FRIEND_LIST.get(memberIdList.getString(j)));
                        if (j < memberIdList.length()-1) {
                            memberList_str.append(", ");
                        }
                    }
                }


                if ("selectAllChatRoom".equals(method) && "r200".equals(status)) {
                    list_layout = inflater.inflate(R.layout.chatroomlistlayout,inflateLayout,false);
                    listNameText = (TextView)list_layout.findViewById(R.id.chatRoomNameText);
                    listDateText = (TextView)list_layout.findViewById(R.id.chatRoomDateText);
                    countText = (TextView)list_layout.findViewById(R.id.chatRoomCountText);
                    listNameText.setText(memberList_str.toString());
                    listDateText.setText("00:00");
                    if(memberIdList.length()==2){
                        countText.setText("");
                    }else{
                        countText.setText(Integer.toString(memberIdList.length()));
                    }
                    list_layout.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            showDialog();
                            return true;
                        }
                    });
                    list_layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            moveChatRoom(memberList_str.toString(), chatRoomNo);
                        }
                    });
                    inflateLayout.addView(list_layout);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rootView;
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
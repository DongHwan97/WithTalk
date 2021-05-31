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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
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


        moveSearchFriend = (ImageButton) rootView.findViewById(R.id.moveSearchFriend);
        moveAddFriend = (ImageButton) rootView.findViewById(R.id.moveAddFriend);

        sendToFriendList();
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        list_layout = inflater.inflate(R.layout.friendlistlayout, inflateLayout, false);
        receiveFromFriendList();

        inflateLayout.addView(list_layout);

        return rootView;
    }

        moveSearchFriendButton = (ImageButton)rootView.findViewById(R.id.moveSearchFriendButton);
        moveAddFriendButton = (ImageButton)rootView.findViewById(R.id.moveAddFriendButton);
        refreshButton = (ImageButton)rootView.findViewById(R.id.refreshButton);


    public void sendToFriendList() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"" + "friend" + "\",");
        sb.append("\"method\":\"" + "selectAllFriend" + "\",");
        sb.append("\"id\":\"" + MainActivity.id + "\"");
        sb.append("}");

        ConnectSocket.sendQueue.offer((sb.toString()));

    }

    public void receiveFromFriendList() {
        List<String> lists = JsonHandler.messageReceived();

        Log.d("---------", lists.toString());
        String status = lists.get(0);

        if ("r200".equals(status)) {
            for (int i = 1; i < lists.size(); i = i + 2) {
                friendNameText = (TextView) list_layout.findViewById(R.id.friendNameText);
                String name = lists.get(i);
                String friendId = lists.get(i + 1);

                friendNameText.setText(name);
                list_layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showDialog(name, friendId);
                        return true;
                    }
                });
=======
        try {
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String result = ConnectSocket.receiveQueue.poll();

        try {
            JSONObject json = new JSONObject(result);
            JSONArray jsonArray = json.getJSONArray("friendList");
            String method = json.getString("method");
            String status = json.getString("status");
            for(int i=0; i<jsonArray.length();i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                String name = obj.getString("name");
                String friendId = obj.getString("id");

                if ("selectAllFriend".equals(method) && "r200".equals(status)) {
                    list_layout = inflater.inflate(R.layout.friendlistlayout,inflateLayout,false);
                    friendNameText = (TextView)list_layout.findViewById(R.id.friendNameText);
                    friendNameText.setText(name);
                    list_layout.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            showDialog(name, friendId);
                            return true;
                        }
                    });
                    inflateLayout.addView(list_layout);
                }
>>>>>>> b9dc584912a430d449e5e99cf0c6e941b1fe7366
            }
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

    public void showDialog(String friendName, String friendId) {
        final CharSequence[] items = {"1:1 대화", "친구 삭제"};
        AlertDialog.Builder friendBuilder = new AlertDialog.Builder(getActivity());
        friendBuilder.setTitle("친구 관리");
        friendBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("onClick: ", Integer.toString(which) + "입니다");
                switch (which) {
                    case 0:
                        moveChatRoom(friendName, friendId);
                        break;
                    case 1:
                        sendToDeleteFriend(friendId);
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        receiveDeleteFriend(friendId);
                        Util.startToast(getContext(), "친구삭제 되었습니다");
                        break;
                }
            }
        });
        friendBuilder.show();
    }

    public void moveChatRoom(String friendName, String friendId) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("friendName", friendName);
        intent.putExtra("friendId", friendId);
        startActivity(intent);
    }

    public void sendToDeleteFriend(String friendId) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"" + "friend" + "\",");
        sb.append("\"method\":\"" + "delete" + "\",");
        sb.append("\"memberId\":\"" + MainActivity.id + "\",");
        sb.append("\"friendId\":\"" + friendId + "\"");
        sb.append("}");

        ConnectSocket.sendQueue.offer((sb.toString()));
    }

<<<<<<< HEAD
    public void receiveDeleteFriend(String friendId) {
        List<String> lists = JsonHandler.lists;

        String status = lists.get(0);
        if ("r200".equals(status)) {
            Util.startToast(getContext(), friendId + "가 삭제되었습니다.");
        } else {
            Util.startToast(getContext(), "삭제에 실패했습니다.");
        }
    }

    private void moveActivity(Class c) {
        Intent intent = new Intent(getContext(), c);
=======
        try {
            Thread.sleep(300);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 결과 받기
        String result = ConnectSocket.receiveQueue.poll();
        try {
            JSONObject json = new JSONObject(result);
            String method = json.getString("method");
            String status = json.getString("status");
            if ("delete".equals(method) && "r200".equals(status)) {
                Util.startToast(getContext(),friendId+"삭제되었습니다.");
                refresh();
            }else{
                Util.startToast(getContext(),"실패했습니다.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void refresh(){
        inflateLayout.removeAllViews();
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"" + "friend" + "\",");
        sb.append("\"method\":\"" + "selectAllFriend" + "\",");
        sb.append("\"id\":\"" + MainActivity.id + "\"");
        sb.append("}");

        ConnectSocket.sendQueue.offer((sb.toString()));
        try {
            Thread.sleep(300);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String result = ConnectSocket.receiveQueue.poll();

        try {
            JSONObject json = new JSONObject(result);
            JSONArray jsonArray = json.getJSONArray("friendList");
            String method = json.getString("method");
            String status = json.getString("status");
            for(int i=0; i<jsonArray.length();i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                String name = obj.getString("name");
                String friendId = obj.getString("id");

                if ("selectAllFriend".equals(method) && "r200".equals(status)) {
                    LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
                    list_layout = inflater.inflate(R.layout.friendlistlayout,inflateLayout,false);
                    friendNameText = (TextView)list_layout.findViewById(R.id.friendNameText);
                    friendNameText.setText(name);
                    list_layout.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            showDialog(name, friendId);
                            return true;
                        }
                    });
                    inflateLayout.addView(list_layout);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void moveActivity(Class c){//

        Intent intent = new Intent(getContext(),c);
>>>>>>> b9dc584912a430d449e5e99cf0c6e941b1fe7366
        startActivity(intent);
    }

}
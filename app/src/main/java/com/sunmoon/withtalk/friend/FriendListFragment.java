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

import com.sunmoon.withtalk.common.JsonHandler;
import com.sunmoon.withtalk.R;
import com.sunmoon.withtalk.chatroom.ChatActivity;
import com.sunmoon.withtalk.common.ConnectSocket;
import com.sunmoon.withtalk.common.Friend;
import com.sunmoon.withtalk.common.FriendList;
import com.sunmoon.withtalk.common.MainActivity;
import com.sunmoon.withtalk.common.Util;
import com.sunmoon.withtalk.common.DataAdapter;

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

        selectAllFriend(inflater);

        return rootView;
    }

    public void selectAllFriend(LayoutInflater inflater) {
        DataAdapter mDbHelper = new DataAdapter(getContext());
        mDbHelper.createDatabase();
        mDbHelper.open();

        Friend friend = null;
        List fList = mDbHelper.selectAllFriend();

        mDbHelper.close();

        if (fList.size() == 0) {
            Log.d( "selectAllFriend: ", "바보1");
            sendSelectAllFriend();
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            receiveSelectAllFriend(inflater);
            Log.d( "selectAllFriend: ", "바보2");
        } else {
            for (int i = 0; i < fList.size(); i++) {
                Log.d( "selectAllFriend: ", "바보3");
                friend = (Friend) fList.get(i);
                String friendId = friend.id;
                String name = friend.name;

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
        }
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

        DataAdapter mDbHelper = new DataAdapter(getContext().getApplicationContext());
        mDbHelper.createDatabase();
        mDbHelper.open();

        Friend friend = null;

        if ("r200".equals(status) && lists.size() != 1) {
            for (int i = 1; i < lists.size(); i = i + 2) {
                String friendId = lists.get(i);
                String name = lists.get(i + 1);

                friend = new Friend(friendId, name);
                mDbHelper.insertFriend(friend);
                mDbHelper.close();

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
        Log.d("Friend1", friendId);
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
                        sendDeleteFriend(friendId);

                        try {
                            Thread.sleep(1000);
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
        intent.putExtra("friendName", friendName);
        intent.putExtra("friendId", friendId);

        startActivity(intent);
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

        Log.d("++++++++", sb.toString());
        ConnectSocket.sendQueue.offer((sb.toString()));
    }

    public void receiveDeleteFriend(String friendId) {
        ArrayList<String> lists = JsonHandler.messageReceived();
        String status = lists.get(0);
        Log.d("Friend3", friendId);

        if ("r200".equals(status)) {
            //내부 디비에서 삭제하기
            DataAdapter mDbHelper = new DataAdapter(getContext().getApplicationContext());
            mDbHelper.createDatabase();
            mDbHelper.open();

            mDbHelper.deleteFriend(friendId);
            Log.d("FriendID!!", friendId);

            mDbHelper.close();

            Util.startToast(getContext(), friendId + "가 삭제되었습니다.");
            Log.d("Friend4", friendId);
            refresh();
        } else {
            Util.startToast(getContext(), "실패했습니다.");
        }
    }

    public void refresh() {
        inflateLayout.removeAllViews();
        DataAdapter mDbHelper = new DataAdapter(getContext());
        mDbHelper.createDatabase();
        mDbHelper.open();

        Friend friend = null;
        List fList = mDbHelper.selectAllFriend();
        mDbHelper.close();

        for (int i = 0; i < fList.size(); i++) {
            friend = (Friend) fList.get(i);
            String friendId = friend.id;
            String name = friend.name;

            Log.d("FriendID22", friendId + " , " + name);

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
    }

    private void moveActivity(Class c) {
        Intent intent = new Intent(getContext(), c);
        startActivity(intent);
    }
}
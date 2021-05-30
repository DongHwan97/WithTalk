package com.sunmoon.withtalk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FriendListFragment extends Fragment {

    ViewGroup rootView;
    TextView friendNameText;
    View list_layout;
    LinearLayout inflateLayout;
    ImageButton moveSearchFriend, moveAddFriend;

    public static FriendListFragment newInstance() {
        FriendListFragment fragment = new FriendListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_friend_list, container, false);
        inflateLayout = (LinearLayout)rootView.findViewById(R.id.friend_layout);

        moveSearchFriend = (ImageButton)rootView.findViewById(R.id.moveSearchFriend);
        moveAddFriend = (ImageButton)rootView.findViewById(R.id.moveAddFriend);


        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"" + "friend" + "\",");
        sb.append("\"method\":\"" + "selectAllFriend" + "\",");
        sb.append("\"id\":\"" + MainActivity.id + "\",");
        sb.append("}");

        ConnectSocket.sendQueue.offer((sb.toString()));
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String result = ConnectSocket.receiveQueue.poll();

        Log.e( "onCreateView: ", "몇명?"+ result);

        try {
            JSONObject json = new JSONObject(result);
            JSONArray jsonArray = json.getJSONArray("friendList");
            String method = json.getString("method");
            String status = json.getString("status");
            for(int i=0; i<jsonArray.length();i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                String name = obj.getString("name");
                String id = obj.getString("id");
                if ("selectAllFriend".equals(method) && "r200".equals(status)) {
                    list_layout = inflater.inflate(R.layout.friendlistlayout,inflateLayout,false);
                    friendNameText = (TextView)list_layout.findViewById(R.id.friendNameText);
                    friendNameText.setText(name);

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
        inflateLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDialog();
                return true;
            }
        });


        moveSearchFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveActivity(SearchFriendActivity.class);
            }
        });

        moveAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveActivity(AddFriendActivity.class);
            }
        });
    }

    public void showDialog(){
    final CharSequence[] items = {"1:1 대화", "친구 삭제"};
        AlertDialog.Builder friendBuilder = new AlertDialog.Builder(getActivity());
        friendBuilder.setTitle("친구 관리");
        friendBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e( "onClick: ", Integer.toString(which)+"입니다");
                     switch (which){
                         case 0: moveActivity(ChatActivity.class);
                             break;
                         case 1: deleteFriend();
                             Util.startToast(getContext(),"친구삭제 되었습니다");
                             break;
                     }
            }
        });
        friendBuilder.show();
    }

    public void deleteFriend(){

    }
    public void showFriendList(){
        
    }
    private void moveActivity(Class c){//

        Intent intent = new Intent(getActivity(),c);
        startActivity(intent);
    }
}
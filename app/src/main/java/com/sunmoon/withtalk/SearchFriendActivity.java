package com.sunmoon.withtalk;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchFriendActivity extends AppCompatActivity {

    ImageButton searchFriendButton;
    EditText searchFriendEdit;
    LinearLayout inflateLayout;
    View listLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        inflateLayout = findViewById(R.id.searchFriendLayout);

        searchFriendButton = findViewById(R.id.searchFriendButton);
        searchFriendEdit = findViewById(R.id.searchFriendEdit);

        String senderId = MainActivity.id;

        searchFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFriend(senderId);
            }
        });


    }

    public void searchFriend(String senderId){
        inflateLayout.removeAllViews();
        String friendName = searchFriendEdit.getText().toString();//검색 데이터 전송 받고
        if ((friendName.length() > 0) ) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"type\":\"" + "friend" + "\",");
            sb.append("\"method\":\"" + "searchRegistFriend" + "\",");
            sb.append("\"senderId\":\"" + senderId + "\",");
            sb.append("\"searchName\":\"" + friendName + "\"");
            sb.append("}");
            ConnectSocket.sendQueue.offer((sb.toString()));

            try {
                Thread.sleep(300);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 결과 받기
            String result = ConnectSocket.receiveQueue.poll();
            Log.e( "searchFriend: ","result"+result );

            try {
                JSONObject json = new JSONObject(result);
                JSONArray jsonArray = json.getJSONArray("registFriendList");
                String method = json.getString("method");
                String status = json.getString("status");
                if ("searchRegistFriend".equals(method) && "r200".equals(status)) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String name = obj.getString("name");
                        String friendId = obj.getString("id");


                        if ("searchRegistFriend".equals(method) && "r200".equals(status)) {
                            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            listLayout = inflater.inflate(R.layout.friendlistlayout, inflateLayout, false);
                            TextView friendNameText = (TextView) listLayout.findViewById(R.id.friendNameText);
                            friendNameText.setText(name);
                            inflateLayout.addView(listLayout);
                            listLayout.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    showDialog(friendId);
                                    return true;
                                }
                            });
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Util.startToast(this, "이름을 입력해 주세요");
        }
    }

    public void showDialog(String friendId){
        final CharSequence[] items = {"1:1 대화", "친구 삭제"};
        AlertDialog.Builder friendBuilder = new AlertDialog.Builder(this);
        friendBuilder.setTitle("친구 관리");

        friendBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0: moveActivity(ChatActivity.class);
                        break;
                    case 1: deleteFriend(friendId);
                        break;
                }
            }
        });
        friendBuilder.show();
    }

    public void deleteFriend(String friendId){

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\":\"" + "friend" + "\",");
        sb.append("\"method\":\"" + "delete" + "\",");
        sb.append("\"memberId\":\"" + MainActivity.id + "\",");
        sb.append("\"friendId\":\"" + friendId + "\"");
        sb.append("}");
        ConnectSocket.sendQueue.offer((sb.toString()));

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
                Util.startToast(this,friendId+"삭제되었습니다.");
            }else{
                Util.startToast(this,"실패했습니다.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void moveActivity(Class c){//
        Intent intent = new Intent(this,c);
        startActivity(intent);
    }
}
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

        searchFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFriend();
            }
        });

        inflateLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDialog();
                return true;
            }
        });
    }

    public void searchFriend(){
        String friendPhoneNo = searchFriendEdit.getText().toString();//검색 데이터 전송 받고
        if ((friendPhoneNo.length() > 10) ) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"type\":\"" + "friend" + "\",");
            sb.append("\"method\":\"" + "searchFriend" + "\",");
            sb.append("\"phoneNo\":\"" + friendPhoneNo + "\",");
            sb.append("}");

            ConnectSocket.sendQueue.offer((sb.toString()));
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 결과 받기
            String result = ConnectSocket.receiveQueue.poll();

            JsonParser parser = new JsonParser();
            JsonElement json = parser.parse(result);

            String method = json.getAsJsonObject().get("method").toString();
            String status = json.getAsJsonObject().get("status").toString();
            String id = json.getAsJsonObject().get("id").toString();
            String name = json.getAsJsonObject().get("name").toString();
            String phoneNo = json.getAsJsonObject().get("phoneNo").toString();

            if ("\"searchFriend\"".equals(method) && "\"r200\"".equals(status)) {
                Util.startToast(this, "로그인 성공하셨습니다.");
                inflateLayout.removeView(listLayout);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                listLayout =  inflater.inflate(R.layout.friendlistlayout, inflateLayout, false);
                TextView friendNameText = (TextView)listLayout.findViewById(R.id.friendNameText);
                friendNameText.setText(name);
                inflateLayout.addView(listLayout);

            } else {
                Util.startToast(this, "에헤헤 뵹신 !");
            }
        }else{
            Util.startToast(this, "연락처를 입력해 주세요");
        }
    }

    public void showDialog(){
        final CharSequence[] items = {"1:1 대화", "친구 삭제"};
        AlertDialog.Builder friendBuilder = new AlertDialog.Builder(this);
        friendBuilder.setTitle("친구 관리");
        friendBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e( "onClick: ", Integer.toString(which)+"입니다");
                switch (which){
                    case 0: moveActivity(ChatActivity.class);
                        break;
                    case 1: deleteFriend();
                        break;
                }
            }
        });
        friendBuilder.show();
    }

    public void deleteFriend(){

    }

    private void moveActivity(Class c){//

        Intent intent = new Intent(this,c);
        startActivity(intent);
    }
}
package com.sunmoon.withtalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AddFriendActivity extends AppCompatActivity {

    ImageButton searchAddFriendButton;
    EditText searchAddFriendEdit;
    LinearLayout inflateLayout;
    View listLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        inflateLayout = findViewById(R.id.searchAddFriendLayout);

        searchAddFriendButton = findViewById(R.id.searchAddFriendButton);
        searchAddFriendEdit = findViewById(R.id.searchAddFriendEdit);

        searchAddFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFriend();
            }
        });
    }

    public void searchFriend(){
        inflateLayout.removeView(listLayout);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        searchAddFriendEdit.getText().toString();//검색 데이터 전송 받고

        listLayout =  inflater.inflate(R.layout.friendlistlayout, inflateLayout, false);
        TextView friendNameText = (TextView)listLayout.findViewById(R.id.friendNameText);
        ImageButton addFriendButton = (ImageButton) listLayout.findViewById(R.id.friendAddButton);

        addFriendButton.setVisibility(View.VISIBLE);
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend();
            }
        });

        friendNameText.setText("친구");

        inflateLayout.addView(listLayout);
    }

    public void addFriend(){
        Util.startToast(this,"친구추가 되었습니다");
    }

}
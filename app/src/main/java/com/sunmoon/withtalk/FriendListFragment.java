package com.sunmoon.withtalk;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


public class FriendListFragment extends Fragment {

    ViewGroup rootView;
    TextView nameText, dateText;
    View list_layout;
    LinearLayout inflateLayout;
    ImageButton searchFriendButton, addFriendButton;
    public static FriendListFragment newInstance() {
        FriendListFragment fragment = new FriendListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_friend_list, container, false);
        inflateLayout = (LinearLayout)rootView.findViewById(R.id.friend_layout);

        searchFriendButton = (ImageButton)rootView.findViewById(R.id.searchFriendButton);
        addFriendButton = (ImageButton)rootView.findViewById(R.id.addFriendButton);


        for(int i=0;i<10;i++){
            list_layout = inflater.inflate(R.layout.list_layout,inflateLayout,false);
            nameText = (TextView)list_layout.findViewById(R.id.chatRoomText);
            dateText = (TextView)list_layout.findViewById(R.id.listDateText);

            nameText.setText(Integer.toString(i)+"친구이름");
            dateText.setText("");
            inflateLayout.addView(list_layout);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inflateLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN) {

                }
                return true;
            }
        });

        searchFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveActivity(SearchFriendActivity.class);
            }
        });

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveActivity(AddFriendActivity.class);
            }
        });
    }

    private void moveActivity(Class c){//

        Intent intent = new Intent(getActivity(),c);
        startActivity(intent);
    }
}
package com.sunmoon.withtalk.chatroom;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChatRoomList {//서버에서 받아온 채팅방 리스트 저장
    public static final Map<String, String> CHATROOMLIST_DM = new HashMap<String, String>();//<친구 이름, 방아이디(방번호)>
    public static final Map<String, JSONObject> CHATROOMLIST_ALL = new HashMap<String, JSONObject>();//<방아이디(방번호), 채팅방이름>
}

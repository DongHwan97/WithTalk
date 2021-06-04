package com.sunmoon.withtalk.common;

import android.app.Activity;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sunmoon.withtalk.common.ConnectSocket;
import com.sunmoon.withtalk.common.FriendList;
import com.sunmoon.withtalk.common.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class JsonHandler {
    static JSONObject json;
    static String method;
    static ArrayList<String> lists;
    static JSONArray jsonArray;

    public static ArrayList<String> messageReceived() {
        if (ConnectSocket.receiveQueue.peek() != null) {
            String result = ConnectSocket.receiveQueue.poll();
            try {
                json = new JSONObject(result);
                Log.d("응답", result.toString());

                method = json.getString("method");

                switch (method) {
                    case "login":
                    case "signUp":
                    case "auth":
                    case "resetPassword":
                    case "logout":
                    case "delete":
                    case "insertFriend":
                    case "exit":
                        lists = new ArrayList<>();
                        lists.add(json.getString("status"));

                        break;
                    case "findId":
                        lists = new ArrayList<>();
                        lists.add(json.getString("status"));
                        lists.add(json.getString("id"));

                        break;
                    case "selectAllFriend":
                        lists = new ArrayList<>();
                        lists.add(json.getString("status"));

                        jsonArray = json.getJSONArray("friendList");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            lists.add(obj.getString("id"));
                            lists.add(obj.getString("name"));
                        }

                        break;
                    case "searchFriend":
                        lists = new ArrayList<>();
                        lists.add(json.getString("status"));
                        lists.add(json.getString("id"));
                        lists.add(json.getString("name"));
                        lists.add(json.getString("phoneNo"));
                        break;
                    case "searchRegistFriend":
                        lists = new ArrayList<>();
                        lists.add(json.getString("status"));

                        jsonArray = json.getJSONArray("registFriendList");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            lists.add(obj.getString("id"));
                            lists.add(obj.getString("name"));
                        }

                        break;
                    case "selectAllChatRoom":
                        lists = new ArrayList<>();
                        lists.add(json.getString("status"));
                        jsonArray = json.getJSONArray("chatRoomList");
                        if (jsonArray.length() != 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                lists.add(obj.getString("chatRoomNo"));
                                JSONArray memberIdList = obj.getJSONArray("memberIdList");
                                StringBuilder memberList_str = new StringBuilder();
                                int j;
                                memberList_str.append("{\"memberIdList\":[");
                                if(memberIdList.length()==2){

                                    for (j = 0; j < memberIdList.length(); j++) {
                                        if (!MainActivity.id.equals(memberIdList.getString(j))) {
                                            memberList_str.append("\"");
                                            memberList_str.append(memberIdList.getString(j));
                                            memberList_str.append("\"");
                                        }
                                    }
                                }else{
                                    for (j = 0; j < memberIdList.length(); j++) {
                                        if (!MainActivity.id.equals(memberIdList.getString(j))) {
                                            memberList_str.append(memberIdList.getString(j));
                                            memberList_str.append("\"");
                                            if (j < memberIdList.length() - 1) {
                                                memberList_str.append(", ");
                                            }
                                            memberList_str.append("\"");
                                        }
                                        Log.e("messageReceived: ", memberList_str.toString());
                                    }
                                }
                                memberList_str.append("]}");

                                lists.add(memberList_str.toString());
                                lists.add(Integer.toString(memberIdList.length()));
                            }
                        }

                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return lists;
    }
}

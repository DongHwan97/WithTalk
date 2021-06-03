package com.sunmoon.withtalk;

import android.app.Activity;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

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
                Log.d("+++++++++++", "얘가 문제니?" + method);
                switch (method) {
                    case "login":
                    case "signUp":
                    case "auth":
                    case "resetPassword":
                    case "logout":
                    case "delete":
                    case "insertFriend":
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
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return lists;
    }
}

package com.sunmoon.withtalk;

import android.app.Activity;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Handler;

public class JsonHandler {
    static JSONObject json;
    static String method;
    static String[] list;

    public static String[] messageReceived() {
        Log.d("+++++++++++", "while문 전");
        while (true) {
            if (ConnectSocket.receiveQueue.peek() != null) {
                String result = ConnectSocket.receiveQueue.poll();
                try {
                    json = new JSONObject(result);

                    method = json.getString("method");
                    Log.d("+++++++++++", "얘가문제니?"+method);
                    switch (method) {
                        case "findId":
                            list = new String[2];
                            list[0] = json.getString("status");
                            list[1] = json.getString("id");

                            Log.d("+++++++++++", list.toString());

                            break;
                        case "\"login\"":
                            list = new String[1];
                            list[0] = json.getString("status");

                            break;
                        case "":

                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                return list;
            }
        }
    }
}

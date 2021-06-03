package com.sunmoon.withtalk.common;

import android.app.Activity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class ConnectSocket extends Activity {
    public static final String SERVER_IP = "192.168.25.55";
    public static final int SERVER_PORT = 5050;
    public static final int READ_BUFFER = 1024;

    public static SocketChannel socketChannel;

    String received_msg;


    public static Queue<String> receiveQueue = new LinkedList<>();
    public static Queue<String> sendQueue = new LinkedList<>();

    public ConnectSocket() {
        this.startClient();
    }

    void startClient() {
        new Thread() {
            @Override
            public void run() {
                try {
                    socketChannel = SocketChannel.open();
                    socketChannel.configureBlocking(true);
                    socketChannel.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
                } catch (Exception e) {
                    return;
                }
                receive();
            }
        }.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                send();
            }
        }).start();
    }

    void receive() {
        while (true) {
            try {
                ByteBuffer byteBuffer = ByteBuffer.allocate(READ_BUFFER);

                int byteCount = socketChannel.read(byteBuffer);

                if (byteCount == -1) {
                    throw new IOException();
                }

                byteBuffer.flip();
                Charset charset = Charset.forName("UTF-8");
                String data = charset.decode(byteBuffer).toString();

                received_msg =  data;

                // 1. 내부 DB에 넣기

                // 2. 송신ID == chatRoomID ? 현재 채팅방에 보여줌 : 안보여줌
                receiveQueue.offer(received_msg);

            } catch (Exception e) {
                e.printStackTrace();
                stopClient();
                break;
            }
        }
    }

    void send() {
        while(true) {
            try {
                if (sendQueue.peek() != null) {
                    String data = sendQueue.poll();
                    Charset charset = Charset.forName("UTF-8");
                    ByteBuffer byteBuffer = charset.encode(data);
                    socketChannel.write(byteBuffer);
                }
            } catch (Exception e) {
                stopClient();
            }
        }
    }

    void stopClient() {
        try {
            if (socketChannel != null && socketChannel.isOpen()) {
                socketChannel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class JsonHandler {
        static JSONObject json;
        static String method;
        static ArrayList<String> lists;
        static JSONArray jsonArray;

        public static ArrayList<String> messageReceived() {
            if (receiveQueue.peek() != null) {
                String result = receiveQueue.poll();
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
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                lists.add(obj.getString("chatRoomNo"));
                                JSONArray memberIdList = obj.getJSONArray("memberIdList");
                                StringBuilder memberList_str = new StringBuilder();
                                int j;
                                for (j=0; j<memberIdList.length();j++){
                                    if(!MainActivity.id.equals(memberIdList.getString(j))){
                                        memberList_str.append(FriendList.FRIEND_LIST.get(memberIdList.getString(j)));
                                        if (j < memberIdList.length()-1) {
                                            memberList_str.append(", ");
                                        }
                                    }
                                    Log.e("messageReceived: ", memberList_str.toString());
                                }
                                lists.add(memberList_str.toString());
                                lists.add(Integer.toString(j));
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
}
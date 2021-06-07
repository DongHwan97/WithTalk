package com.sunmoon.withtalk.common;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.sunmoon.withtalk.db.DataAdapter;
import com.sunmoon.withtalk.db.MessageDB;
import com.sunmoon.withtalk.friend.FriendList;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Queue;

public class ConnectSocket extends Thread {
    public static final String SERVER_IP = "192.168.25.55";
    public static final int SERVER_PORT = 5050;
    public static final int READ_BUFFER = 10000;

    public static SocketChannel socketChannel;

    public static Context mContext;

    String received_msg;

    public static Queue<String> receiveQueue = new LinkedList<>();
    public static Queue<String> sendQueue = new LinkedList<>();
    public static Queue<String> toChatQueue = new LinkedList<>();



    Handler handler;

    public ConnectSocket(Handler handler) {

        this.handler = handler;

    }

    @Override
    public void run(){
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(true);
            socketChannel.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
        } catch (Exception e) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                send();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                receive();
            }
        }).start();
    }

    void receive() {

        DataAdapter mDbHelper = new DataAdapter(mContext);
        mDbHelper.createDatabase();
        mDbHelper.open();
        while (true) {
            try {
                ByteBuffer byteBuffer = ByteBuffer.allocate(READ_BUFFER);

                int byteCount = socketChannel.read(byteBuffer);

                if (byteCount == -1) {
                    //throw new IOException();
                    continue;
                }

                byteBuffer.flip();
                Charset charset = Charset.forName("UTF-8");
                String data = charset.decode(byteBuffer).toString();

                received_msg =  data;

                int seqNo, chatRoomNo;
                String contents, sendTime, senderId, isRead="false";

                JSONObject json = new JSONObject(received_msg);


                if (json.getString("method").equals("sendChat")){
                    seqNo = json.getInt("seqNo");
                    contents = json.getString("contents");
                    sendTime = json.getString("sendTime");
                    chatRoomNo = json.getInt("chatRoomNo");
                    senderId = json.getString("senderId");

                    Message message = handler.obtainMessage() ;
                    message.obj = json;
                    this.handler.sendMessage(message);

                    if(FriendList.chatRoomNo != null && FriendList.chatRoomNo.equals(""+chatRoomNo)){
                        isRead="true";
                        // 1.   현재 채팅방에 보여줌 : 안보여줌
                        toChatQueue.offer(received_msg);
                    }
                    // 2. 내부 DB에 넣기

                    MessageDB messageDB = new MessageDB(seqNo, senderId, contents, sendTime, chatRoomNo, isRead);
                    mDbHelper.insertMessage(messageDB);

                    // db 닫기

                } else {
                    receiveQueue.offer(received_msg);
                }

            } catch (IOException e) {
                e.printStackTrace();
                stopClient();
                break;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mDbHelper.close();
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
}
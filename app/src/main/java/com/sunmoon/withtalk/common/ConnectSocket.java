package com.sunmoon.withtalk.common;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
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

public class ConnectSocket extends Thread {
    public static final String SERVER_IP = "192.168.25.55";
    public static final int SERVER_PORT = 5050;
    public static final int READ_BUFFER = 10000;

    public static SocketChannel socketChannel;

    String received_msg;


    public static Queue<String> receiveQueue = new LinkedList<>();
    public static Queue<String> sendQueue = new LinkedList<>();
    public static Queue<String> toChatQueue = new LinkedList<>();

    Handler handler;
    boolean isRun = true;

    public ConnectSocket(Handler handler) {
        try {
            this.handler = handler;
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(true);
            socketChannel.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
        } catch (Exception e) {
            return;
        }

    }

    public ConnectSocket() {
        startClient();

    }

    public void stopForever(){
        synchronized (this) {
            this.isRun = false;
        }
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
                JSONObject json;
                received_msg =  data;
                String contents, sendTime, chatRoomNo, senderId, isRead="false";
                if (received_msg.contains("sendChat")){


                    json = new JSONObject(received_msg);
                    contents = json.getString("contents");
                    sendTime = json.getString("sendTime");
                    chatRoomNo = json.getString("chatRoomNo");
                    senderId = json.getString("senderId");

                    Message message = handler.obtainMessage() ;

                    // fill the message object.
                    message.obj = json;

                    // send message object.
                    this.handler.sendMessage(message) ;

                    if(chatRoomNo.equals(FriendList.chatRoomNo)){
                        isRead="true";
                        // 1.   현재 채팅방에 보여줌 : 안보여줌
                        toChatQueue.offer(received_msg);
                    }
                    // 2. 내부 DB에 넣기




                }else{
                    receiveQueue.offer(received_msg);
                }






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
                    Log.e("tt1",data);
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
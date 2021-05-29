package com.sunmoon.withtalk;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ConnectSocket extends Activity {
    public static final String SERVER_IP = "192.168.25.24";
    public static final int SERVER_PORT = 5050;
    public static final int READ_BUFFER = 1024;

    public static SocketChannel socketChannel;

    String received_msg;

    public static String chatRoomID;
    public static Queue<String> receiveQueue = new LinkedList<>();
    public static Queue<String> sendQueue = new LinkedList<>();

    ConnectSocket() {
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
}

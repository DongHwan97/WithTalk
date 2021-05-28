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
    ByteBuffer buffer = ByteBuffer.allocate(READ_BUFFER);

    TextView msg;

    String received_msg;

    public static String chatRoomID;
    public static Queue<String> toChatRoomMsgList = new LinkedList<>();
    public static Queue<String> fromChatRoomMsgList = new LinkedList<>();

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
                    socketChannel.connect(new InetSocketAddress("192.168.219.162", 1428));

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
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

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
                toChatRoomMsgList.offer(received_msg);


                Log.d("받은 메시지" , received_msg);

                /*
                if (msg != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            msg.setText(data);
                        }
                    });
                }*/


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
                if (fromChatRoomMsgList.peek() != null) {
                    Log.d("withtalk", "있어!");

                    String data = fromChatRoomMsgList.poll();
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



    public void setSocket() {
        try {
            socketChannel = SocketChannel.open(new InetSocketAddress(SERVER_IP, SERVER_PORT));
            receiveMSG.start();

        } catch (IOException e) {
            //e.printStackTrace();
            if (socketChannel != null) {
                try {
                    socketChannel.close();

                } catch (IOException e1) {
                    //e1.printStackTrace();
                }
            }
        }
    }



    Thread receiveMSG = new Thread(new Runnable() {
        @Override
        public void run() {

            try {
                socketChannel.read(buffer);
                buffer.flip();
                Log.d("Receive msg: ", buffer.toString());
                if ( msg != null ) {
                    msg.setText(buffer.toString());
                }
                buffer.clear();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    });

    public void sendMSG(String msg) {
        byte[] bytes;
        ByteBuffer buf;
        try {
            bytes = msg.getBytes();
            buf = ByteBuffer.wrap(bytes);
            this.socketChannel.write(buf);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

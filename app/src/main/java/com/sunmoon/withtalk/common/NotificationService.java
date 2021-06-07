package com.sunmoon.withtalk.common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.sunmoon.withtalk.R;
import com.sunmoon.withtalk.chatroom.ChatRoomList;
import com.sunmoon.withtalk.friend.FriendList;
import com.sunmoon.withtalk.user.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationService extends Service {

    ConnectSocket connectSocket;
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;

    private static String CHANNEL_ID = "2555";
    private static String CHANEL_NAME = "WithTalk";


    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        myServiceHandler handler = new myServiceHandler();
        connectSocket = new ConnectSocket(handler);
        connectSocket.start();
        createNotificationChannel();


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        connectSocket = null;
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_ID;
            String description = CHANNEL_ID;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    class myServiceHandler extends Handler {

        public void showNotification(String friendName, String str){//푸시알림
            builder = null;
            notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(
                        new NotificationChannel(CHANNEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
                );
                builder = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID);
            }

            builder.setContentTitle(friendName)
                    .setContentText(str)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            notificationManager.notify(1,notification);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            Intent intent = new Intent(NotificationService.this, LoginActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(NotificationService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

            try {
                JSONObject j = (JSONObject)msg.obj;
                String friendID = j.getString("senderId");

                if (MainActivity.id.equals(friendID)) { return; }

                String contents = j.getString("contents");
                String friendName = FriendList.FRIEND_LIST.get(friendID);

                this.showNotification(friendName, contents);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
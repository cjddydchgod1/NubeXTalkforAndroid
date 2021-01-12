/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Manager.FireBase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Map;

import io.realm.Realm;
import x.com.nubextalk.ChatRoomActivity;
import x.com.nubextalk.Manager.DateManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.R;

/**
 * Firebase Message Service
 * - onNewToken : Token 이 갱신될때 호출
 * - onMessageReceived : FCM이 수시신될떄 호출
 * - 참고 : https://firebase.google.com/docs/cloud-messaging/android/client?authuser=0
 */
public class FirebaseMsgService extends FirebaseMessagingService {
    String CHANNEL_ID = "0608";
    Realm realm;
    NotificationManager notificationManager;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("FCM_TOKEN_OnNew : ", s);

        Realm realm = Realm.getInstance(UtilityManager.getRealmConfig());
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Config userMe = realm.where(Config.class).equalTo("CODE", "MyAccount").findFirst();
                if (userMe == null) {
                    userMe = new Config();
                    userMe.setCODENAME("MyAccount");
                    userMe.setCODE("MyAccount");
                }
                userMe.setExt4(s);
                realm.copyToRealmOrUpdate(userMe);
            }
        });


        /**
         * FCM Token 확인 함수
         */
        /*FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TOKEN", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.d("TOKEN", token);
                    }
                });*/
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        realm = Realm.getInstance(UtilityManager.getRealmConfig());
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        String cid;
        String uid;
        String rid;
        int type;
        String content;
        Date date;
        Boolean isFirst;

        Map<String, String> data = remoteMessage.getData();
        Log.d("TOKEN", "RECEIVE_TOKEN\nCODE : " + data.get("CODE") + "\nDATE : " + data.get("date"));
        switch (data.get("CODE")) {

            case "CHAT_CONTENT_CREATED": //chat 받았을 때
                cid = data.get("chatContentId");
                uid = data.get("senderId");
                rid = data.get("chatRoomId");
                type = Integer.parseInt(data.get("contentType"));
                content = data.get("content");
                date = DateManager.convertDatebyString(data.get("sendDate"), "yyyy-MM-dd'T'hh:mm:ss");
                isFirst = Boolean.parseBoolean(data.get("isFirst"));

                if (!UtilityManager.getUid().equals(uid)) {
                    makeChannel(CHANNEL_ID);
                    notificationManager.notify(1, makeBuilder(rid, uid, type, content).build());
                }

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        ChatRoom roomInfo = realm.where(ChatRoom.class).equalTo("rid", data.get("chatRoomId")).findFirst();
                        roomInfo.setUpdatedDate(new Date());
                        realm.copyToRealmOrUpdate(roomInfo);

                        ChatContent chat = new ChatContent();

                        chat.setCid(cid); // Content ID 자동으로 유니크한 값 설정
                        chat.setUid(uid); // UID 보내는 사람
                        chat.setRid(rid); // RID 채팅방 아이디
                        chat.setType(type);
                        chat.setContent(content);
                        chat.setSendDate(date);
                        chat.setFirst(isFirst);
                        realm.copyToRealmOrUpdate(chat);
                    }
                });

                if (!UtilityManager.getUid().equals(uid)) {
                    makeChannel(CHANNEL_ID);
                    notificationManager.notify(1, makeBuilder(rid, uid, type, content).build());
                }

                break;
            case "FIRST_CHAT_CREATED": //채팅방이 생성되고 처음 메세지가 생성된 경우 채팅방과 채팅메세지 생성
                FirebaseFunctionsManager.getChatRoom(data.get("hospitalId"), data.get("chatRoomId"));

                cid = data.get("chatContentId");
                uid = data.get("senderId");
                rid = data.get("chatRoomId");
                type = Integer.parseInt(data.get("contentType"));
                content = data.get("content");
                date = DateManager.convertDatebyString(data.get("sendDate"), "yyyy-MM-dd'T'hh:mm:ss");
                isFirst = Boolean.parseBoolean(data.get("isFirst"));

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        ChatContent notifyChat = new ChatContent();
                        notifyChat.setCid("sys".concat(data.get("chatContentId")));
                        notifyChat.setRid(data.get("chatRoomId"));
                        notifyChat.setType(9);
                        notifyChat.setContent("채팅방이 개설되었습니다.");
                        notifyChat.setSendDate(DateManager.
                                convertDatebyString(data.get("sendDate"), "yyyy-MM-dd'T'hh:mm:ss"));
                        notifyChat.setIsRead(true);
                        notifyChat.setFirst(false);
                        realm.copyToRealmOrUpdate(notifyChat);

                        ChatContent chat = new ChatContent();
                        chat.setCid(cid); // Content ID 자동으로 유니크한 값 설정
                        chat.setUid(uid); // UID 보내는 사람
                        chat.setRid(rid); // RID 채팅방 아이디
                        chat.setType(type);
                        chat.setContent(content);
                        chat.setSendDate(date);
                        chat.setFirst(isFirst);
                        realm.copyToRealmOrUpdate(chat);
                    }
                });

                if (!UtilityManager.getUid().equals(uid)) {
                    makeChannel(CHANNEL_ID);
                    notificationManager.notify(1, makeBuilder(rid, uid, type, content).build());
                }
                break;
            case "CHAT_ROOM_INVITED":
                Log.d("TOKEN", "room invited!!");

                // Chatting Message(Notification Message)
                // System Message
                
        }
    }

    public void makeChannel(String id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(id) == null) {
                String channelName = "chat_notify";
                String description = "chatting";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;

                NotificationChannel channel = new NotificationChannel(id, channelName, importance);
                channel.setDescription(description);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public NotificationCompat.Builder makeBuilder(String rid, String uid, int type, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        ChatRoom roomInfo = realm.where(ChatRoom.class).equalTo("rid", rid).findFirst();
        User userInfo = realm.where(User.class).equalTo("uid", uid).findFirst();

        Bitmap userProfileImg = getImageFromURL(userInfo.getProfileImg());

        Intent intent = new Intent(this, ChatRoomActivity.class);
        intent.putExtra("rid", rid);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setSmallIcon(R.drawable.nube_x_logo)
                .setContentTitle(roomInfo.getRoomName())
                .setLargeIcon(userProfileImg)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setChannelId(CHANNEL_ID)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        if (type == 1) {
            builder.setContentText("사진");
        } else {
            builder.setContentText(content);
        }
        return builder;
    }

    public static Bitmap getImageFromURL(String imageURL) {
        Bitmap imgBitmap = null;
        HttpURLConnection conn = null;
        BufferedInputStream bis = null;

        try {
            URL url = new URL(imageURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            int nSize = conn.getContentLength();
            bis = new BufferedInputStream(conn.getInputStream(), nSize);
            imgBitmap = BitmapFactory.decodeStream(bis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        return imgBitmap;
    }
}

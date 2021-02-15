/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Manager.FireBase;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import x.com.nubextalk.Manager.NotifyManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.ChatRoomMember;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.Model.User;

/**
 * Firebase Message Service
 * - onNewToken : Token 이 갱신될때 호출
 * - onMessageReceived : FCM이 수시신될떄 호출
 * - 참고 : https://firebase.google.com/docs/cloud-messaging/android/client?authuser=0
 */
public class FirebaseMsgService extends FirebaseMessagingService {
    Realm realm;
    NotifyManager mNotifyManager;

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
        realm.close();


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
        mNotifyManager = new NotifyManager(this);


        Map<String, String> data = remoteMessage.getData();
        Map<String, Object> payload;

        User userInfo;
        StringBuilder sysContent;

        String cid;
        String rid;
        String uid;

        Log.d("TOKEN", "RECEIVE_TOKEN\nCODE : " + data.get("CODE") + "\nDATE : " + data.get("date") + "\nCONTENT : " + data.get("content"));
        switch (data.get("CODE")) {
            case "SYSTEM_ROOM_CREATED":
                payload = new HashMap<>();

                uid = data.get("senderId");

                Log.d("USERID", uid);
                userInfo = realm.where(User.class).equalTo("userId", uid).findFirst();
                sysContent = new StringBuilder();
                sysContent.append(userInfo.getAppName());
                sysContent.append("님이 채팅방을 개설 하였습니다.");


                payload.put("uid", "system");
                payload.put("cid", data.get("chatContentId"));
                payload.put("rid", data.get("chatRoomId"));
                payload.put("content", sysContent.toString());
                payload.put("type", data.get("contentType"));
                payload.put("sendDate", data.get("sendDate"));
                payload.put("isFirst", data.get("isFirst"));
                payload.put("isRead", "true");

                ChatContent.createChat(realm, payload);
                break;

            case "SYSTEM_MEMBER_ADD":

                payload = new HashMap<>();

                rid = data.get("chatRoomId");
                uid = data.get("senderId");
                userInfo = realm.where(User.class).equalTo("userId", uid).findFirst();
                String[] memberId = data.get("newAddedUserId").split(",");

                sysContent = new StringBuilder();

                sysContent.append(userInfo.getAppName()).append("님이\n");
                for (String id : memberId) {
                    User addUser = realm.where(User.class).equalTo("userId", id).findFirst();
                    if (addUser != null) {
                        sysContent.append(addUser.getAppName()).append("님 ");
                    }
                }
                sysContent.append("을 초대 하였습니다.");


                payload.put("uid", "system");
                payload.put("cid", data.get("chatContentId"));
                payload.put("rid", data.get("chatRoomId"));
                payload.put("content", sysContent.toString());
                payload.put("type", data.get("contentType"));
                payload.put("sendDate", data.get("sendDate"));
                payload.put("isFirst", data.get("isFirst"));
                payload.put("isRead", "true");

                ChatRoomMember.addChatRoomMember(realm, rid, memberId);
                ChatContent.createChat(realm, payload);
                break;

            case "SYSTEM_MEMBER_EXIT":

                payload = new HashMap<>();

                rid = data.get("chatRoomId");
                uid = data.get("senderId");

                userInfo = realm.where(User.class).equalTo("userId", uid).findFirst();

                sysContent = new StringBuilder();
                sysContent.append(userInfo.getAppName());
                sysContent.append("님이 채팅방을 나갔습니다.");

                payload.put("uid", "system");
                payload.put("cid", data.get("chatContentId"));
                payload.put("rid", data.get("chatRoomId"));
                payload.put("content", sysContent.toString());
                payload.put("type", data.get("contentType"));
                payload.put("sendDate", data.get("sendDate"));
                payload.put("isFirst", data.get("isFirst"));
                payload.put("isRead", "true");

                ChatRoomMember.deleteChatRoomMember(realm, rid, uid);
                ChatContent.createChat(realm, payload);
                break;

            case "CHAT_CONTENT_CREATED":
                payload = new HashMap<>();

                cid = data.get("chatContentId");
                rid = data.get("chatRoomId");
                uid = data.get("senderId");

                payload.put("uid", data.get("senderId"));
                payload.put("cid", data.get("chatContentId"));
                payload.put("rid", data.get("chatRoomId"));
                payload.put("content", data.get("content"));
                payload.put("type", data.get("contentType"));
                payload.put("sendDate", data.get("sendDate"));
                payload.put("isFirst", data.get("isFirst"));
                payload.put("ext1", data.get("ext1"));

                if (realm.where(ChatRoom.class).equalTo("rid", rid).findAll().isEmpty()) {
                    FirebaseFunctionsManager.getChatRoom("w34qjptO0cYSJdAwScFQ", rid) //Firebase Functions 함수의 getChatRoom 함수 호출을 통해 FireStore 에 있는 채팅방 데이터 불러옴
                            .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                                @Override
                                public void onSuccess(HttpsCallableResult httpsCallableResult) {
                                    Gson gson = new Gson();
                                    Map<String, Object> value = new HashMap<>();
                                    try {
                                        // realm ChatRoom 데이터 초기화
                                        Realm realm1 = Realm.getInstance(UtilityManager.getRealmConfig());

                                        JSONObject result = new JSONObject(gson.toJson(httpsCallableResult.getData()));
                                        value.put("rid", rid);
                                        value.put("title", result.getJSONObject("chatRoom").getString("roomName"));
                                        value.put("roomImgUrl", result.getJSONObject("chatRoom").getString("roomImg"));
                                        value.put("updatedDate", result.getJSONObject("chatRoom").getString("updatedDate"));
                                        value.put("notificationId", result.getJSONObject("chatRoom").getString("notificationId"));
                                        ArrayList<String> userIdList = new ArrayList<>();
                                        for (int i = 0; i < result.getJSONArray("chatRoomMember").length(); i++) {
                                            userIdList.add(result.getJSONArray("chatRoomMember").getString(i));
                                        }
                                        // realm ChatRoom, ChatContent 생성
                                        payload.put("isFirst", true);
                                        ChatRoom.createChatRoom(realm1, value, userIdList, new ChatRoom.onChatRoomCreatedListener() {
                                            @Override
                                            public void onCreate(ChatRoom chatRoom) {
                                                if (!Config.getMyUID(realm1).equals(uid)) {
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Realm realm2 = Realm.getInstance(UtilityManager.getRealmConfig());
                                                            mNotifyManager.notify(realm2, cid);
                                                            realm2.close();
                                                        }
                                                    }).start();
                                                }
                                            }
                                        });
                                        ChatContent.createChat(realm1, payload);
                                        realm1.close();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                } else { // 기존 realm 에 채팅방이 있는 경우에는 ChatContent 만 생성
                    ChatContent.createChat(realm, payload);
                    if (!Config.getMyUID(realm).equals(uid)) {
                        mNotifyManager.notify(realm, cid);
                    }
                }
                break;
        }
        realm.close();
    }
}

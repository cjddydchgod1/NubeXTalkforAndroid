/*
 * Created By JONGHO, LEE, Researcher of Institute of Biomedical Engineering, TechHeim Co.,Ltd.
 * on  2021.
 * This software is not stable health-care system and not certificated any health-care authority(like CE, FDA)
 * Prohibit redistribution, modification, commercial use.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Manager.FireBase;

import android.content.Context;
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
import x.com.nubextalk.Manager.ImageManager;
import x.com.nubextalk.Manager.NotifyManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.ChatRoomMember;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.Model.User;

import static x.com.nubextalk.Module.CodeResources.CODE_CHAT_CONTENT_CREATED;
import static x.com.nubextalk.Module.CodeResources.CODE_SYSTEM_MEMBER_ADD;
import static x.com.nubextalk.Module.CodeResources.CODE_SYSTEM_MEMBER_EXIT;
import static x.com.nubextalk.Module.CodeResources.CODE_SYSTEM_ROOM_CREATED;
import static x.com.nubextalk.Module.CodeResources.EMPTY_IMAGE;
import static x.com.nubextalk.Module.CodeResources.HOSPITAL_ID;
import static x.com.nubextalk.Module.CodeResources.MSG_MEMBER_ADD1;
import static x.com.nubextalk.Module.CodeResources.MSG_MEMBER_ADD2;
import static x.com.nubextalk.Module.CodeResources.MSG_MEMBER_ADD3;
import static x.com.nubextalk.Module.CodeResources.MSG_MEMBER_EXIT;
import static x.com.nubextalk.Module.CodeResources.MSG_ROOM_CREATED;

/**
 * Firebase Message Service
 * - onNewToken : Token 이 갱신될때 호출
 * - onMessageReceived : FCM이 수시신될떄 호출
 * - 참고 : https://firebase.google.com/docs/cloud-messaging/android/client?authuser=0
 */
public class FirebaseMsgService extends FirebaseMessagingService {
    private Realm mRealm;
    private Context mContext;

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
        mRealm = Realm.getInstance(UtilityManager.getRealmConfig());
        mContext = this;

        Map<String, String> data = remoteMessage.getData();
        Map<String, Object> payload;

        User userInfo;
        StringBuilder sysContent;

        String cid;
        String rid;
        String uid;

        Log.d("TOKEN", "RECEIVE_TOKEN\nCODE : " + data.get("CODE") + "\nDATE : " + data.get("date") + "\nCONTENT : " + data.get("content"));
        switch (data.get("CODE")) {
            case CODE_SYSTEM_ROOM_CREATED:
                payload = new HashMap<>();

                uid = data.get("uid");

                userInfo = mRealm.where(User.class).equalTo("uid", uid).findFirst();
                sysContent = new StringBuilder();
                sysContent.append(userInfo.getAppName());
                sysContent.append(MSG_ROOM_CREATED);


                payload.put("uid", "system");
                payload.put("rid", data.get("rid"));
                payload.put("cid", data.get("cid"));
                payload.put("content", sysContent.toString());
                payload.put("type", data.get("type"));
                payload.put("sendDate", data.get("sendDate"));
                payload.put("isFirst", data.get("isFirst"));
                payload.put("isRead", "true");

                ChatContent.createChat(mRealm, payload);
                break;

            case CODE_SYSTEM_MEMBER_ADD:

                payload = new HashMap<>();

                rid = data.get("rid");
                uid = data.get("uid");
                userInfo = mRealm.where(User.class).equalTo("uid", uid).findFirst();
                String[] memberId = data.get("newAddedUserId").split(",");

                sysContent = new StringBuilder();

                sysContent.append(userInfo.getAppName()).append(MSG_MEMBER_ADD1);
                for (String id : memberId) {
                    User addUser = mRealm.where(User.class).equalTo("uid", id).findFirst();
                    if (addUser != null) {
                        sysContent.append(addUser.getAppName()).append(MSG_MEMBER_ADD2);
                    }
                }
                sysContent.append(MSG_MEMBER_ADD3);


                payload.put("uid", "system");
                payload.put("cid", data.get("cid"));
                payload.put("rid", data.get("rid"));
                payload.put("content", sysContent.toString());
                payload.put("type", data.get("type"));
                payload.put("sendDate", data.get("sendDate"));
                payload.put("isFirst", data.get("isFirst"));
                payload.put("isRead", "true");

                ChatRoomMember.addChatRoomMember(mRealm, rid, memberId);
                ChatContent.createChat(mRealm, payload);
                break;

            case CODE_SYSTEM_MEMBER_EXIT:

                payload = new HashMap<>();

                rid = data.get("rid");
                uid = data.get("uid");

                userInfo = mRealm.where(User.class).equalTo("uid", uid).findFirst();

                sysContent = new StringBuilder();
                sysContent.append(userInfo.getAppName());
                sysContent.append(MSG_MEMBER_EXIT);

                payload.put("rid", data.get("rid"));
                payload.put("uid", "system");
                payload.put("cid", data.get("cid"));
                payload.put("content", sysContent.toString());
                payload.put("type", data.get("type"));
                payload.put("sendDate", data.get("sendDate"));
                payload.put("isFirst", data.get("isFirst"));
                payload.put("isRead", "true");

                ChatRoomMember.deleteChatRoomMember(mRealm, rid, uid);
                ChatContent.createChat(mRealm, payload);
                break;

            case CODE_CHAT_CONTENT_CREATED:
                payload = new HashMap<>();

                rid = data.get("rid");
                uid = data.get("uid");
                cid = data.get("cid");

                payload.put("rid", data.get("rid"));
                payload.put("uid", data.get("uid"));
                payload.put("cid", data.get("cid"));
                payload.put("type", data.get("type"));
                payload.put("sendDate", data.get("sendDate"));
                payload.put("isFirst", data.get("isFirst"));
                payload.put("ext1", data.get("ext1"));
                if (data.get("type").equals("1")) {
                    payload.put("content", EMPTY_IMAGE);
                } else {
                    payload.put("content", data.get("content"));
                }

                if (mRealm.where(ChatRoom.class).equalTo("rid", rid).findAll().isEmpty()) {
                    FirebaseFunctionsManager.getChatRoom(HOSPITAL_ID, rid) //Firebase Functions 함수의 getChatRoom 함수 호출을 통해 FireStore 에 있는 채팅방 데이터 불러옴
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
                                        ChatRoom.createChatRoom(realm1, value, userIdList, new ChatRoom.OnChatRoomCreatedListener() {
                                            @Override
                                            public void onCreate(ChatRoom chatRoom) {
                                                if (!Config.getMyUID(realm1).equals(uid)) {
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Realm realm2 = Realm.getInstance(UtilityManager.getRealmConfig());
                                                            new NotifyManager(mContext, realm2).notify(cid);
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
                    ChatContent.createChat(mRealm, payload);
                    if (!Config.getMyUID(mRealm).equals(uid)) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Realm realm2 = Realm.getInstance(UtilityManager.getRealmConfig());
                                new NotifyManager(mContext, realm2).notify(cid);
                                realm2.close();
                            }
                        }).start();
                    }
                }
                if (data.get("type").equals("1")) {
                    ImageManager imageManager = new ImageManager(this);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Realm realm1 = Realm.getInstance(UtilityManager.getRealmConfig());

                            String url = data.get("content");
                            String name = "thumb_" + data.get("cid") + "(" + data.get("sendDate") + ").jpg";

                            String path = imageManager.saveUrlToCache(url, name);
                            payload.put("content", path);
                            payload.put("ext1", url);
                            ChatContent.createChat(realm1, payload);
                        }
                    }).start();
                }

                break;
        }
        mRealm.close();
    }
}

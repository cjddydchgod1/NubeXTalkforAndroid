/*
 * Created by Jong ho Lee on 19/8/2016.
 * Copyright © 2015 FocusNews. All rights reserved.
 */

package x.com.nubextalk.Manager.FireBase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatRoom;

import static x.com.nubextalk.Module.CodeResources.FIREBASE_LOC;
import static x.com.nubextalk.Module.CodeResources.FUNCTION_CREATE_CHAT_ROOM;
import static x.com.nubextalk.Module.CodeResources.FUNCTION_EXIT_CHAT_ROOM;
import static x.com.nubextalk.Module.CodeResources.FUNCTION_GET_CHAT_ROOM;
import static x.com.nubextalk.Module.CodeResources.FUNCTION_GET_PERSONAL_CHAT_ROOM_ID;
import static x.com.nubextalk.Module.CodeResources.FUNCTION_GET_USER_IN_CHAT_ROOMS_ID;
import static x.com.nubextalk.Module.CodeResources.FUNCTION_NOTIFY_TO_CHAT_ROOM_ADDED_USER;
import static x.com.nubextalk.Module.CodeResources.FUNTION_CREATE_CHAT;
import static x.com.nubextalk.Module.CodeResources.HOSPITAL_ID;

/**
 * FireBase Functions 함수 기능 부분
 * - 각 Functions Name이 Firebase Function에 등록된 함수와 매칭되어야함
 * - Functions의 Callback addCompleteListener 등으로 구현
 * - 참고 : https://firebase.google.com/docs/functions/
 */
public class FirebaseFunctionsManager {

    /**
     * Interface
     **/
    public interface OnCompleteListener {
        void onComplete(String result);
    }

    /**
     * Firebase Functions 통해 FireStore에 채팅방 생성
     *
     * @param data 채팅방 생성 데이터 Map
     * @return
     */
    public static Task<HttpsCallableResult> createChatRoom(@NonNull Map data) {
        return createChatRoom(data, null);
    }

    public static Task<HttpsCallableResult> createChatRoom(@NonNull Map data, OnCompleteListener onCompleteListener) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance(FIREBASE_LOC);
        Map<String, Object> params = new HashMap<>();
        params.put("hid", data.get("hid"));
        params.put("rid", data.get("rid"));
        params.put("uid", data.get("uid"));
        params.put("title", data.get("title"));
        params.put("members", data.get("members"));
        params.put("roomImgUrl", data.get("roomImgUrl"));
        params.put("notificationId", data.get("notificationId"));

        return functions
                .getHttpsCallable(FUNCTION_CREATE_CHAT_ROOM)
                .call(params);

    }

    /**
     * Firebase Functions 통해 FireStore 에 채팅 메세지 생성 후 채팅방 유저들에게 FCM 메세지 보냄
     *
     * @param data
     * @return
     */
    public static Task<HttpsCallableResult> createChat(@NonNull Map data) {
        return createChat(data, null);
    }

    public static Task<HttpsCallableResult> createChat(@NonNull Map data, OnCompleteListener onCompleteListener) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance(FIREBASE_LOC);
        Map<String, Object> params = new HashMap<>();
        params.put("hid", data.get("hid"));
        params.put("rid", data.get("rid"));
        params.put("uid", data.get("uid"));
        params.put("cid", data.get("cid"));
        params.put("type", data.get("type"));
        params.put("content", data.get("content"));
        params.put("ext1", data.get("ext1"));

        return functions
                .getHttpsCallable(FUNTION_CREATE_CHAT)
                .call(params);
    }

    /**
     * Firbase Functions 통해 FireStore 에서 채팅방 가져오기
     *
     * @param hid 병원 id
     * @param rid 채팅방 id
     * @return FireStore 채팅방 데이
     */
    public static Task<HttpsCallableResult> getChatRoom(@NonNull String hid, @NonNull String rid) {
        return getChatRoom(hid, rid, null);
    }

    public static Task<HttpsCallableResult> getChatRoom(@NonNull String hid, @NonNull String rid, OnCompleteListener onCompleteListener) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance(FIREBASE_LOC);
        Map<String, Object> params = new HashMap<>();
        params.put("hid", hid);
        params.put("rid", rid);

        return functions
                .getHttpsCallable(FUNCTION_GET_CHAT_ROOM)
                .call(params);
    }

    /**
     * Firebase Functions 통해 FireStore 에서 사용자가 현재 참여중인 채팅방 id 가져오고 채팅방 id 가 존재하면 realm
     * 로컬에 생성한다.
     *
     * @param hid
     * @param uid
     * @return
     */
    public static Task<HttpsCallableResult> getUserAttendingChatRoom(@NonNull String hid, @NonNull String uid) {
        return getUserAttendingChatRoom(hid, uid, null);
    }

    public static Task<HttpsCallableResult> getUserAttendingChatRoom(@NonNull String hid, @NonNull String uid, OnCompleteListener onCompleteListener) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance(FIREBASE_LOC);
        Map<String, Object> params = new HashMap<>();
        params.put("hid", hid);
        params.put("uid", uid);

        return functions.getHttpsCallable(FUNCTION_GET_USER_IN_CHAT_ROOMS_ID)
                .call(params).addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<HttpsCallableResult>() {
                    @Override
                    public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                        Gson gson = new Gson();
                        try {
                            JSONObject result = new JSONObject(gson.toJson(task.getResult())).getJSONObject("data");

                            // 유저 참여중이던 채팅방 id 가 존재하면 채팅방 데이터륿 불러와서 로컬에 생성해준다.
                            if (result.getInt("code") == 200) {
                                JSONArray dataJsonArray = result.getJSONArray("data");
                                ArrayList<String> ridList = new ArrayList<>();

                                for (int i = 0; i < dataJsonArray.length(); i++) {
                                    ridList.add(dataJsonArray.getString(i));
                                }

                                for (String rid : ridList) {
                                    getChatRoom(hid, rid)
                                            .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<HttpsCallableResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                                                    Map<String, Object> value = new HashMap<>();
                                                    try {
                                                        Realm realm = Realm.getInstance(UtilityManager.getRealmConfig());
                                                        JSONObject chatRoomResult = new JSONObject(gson.toJson(task.getResult().getData())).getJSONObject("chatRoom");
                                                        JSONArray chatRoomMemberResult = new JSONObject(gson.toJson(task.getResult().getData())).getJSONArray("chatRoomMember");
                                                        ArrayList<String> uidList = new ArrayList<>();

                                                        value.put("rid", rid);
                                                        value.put("title", chatRoomResult.getString("roomName"));
                                                        value.put("roomImgUrl", chatRoomResult.getString("roomImg"));
                                                        value.put("updatedDate", chatRoomResult.getString("updatedDate"));
                                                        value.put("notificationId", chatRoomResult.get("notificationId"));

                                                        for (int i = 0; i < chatRoomMemberResult.length(); i++) {
                                                            uidList.add(chatRoomMemberResult.getString(i));
                                                        }

                                                        ChatRoom.createChatRoom(realm, value, uidList, new ChatRoom.OnChatRoomCreatedListener() {
                                                            @Override
                                                            public void onCreate(ChatRoom chatRoom) {

                                                            }
                                                        });

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 기존 채팅방에서 새로운 유저들이 추가되면 해당 유저들에게 fcm 메세지 보내는 함수
     *
     * @param data 병원 id, 유저들 userId, 채팅방 id 가 담긴 데이터 Map
     * @return
     */
    public static Task<HttpsCallableResult> notifyToChatRoomAddedUser(@NonNull Map data) {
        return notifyToChatRoomAddedUser(data, null);
    }

    public static Task<HttpsCallableResult> notifyToChatRoomAddedUser(@NonNull Map data, OnCompleteListener onCompleteListener) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance(FIREBASE_LOC);
        Map<String, Object> params = new HashMap<>();
        params.put("hid", data.get("hid"));
        params.put("rid", data.get("rid"));
        params.put("uid", data.get("uid"));
        params.put("cid", data.get("cid"));
        params.put("membersId", data.get("membersId"));

        return functions
                .getHttpsCallable(FUNCTION_NOTIFY_TO_CHAT_ROOM_ADDED_USER)
                .call(params);
    }

    /**
     * 채팅방을 나가면 FireStore 의 ChatRoom > ChatRoomMember 해당 유저 삭제 함수
     *
     * @param hid 병원 id
     * @param uid     유저 id
     * @param rid 채팅방 id
     * @return
     */
    public static Task<HttpsCallableResult> exitChatRoom(@NonNull String hid, @NonNull String uid, @NonNull String rid) {
        return exitChatRoom(hid, uid, rid, null);
    }

    public static Task<HttpsCallableResult> exitChatRoom(@NonNull String hid, @NonNull String uid, @NonNull String rid, OnCompleteListener onCompleteListener) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance(FIREBASE_LOC);
        Map<String, Object> params = new HashMap<>();
        params.put("hid", hid);
        params.put("rid", rid);
        params.put("roomMemberId", uid);
        return functions
                .getHttpsCallable(FUNCTION_EXIT_CHAT_ROOM)
                .call(params);
    }

    /**
     * 1:1 채팅방 생성 시 FireStore 에 기존 1:1 채팅방 존재 여부 확인 함수
     * onComplete 콜백으로 채팅방이 존재하면 해당 채팅방의 rid 값을 없으면 null 값을 리턴
     *
     * @param hid
     * @param myUid
     * @param otherUid
     * @return
     */
    public static Task<HttpsCallableResult> getPersonalChatRoomId(
            @NonNull String hid, @NonNull String myUid, @NonNull String otherUid) {
        return getPersonalChatRoomId(hid, myUid, otherUid, null);
    }

    public static Task<HttpsCallableResult> getPersonalChatRoomId(
            @NonNull String hid, @NonNull String myUid,
            @NonNull String otherUid, OnCompleteListener onCompleteListener) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance(FIREBASE_LOC);
        Map<String, Object> params = new HashMap<>();
        params.put("hid", hid);
        params.put("myUid", myUid);
        params.put("otherUid", otherUid);
        return functions
                .getHttpsCallable(FUNCTION_GET_PERSONAL_CHAT_ROOM_ID)
                .call(params)
                .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<HttpsCallableResult>() {
                    @Override
                    public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                        String chatRoomId = String.valueOf(task.getResult().getData());
                        onCompleteListener.onComplete(chatRoomId);
                    }
                });
    }
}

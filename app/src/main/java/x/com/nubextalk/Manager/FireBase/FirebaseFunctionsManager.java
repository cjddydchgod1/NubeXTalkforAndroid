/*
 * Created by Jong ho Lee on 19/8/2016.
 * Copyright © 2015 FocusNews. All rights reserved.
 */

package x.com.nubextalk.Manager.FireBase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
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

/**
 * FireBase Functions 함수 기능 부분
 * - 각 Functions Name이 Firebase Function에 등록된 함수와 매칭되어야함
 * - Functions의 Callback addCompleteListener 등으로 구현
 * - 참고 : https://firebase.google.com/docs/functions/
 */
public class FirebaseFunctionsManager {

    public static final String FUNTION_TEST = "executeTest";
    public static final String FUNCTION_CREATE_CHAT_ROOM = "createChatRoom";
    public static final String FUNTION_CREATE_CHAT = "createChat";
    public static final String FUNCTION_GET_CHAT_ROOM = "getChatRoom";


    /**
     * Interface
     **/
    public interface OnCompleteListener {
        void onComplete(String result);
    }

    /**
     * EXECUTE TEST
     */
    public static Task<HttpsCallableResult> executeTest(String token) {
        return executeTest(token, null);
    }

    public static Task<HttpsCallableResult> executeTest(String token, OnCompleteListener onCompleteListener) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();

        Map<String, Object> params = new HashMap<>();
        params.put("token", token);

        return functions
                .getHttpsCallable(FUNTION_TEST)
                .call(params);
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
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        Map<String, Object> params = new HashMap<>();
        params.put("hospital", data.get("hospital"));
        params.put("senderId", data.get("senderId"));
        params.put("chatRoomId", data.get("chatRoomId"));
        params.put("members", data.get("members"));
        params.put("title", data.get("title"));
        params.put("roomImgUrl", data.get("roomImgUrl"));
        params.put("notificationId", data.get("notificationId"));

        return functions
                .getHttpsCallable(FUNCTION_CREATE_CHAT_ROOM)
                .call(params);

    }

    /**
     * Firebase Functions 통해 FireStore 에 채팅 메세지 생성 후 채팅방 유저들에게 FCM 메세지 보냄
     * @param data
     * @return
     */
    public static Task<HttpsCallableResult> createChat(@NonNull Map data) {
        return createChat(data, null);
    }

    public static Task<HttpsCallableResult> createChat(@NonNull Map data, OnCompleteListener onCompleteListener) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance("asia-northeast3");
        Map<String, Object> params = new HashMap<>();
//        params.put("hospitalId", data.get("hid"));
        params.put("hospitalId", "w34qjptO0cYSJdAwScFQ");
        params.put("chatRoomId", data.get("rid"));
        params.put("chatContentId", data.get("cid"));
        params.put("senderId", data.get("uid"));
        params.put("content", data.get("content"));
        params.put("type", data.get("type"));
        params.put("ext1", data.get("ext1"));

        return functions
                .getHttpsCallable(FUNTION_CREATE_CHAT)
                .call(params);
    }

    /**
     * Firbase Functions 통해 FireStore 에서 채팅방 가져오기
     *
     * @param hospitalId 병원 id
     * @param chatRoomId 채팅방 id
     * @return FireStore 채팅방 데이
     */
    public static Task<HttpsCallableResult> getChatRoom(@NonNull String hospitalId, @NonNull String chatRoomId) {
        return getChatRoom(hospitalId, chatRoomId, null);
    }

    public static Task<HttpsCallableResult> getChatRoom(@NonNull String hospitalId, @NonNull String chatRoomId, OnCompleteListener onCompleteListener) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        Map<String, Object> params = new HashMap<>();
        params.put("hospitalId", hospitalId);
        params.put("chatRoomId", chatRoomId);

        return functions
                .getHttpsCallable(FUNCTION_GET_CHAT_ROOM)
                .call(params);
    }

    /**
     * Firebase Functions 통해 FireStore 에서 사용자가 현재 참여중인 채팅방 id 가져오고 채팅방 id 가 존재하면 realm
     * 로컬에 생성한다.
     *
     * @param hospitalId
     * @param userId
     * @return
     */
    public static Task<HttpsCallableResult> getUserAttendingChatRoom(@NonNull String hospitalId, @NonNull String userId) {
        return getUserAttendingChatRoom(hospitalId, userId, null);
    }

    public static Task<HttpsCallableResult> getUserAttendingChatRoom(@NonNull String hospitalId, @NonNull String userId, OnCompleteListener onCompleteListener) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        Map<String, Object> params = new HashMap<>();
        params.put("hospitalId", hospitalId);
        params.put("userId", userId);

        return functions.getHttpsCallable("getUserAttendingChatRoom")
                .call(params).addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<HttpsCallableResult>() {
                    @Override
                    public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                        Gson gson = new Gson();
                        try {
                            JSONObject result = new JSONObject(gson.toJson(task.getResult())).getJSONObject("data");
//                            Log.d("login test", "user chatroom list: " + result.toString());

                            // 유저 참여중이던 채팅방 id 가 존재하면 채팅방 데이터륿 불러와서 로컬에 생성해준다.
                            if (result.getInt("code") == 200) {
                                JSONArray dataJsonArray = result.getJSONArray("data");
                                ArrayList<String> chatRoomIdList = new ArrayList<>();

                                for (int i = 0; i < dataJsonArray.length(); i++) {
                                    chatRoomIdList.add(dataJsonArray.getString(i));
                                }

                                for (String rid : chatRoomIdList) {
//                                    Log.d("login test", "chatroom id: " + rid);
                                    getChatRoom("w34qjptO0cYSJdAwScFQ", rid)
                                            .addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<HttpsCallableResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                                                    Map<String, Object> value = new HashMap<>();
                                                    try {
                                                        Realm realm = Realm.getInstance(UtilityManager.getRealmConfig());
                                                        JSONObject chatRoomResult = new JSONObject(gson.toJson(task.getResult().getData())).getJSONObject("chatRoom");
                                                        JSONArray chatRoomMemberResult = new JSONObject(gson.toJson(task.getResult().getData())).getJSONArray("chatRoomMember");
                                                        ArrayList<String> userIdList = new ArrayList<>();
//                                                        Log.d("login test", "chat room data got: " + chatRoomResult.toString());
                                                        value.put("rid", rid);
                                                        value.put("title", chatRoomResult.getString("roomName"));
                                                        value.put("roomImgUrl", chatRoomResult.getString("roomImg"));
                                                        value.put("updatedDate", chatRoomResult.getString("updatedDate"));
                                                        value.put("notificationId", chatRoomResult.get("notificationId"));

                                                        for (int i = 0; i < chatRoomMemberResult.length(); i++) {
                                                            userIdList.add(chatRoomMemberResult.getString(i));
                                                        }

                                                        ChatRoom.createChatRoom(realm, value, userIdList, new ChatRoom.OnChatRoomCreatedListener() {
                                                            @Override
                                                            public void onCreate(ChatRoom chatRoom) {
//                                                                Log.d("login test", "chat room " + chatRoom.getRoomName() + " created!");
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
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        Map<String, Object> params = new HashMap<>();
        params.put("chatContentId", data.get("chatContentId"));
        params.put("hospitalId", data.get("hospitalId"));
        params.put("membersId", data.get("membersId"));
        params.put("chatRoomId", data.get("chatRoomId"));
        params.put("senderId", data.get("senderId"));

        return functions
                .getHttpsCallable("notifyToChatRoomAddedUser")
                .call(params);
    }

    /**
     * 채팅방을 나가면 FireStore 의 ChatRoom > ChatRoomMember 해당 유저 삭제 함수
     *
     * @param hospitalId 병원 id
     * @param userId     유저 id
     * @param chatRoomId 채팅방 id
     * @return
     */
    public static Task<HttpsCallableResult> exitChatRoom(@NonNull String hospitalId, @NonNull String userId, @NonNull String chatRoomId) {
        return exitChatRoom(hospitalId, userId, chatRoomId, null);
    }

    public static Task<HttpsCallableResult> exitChatRoom(@NonNull String hospitalId, @NonNull String userId, @NonNull String chatRoomId, OnCompleteListener onCompleteListener) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        Map<String, Object> params = new HashMap<>();
        params.put("hospitalId", hospitalId);
        params.put("roomMemberId", userId);
        params.put("chatRoomId", chatRoomId);
        return functions
                .getHttpsCallable("exitChatRoom")
                .call(params);
    }

    /**
     * 1:1 채팅방 생성 시 FireStore 에 기존 1:1 채팅방 존재 여부 확인 함수
     * onComplete 콜백으로 채팅방이 존재하면 해당 채팅방의 rid 값을 없으면 null 값을 리턴
     *
     * @param hospitalId
     * @param myUserId
     * @param anotherUserId
     * @return
     */
    public static Task<HttpsCallableResult> checkIfOneOnOneChatRoomExists(
            @NonNull String hospitalId, @NonNull String myUserId, @NonNull String anotherUserId) {
        return checkIfOneOnOneChatRoomExists(hospitalId, myUserId, anotherUserId, null);
    }

    public static Task<HttpsCallableResult> checkIfOneOnOneChatRoomExists(
            @NonNull String hospitalId, @NonNull String myUserId,
            @NonNull String anotherUserId, OnCompleteListener onCompleteListener) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        Map<String, Object> params = new HashMap<>();
        params.put("hospitalId", hospitalId);
        params.put("myUserId", myUserId);
        params.put("anotherUserId", anotherUserId);
        return functions
                .getHttpsCallable("checkIfOneOnOneChatRoomExists")
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

/*
 * Created by Jong ho Lee on 19/8/2016.
 * Copyright © 2015 FocusNews. All rights reserved.
 */

package x.com.nubextalk.Manager.FireBase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.ChatRoomMember;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.Model.User;

/**
 * FireBase Functions 함수 기능 부분
 * - 각 Functions Name이 Firebase Function에 등록된 함수와 매칭되어야함
 * - Functions의 Callback addCompleteListener 등으로 구현
 * - 참고 : https://firebase.google.com/docs/functions/
 */
public class FirebaseFunctionsManager {

    public static final String FUNTION_TEST = "executeTest";
    public static final String FUNCTION_CREATE_CHAT_ROOM = "createChatRoom";
    public static final String FUNCTION_GET_CHAT_ROOM = "getChatRoom";

    /**
     * Interface
     **/
    public interface OnCompleteListsner {
        void onComplete();
    }

    /**
     * EXECUTE TEST
     */
    public static Task<HttpsCallableResult> executeTest(String token) {
        return executeTest(token, null);
    }

    public static Task<HttpsCallableResult> executeTest(String token, OnCompleteListsner onCompleteListsner) {
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

    public static Task<HttpsCallableResult> createChatRoom(@NonNull Map data, OnCompleteListsner onCompleteListsner) {
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
     * Firbase Functions 통해 FireStore 에서 채팅방 가져오기
     *
     * @param hospitalId 병원 id
     * @param chatRoomId 채팅방 id
     * @return FireStore 채팅방 데이
     */
    public static Task<HttpsCallableResult> getChatRoom(@NonNull String hospitalId, @NonNull String chatRoomId) {
        return getChatRoom(hospitalId, chatRoomId, null);
    }

    public static Task<HttpsCallableResult> getChatRoom(@NonNull String hospitalId, @NonNull String chatRoomId, OnCompleteListsner onCompleteListsner) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        Map<String, Object> params = new HashMap<>();
        params.put("hospitalId", hospitalId);
        params.put("chatRoomId", chatRoomId);

        return functions
                .getHttpsCallable(FUNCTION_GET_CHAT_ROOM)
                .call(params);
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

    public static Task<HttpsCallableResult> notifyToChatRoomAddedUser(@NonNull Map data, OnCompleteListsner onCompleteListsner) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        Map<String, Object> params = new HashMap<>();
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

    public static Task<HttpsCallableResult> exitChatRoom(@NonNull String hospitalId, @NonNull String userId, @NonNull String chatRoomId, OnCompleteListsner onCompleteListsner) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        Map<String, Object> params = new HashMap<>();
        params.put("hospitalId", hospitalId);
        params.put("roomMemberId", userId);
        params.put("chatRoomId", chatRoomId);
        return functions
                .getHttpsCallable("exitChatRoom")
                .call(params);
    }
}

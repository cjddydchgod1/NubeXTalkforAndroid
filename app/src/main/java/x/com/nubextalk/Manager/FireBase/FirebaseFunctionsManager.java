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

    public static Task<HttpsCallableResult> createChatRoom(Map value) {
        return createChatRoom(value, null);
    }


    public static Task<HttpsCallableResult> createChatRoom(Map value, OnCompleteListsner onCompleteListsner) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();

        Map<String, Object> params = new HashMap<>();
        params.put("hospital", value.get("hospital"));
        params.put("chatRoomId", value.get("chatRoomId"));
        params.put("members", value.get("members"));
        params.put("title", value.get("title"));
        params.put("roomImgUrl", value.get("roomImgUrl"));
        params.put("notificationId", value.get("notificationId"));

        return functions
                .getHttpsCallable(FUNCTION_CREATE_CHAT_ROOM)
                .call(params);

    }

    public static Task<HttpsCallableResult> getChatRoom(String hospitalId, String chatRoomId) {
        return getChatRoom(hospitalId, chatRoomId, null);
    }

    public static Task<HttpsCallableResult> getChatRoom(String hospitalId, String chatRoomId, OnCompleteListsner onCompleteListsner) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        Map<String, Object> params = new HashMap<>();
        params.put("hospitalId", hospitalId);
        params.put("chatRoomId", chatRoomId);

        return functions
                .getHttpsCallable(FUNCTION_GET_CHAT_ROOM)
                .call(params)
                .continueWith(new Continuation<HttpsCallableResult, HttpsCallableResult>() {
                    @Override
                    public HttpsCallableResult then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        if (task.isSuccessful()) {
                            Realm realm = Realm.getInstance(UtilityManager.getRealmConfig());
                            Gson gson = new Gson();
                            JSONObject result = new JSONObject(gson.toJson(task.getResult().getData()));
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    try {
                                        ChatRoom chatRoom = gson.
                                                fromJson(result.getJSONObject("chatRoom").toString(), ChatRoom.class);
                                        realm.copyToRealmOrUpdate(chatRoom);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            });
                            String rid = result.getJSONObject("chatRoom").getString("rid");
                            ArrayList<String> memberIdList = new ArrayList<String>();
                            for (int i = 0; i < result.getJSONArray("chatRoomMember").length(); i++) {
                                memberIdList.add(result.getJSONArray("chatRoomMember").getString(i));
                            }
                            for (int i = 0; i < memberIdList.size(); i++) {
                                int finalI = i;
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        ChatRoomMember chatRoomMember = new ChatRoomMember();
                                        chatRoomMember.setRid(rid);
                                        chatRoomMember.setUid(memberIdList.get(finalI));
                                        realm.copyToRealm(chatRoomMember);
                                    }
                                });
                            }
                            if (memberIdList.size() == 2 &&
                                    result.getJSONObject("chatRoom").getString("roomName").length() == 0) { // 1:1 채팅방일 때
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        String myId = Config.getMyUID(realm);
                                        String anotherId = "";
                                        for (String id : memberIdList) {
                                            if (!id.equals(myId)) {
                                                anotherId = id;
                                            }
                                        }
                                        User anotherUser = realm.where(User.class).equalTo("userId", anotherId).findFirst();
                                        ChatRoom chatRoom = realm.where(ChatRoom.class).equalTo("rid", rid).findFirst();
                                        chatRoom.setRoomName(anotherUser.getAppName());
                                        realm.copyToRealmOrUpdate(chatRoom);
                                    }
                                });
                            }
                            return task.getResult();
                        }
                        return null;
                    }
                });
    }

    public static Task<HttpsCallableResult> notifyToChatRoomAddedUser(Map value) {
        return notifyToChatRoomAddedUser(value, null);
    }

    public static Task<HttpsCallableResult> notifyToChatRoomAddedUser(Map value, OnCompleteListsner onCompleteListsner) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        return functions
                .getHttpsCallable("notifyToChatRoomAddedUser")
                .call(value);
    }
}

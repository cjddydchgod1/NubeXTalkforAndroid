/*
 * Created by Jong ho Lee on 19/8/2016.
 * Copyright © 2015 FocusNews. All rights reserved.
 */

package x.com.nubextalk.Manager.FireBase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.api.Http;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.realm.Realm;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.ChatRoomMember;

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

    public static Task<HttpsCallableResult> createChatRoom(String token, Map value) {
        return createChatRoom(token, value, null);
    }


    public static Task<HttpsCallableResult> createChatRoom(String token, Map value, OnCompleteListsner onCompleteListsner) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();

        Map<String, Object> params = new HashMap<>();
        params.put("token", token);
        params.put("hospital", value.get("hospital"));
        params.put("members", value.get("members"));
        params.put("title", value.get("title"));
        params.put("roomImgUrl", value.get("roomImgUrl"));

        return functions.getHttpsCallable(FUNCTION_CREATE_CHAT_ROOM)
                .call(params)
                .continueWith(new Continuation<HttpsCallableResult, HttpsCallableResult>() {
                                  @Override
                                  public HttpsCallableResult then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                                      if (task.isSuccessful()) {
                                          Gson gson = new Gson();
                                          JSONObject result = new JSONObject(gson.toJson(task.getResult().getData()));
                                          Realm realm = Realm.getInstance(UtilityManager.getRealmConfig());
                                          realm.executeTransaction(new Realm.Transaction() {
                                              @Override
                                              public void execute(Realm realm) {
                                                  try {
                                                      String rid = result.getString("rid");
                                                      ChatRoom chatRoom = gson.fromJson(result.toString(), ChatRoom.class);
                                                      realm.copyToRealmOrUpdate(chatRoom);

                                                  } catch (JSONException e) {
                                                      e.printStackTrace();
                                                  }

                                              }
                                          });
                                          String rid = result.getString("rid");
                                          ArrayList<String> memberIdList = new ArrayList<String>();
                                          for (int i = 0; i < result.getJSONArray("roomMemberId").length(); i++) {
                                              memberIdList.add(result.getJSONArray("roomMemberId").getString(i));
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
                                      }
                                      return null;
                                  }
                              }
                );
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

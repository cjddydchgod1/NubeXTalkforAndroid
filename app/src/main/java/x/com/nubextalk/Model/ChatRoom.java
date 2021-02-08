/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Model;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import x.com.nubextalk.Manager.DateManager;
import x.com.nubextalk.Manager.FireBase.FirebaseFunctionsManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.R;


public class ChatRoom extends RealmObject {
    @NonNull
    @PrimaryKey
    private String rid;
    @NonNull
    private String roomName;
    @NonNull
    private String roomImg;
    private Boolean settingAlarm = true;
    private Boolean settingFixTop = false;
    @NonNull
    private Date updatedDate;
    private String notificationId;
    private int memberCount;
    private Boolean isGroupChat = false;

    @NonNull
    public String getRid() {
        return rid;
    }

    public void setRid(@NonNull String rid) {
        this.rid = rid;
    }

    @NonNull
    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(@NonNull String roomName) {
        this.roomName = roomName;
    }

    @NonNull
    public String getRoomImg() {
        return roomImg;
    }

    public void setRoomImg(@NonNull String roomImg) {
        this.roomImg = roomImg;
    }

    public Boolean getSettingAlarm() {
        return settingAlarm;
    }

    public void setSettingAlarm(Boolean settingAlarm) {
        this.settingAlarm = settingAlarm;
    }

    public Boolean getSettingFixTop() {
        return settingFixTop;
    }

    public void setSettingFixTop(Boolean settingFixTop) {
        this.settingFixTop = settingFixTop;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    @NonNull
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(@NonNull Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @NonNull
    public Boolean getIsGroupChat() {
        return isGroupChat;
    }

    public void setIsGroupChat(Boolean isGroupChat) {
        this.isGroupChat = isGroupChat;
    }

    /**
     * Data 초기화 함수
     *
     * @param realm
     */
    public static void init(Context context, Realm realm) {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(UtilityManager.loadJson(context, "example_chat_room.json")); //json 파일 추가
            RealmList<ChatRoom> list = new RealmList<>();
            for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                String rid = it.next();
                jsonObject.getJSONObject(rid).put("rid", rid);
                jsonArray.put(jsonObject.getJSONObject(rid));
            }

            realm.executeTransaction(realm1 -> {
                realm1.where(ChatRoom.class).findAll().deleteAllFromRealm();
                realm1.createOrUpdateAllFromJson(ChatRoom.class, jsonArray);
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static RealmResults<ChatRoom> getAll(Realm realm) {
        return realm.where(ChatRoom.class).findAll();
    }

    public interface onChatRoomCreatedListener {
        void onCreate(ChatRoom chatRoom);
    }

    /**
     * realm ChatRoom 과 ChatRoomMember 생성
     *
     * @param realm
     * @param data     채팅방 생성을 위한 데이터 Map
     * @param userList 채팅방 참여 사용자 (userId) 가 담긴 ArrayList
     */
    public static void createChatRoom(Realm realm, Map data, ArrayList<String> userList, onChatRoomCreatedListener onChatRoomCreatedListener) {
        User myAccount = (User) User.getMyAccountInfo(realm);

        // userList 에 자신의 아이디 추가
        if (!userList.contains(myAccount.getUserId())) {
            userList.add(myAccount.getUserId());
        }

        // 채팅방 데이터 필드 초기화
        Date newDate = new Date();
        final String[] rid = {data.get("rid") == null ? myAccount.getUserId().concat(String.valueOf(newDate.getTime())) : data.get("rid").toString()};
        String roomName = data.get("title") == null ? "" : data.get("title").toString();
        String roomImg = data.get("roomImgUrl") == null ? "" : data.get("roomImgUrl").toString();
        Date updatedDate = data.get("updatedDate") == null
                ? newDate : DateManager.convertDatebyString(data.get("updatedDate").toString(), "yyyy-MM-dd'T'HH:mm:ss");
        String notificationId = data.get("notificationId") == null
                ? String.valueOf(newDate.hashCode()) : data.get("notificationId").toString();
        Boolean isGroupChat = data.get("isGroupChat") == null ? false : (Boolean) data.get("isGroupChat");

        int memberCount = userList.size();
        if (memberCount < 3) { //1:1 채팅방일 때 채팅방 이름, 사진 상대방 유저로 설정
            for (String userId : userList) {
                if (!userId.equals(myAccount.getUserId())) {
                    User user = realm.where(User.class).equalTo("userId", userId).findFirst();
                    roomName = user.getAppName();
                    roomImg = user.getAppImagePath();
                    isGroupChat = false;
                }
            }
        } else { // 단체 채팅방일 때, 채팅방 사진을 기본 단체채팅방 사진으로 설정
            roomImg = String.valueOf(R.drawable.ic_twotone_group_24);
            isGroupChat = true;
        }

        String finalRoomName = roomName;
        String finalRoomImg = roomImg;
        Boolean finalIsGroupChat = isGroupChat;

        // realm 로컬 채팅방 생성
        if (memberCount == 2) { // 1:1 채팅방 생성인 경우 FireStore 에 기존 채팅방 존재 여부 확인
            FirebaseFunctionsManager.checkIfOneOnOneChatRoomExists(
                    "w34qjptO0cYSJdAwScFQ", myAccount.getUserId(), userList.get(0),
                    new FirebaseFunctionsManager.OnCompleteListener() {
                        @Override
                        public void onComplete(String result) {
                            String chatRoomId = result;
                            if (!chatRoomId.equals("null")) { //기존 1:1 채팅방 존재 시 해당 rid 값으로 대체
                                rid[0] = chatRoomId;
                                Log.d("CHATROOM", "rid: " + rid[0]);
                            }
                            ChatRoom chatRoom = new ChatRoom();
                            chatRoom.setRid(rid[0]);
                            chatRoom.setRoomName(finalRoomName);
                            chatRoom.setRoomImg(finalRoomImg);
                            chatRoom.setUpdatedDate(updatedDate);
                            chatRoom.setNotificationId(notificationId);
                            chatRoom.setMemeberCount(memberCount);
                            chatRoom.setIsGroupChat(finalIsGroupChat);
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(@NonNull Realm realm) {
                                    realm.copyToRealmOrUpdate(chatRoom);

                                    //ChatRoomMember 모델에 채팅유저 생성
                                    for (String userId : userList) {
                                        //채팅방 목록에 없는 유저일 경우 ChatRoomMember 모델 생성
                                        if (!realm.where(User.class).equalTo("userId", userId).findAll().isEmpty()
                                                && realm.where(ChatRoomMember.class).equalTo("rid", rid[0]).and().equalTo("uid", userId).findAll().isEmpty()) {
                                            User user = realm.where(User.class).equalTo("userId", userId).findFirst();
                                            ChatRoomMember chatRoomMember = new ChatRoomMember();
                                            chatRoomMember.setRid(rid[0]);
                                            chatRoomMember.setUid(user.getUserId());
                                            realm.copyToRealm(chatRoomMember);
                                        }
                                    }
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    if (onChatRoomCreatedListener != null) {
                                        onChatRoomCreatedListener.onCreate(chatRoom);
                                    }
                                }
                            });
                        }
                    });
        } else { // 단체 채팅방 생성
            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setRid(rid[0]);
            chatRoom.setRoomName(finalRoomName);
            chatRoom.setRoomImg(finalRoomImg);
            chatRoom.setUpdatedDate(updatedDate);
            chatRoom.setNotificationId(notificationId);
            chatRoom.setMemeberCount(memberCount);
            chatRoom.setIsGroupChat(finalIsGroupChat);
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    realm.copyToRealmOrUpdate(chatRoom);

                    //ChatRoomMember 모델에 채팅유저 생성
                    for (String userId : userList) {
                        //채팅방 목록에 없는 유저일 경우 ChatRoomMember 모델 생성
                        if (!realm.where(User.class).equalTo("userId", userId).findAll().isEmpty()
                                && realm.where(ChatRoomMember.class).equalTo("rid", rid[0]).and().equalTo("uid", userId).findAll().isEmpty()) {
                            User user = realm.where(User.class).equalTo("userId", userId).findFirst();
                            ChatRoomMember chatRoomMember = new ChatRoomMember();
                            chatRoomMember.setRid(rid[0]);
                            chatRoomMember.setUid(user.getUserId());
                            realm.copyToRealm(chatRoomMember);
                        }
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    if (onChatRoomCreatedListener != null) {
                        onChatRoomCreatedListener.onCreate(chatRoom);
                    }

                }
            });
        }

    }

    /**
     * realm ChatRoom, ChatContent, ChatRoomMember 모두 삭제
     *
     * @param realm
     * @param rid   채팅방 rid
     */
    public static void deleteChatRoom(Realm realm, String rid) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ChatRoom chatRoom = realm.where(ChatRoom.class).equalTo("rid", rid).findFirst();
                RealmResults<ChatContent> chatContents = realm.where(ChatContent.class).equalTo("rid", rid).findAll();
                RealmResults<ChatRoomMember> chatRoomMembers = realm.where(ChatRoomMember.class).equalTo("rid", rid).findAll();
                chatRoom.deleteFromRealm();
                chatContents.deleteAllFromRealm();
                chatRoomMembers.deleteAllFromRealm();
            }
        });
    }

    /**
     * 채팅방 참여 사용자를 반환
     *
     * @param realm
     * @param rid   채팅방 rid
     * @return RealmResults
     */
    public static RealmResults<ChatRoomMember> getChatRoomUsers(Realm realm, String rid) {
        return realm.where(ChatRoomMember.class).equalTo("rid", rid).findAll();

    }

    public int getMemeberCount() {
        return memberCount;
    }

    public void setMemeberCount(int memberCount) {
        this.memberCount = memberCount;
    }
}

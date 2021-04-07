/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Model;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import x.com.nubextalk.Manager.DateManager;
import x.com.nubextalk.Manager.FireBase.FirebaseFunctionsManager;
import x.com.nubextalk.R;

import static x.com.nubextalk.Module.CodeResources.DATE_FORMAT4;
import static x.com.nubextalk.Module.CodeResources.HOSPITAL_ID;


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
    @NonNull
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
    public int getMemeberCount() {
        return memberCount;
    }

    public void setMemeberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    @NonNull
    public Boolean getIsGroupChat() {
        return isGroupChat;
    }

    public void setIsGroupChat(Boolean isGroupChat) {
        this.isGroupChat = isGroupChat;
    }

    public static RealmResults<ChatRoom> getAll(Realm realm) {
        return realm.where(ChatRoom.class).findAll();
    }

    public interface OnChatRoomCreatedListener {
        void onCreate(ChatRoom chatRoom);
    }

    /**
     * realm ChatRoom 과 ChatRoomMember 생성
     *
     * @param realm
     * @param data     채팅방 생성을 위한 데이터 Map
     * @param userList 채팅방 참여 사용자 (userId) 가 담긴 ArrayList
     */
    public static void createChatRoom(Realm realm, Map data, ArrayList<String> userList, @NonNull OnChatRoomCreatedListener onChatRoomCreatedListener) {
        User myAccount = (User) User.getMyAccountInfo(realm);

        // userList 에 자신의 아이디 추가
        if (!userList.contains(myAccount.getUid())) {
            userList.add(myAccount.getUid());
        }

        // 채팅방 데이터 필드 초기화
        Date newDate = new Date();
        final String[] rid = {data.get("rid") == null ? myAccount.getUid().concat(String.valueOf(newDate.getTime())) : data.get("rid").toString()};
        String roomName = data.get("title") == null ? "" : data.get("title").toString();
        String roomImg = data.get("roomImgUrl") == null ? "" : data.get("roomImgUrl").toString();
        Date updatedDate = data.get("updatedDate") == null
                ? newDate : DateManager.convertDatebyString(data.get("updatedDate").toString(), DATE_FORMAT4);
        String notificationId = data.get("notificationId") == null
                ? String.valueOf(newDate.hashCode()) : data.get("notificationId").toString();
        Boolean isGroupChat = data.get("isGroupChat") == null ? false : (Boolean) data.get("isGroupChat");

        int memberCount = userList.size();
        if (memberCount < 3) { //1:1 채팅방일 때 채팅방 이름, 사진 상대방 유저로 설정
            for (String userId : userList) {
                if (!userId.equals(myAccount.getUid())) {
                    User user = realm.where(User.class).equalTo("uid", userId).findFirst();
                    roomName = user.getAppName();
                    roomImg = user.getAppImagePath();
                    isGroupChat = false;
                }
            }
        } else { // 단체 채팅방일 때, 채팅방 사진을 기본 단체채팅방 사진으로 설정
            roomImg = String.valueOf(R.drawable.ic_people_gray_24);
            isGroupChat = true;
        }

        String finalRoomName = roomName;
        String finalRoomImg = roomImg;
        Boolean finalIsGroupChat = isGroupChat;


        // realm 로컬 채팅방 생성
        if (memberCount == 2) { // 1:1 채팅방 생성인 경우 FireStore 에 기존 채팅방 존재 여부 확인

            String anotherUserId = null;
            for (String userId : userList) {
                if (!userId.equals(myAccount.getUid())) {
                    anotherUserId = userId;
                }
            }

            FirebaseFunctionsManager.getPersonalChatRoomId(

                    HOSPITAL_ID, myAccount.getUid(), anotherUserId,
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
                            chatRoom.setIsGroupChat(finalIsGroupChat);
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(@NonNull Realm realm) {
                                    realm.copyToRealmOrUpdate(chatRoom);
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    //ChatRoomMember 모델에 채팅유저 생성
                                    for (String uid : userList) {
                                        ChatRoomMember.addChatRoomMember(realm, rid[0], uid, new ChatRoomMember.OnChatRoomMemberListener() {
                                            @Override
                                            public void onCreate() {
                                            }
                                        });
                                    }
                                    onChatRoomCreatedListener.onCreate(chatRoom);

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
            chatRoom.setIsGroupChat(finalIsGroupChat);
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    realm.copyToRealmOrUpdate(chatRoom);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    //ChatRoomMember 모델에 채팅유저 생성
                    for (String uid : userList) {
                        ChatRoomMember.addChatRoomMember(realm, rid[0], uid, new ChatRoomMember.OnChatRoomMemberListener() {
                            @Override
                            public void onCreate() {
                            }
                        });
                    }
                    onChatRoomCreatedListener.onCreate(chatRoom);
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
}

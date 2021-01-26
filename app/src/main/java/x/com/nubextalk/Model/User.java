/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Model;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Iterator;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import x.com.nubextalk.Manager.UtilityManager;

public class User extends RealmObject {
    @NonNull
    @PrimaryKey
    private String code; // user id(고유)
    @NonNull
    private String userId; // id(로그인)
    private String lastName; // ??
    private String typeCode; // 직책번호
    private String typeCodeName; // 직책
    private String removed; // ??

    private String appImagePath; // image값
    private String appStatus; // 상태정보
    private String appName; // 이름
    private String appFcmKey; // FCM key값 저장
    private String appNickName;
    @NonNull
    public String getCode() {
        return code;
    }

    public void setCode(@NonNull String code) {
        this.code = code;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeCodeName() {
        return typeCodeName;
    }

    public void setTypeCodeName(String typeCodeName) {
        this.typeCodeName = typeCodeName;
    }

    public String getRemoved() {
        return removed;
    }

    public void setRemoved(String removed) {
        this.removed = removed;
    }

    public String getAppImagePath() {
        return appImagePath;
    }

    public void setAppImagePath(String appImagePath) {
        this.appImagePath = appImagePath;
    }

    public String getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(String appStatus) {
        if(UtilityManager.checkString(appStatus))
            this.appStatus = appStatus;
        else
            this.appStatus = "0";
    }

    public String getAppName() {
        if(UtilityManager.checkString(this.appNickName))
            return this.appNickName;
        else
            return appName;
    }

    public void setAppName(String appName) {
        this.appName = lastName;
    }

    public String getAppFcmKey() {
        return appFcmKey;
    }

    public void setAppFcmKey(String appFcmKey) {
        this.appFcmKey = appFcmKey;
    }

    public String getAppNickName() {
        return appNickName;
    }

    public void setAppNickName(String appNickName) {
        this.appNickName = appNickName;
    }

    public static RealmObject getMyAccountInfo(Realm realm) {
        return realm.where(User.class).equalTo("userId", Config.getMyAccount(realm).getExt1()).findFirst();
    }
    public static RealmResults<User> getUserlist(Realm realm) {
        return realm.where(User.class).notEqualTo("userId", Config.getMyAccount(realm).getExt1()).findAll();
    }

    public static ChatRoom getChatroom(Realm realm, User user) {
        RealmResults<ChatRoomMember> chatList = realm.where(ChatRoomMember.class).equalTo("uid", user.getUserId()).findAll();
        ChatRoom chatRoom = null;
        if(chatList.size() != 0) {

                Iterator<ChatRoomMember> mChat;
                /**
                 * 1대1채팅방을 찾았다면 그 값이 chatRoom
                 * 찾지 못했다면 chatRoom은 null값
                 */
                for(mChat = chatList.iterator(); chatRoom == null ;mChat.hasNext()) {
                    chatRoom = realm.where(ChatRoom.class)
                            .equalTo("rid", mChat.next().getRid())
                            .and()
                            .equalTo("isGroupChat", false).findFirst();
                }
            }
        return chatRoom;
    }
}

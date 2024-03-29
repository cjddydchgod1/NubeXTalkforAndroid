/*
 * Created By JONGHO, LEE, Researcher of Institute of Biomedical Engineering, TechHeim Co.,Ltd.
 * on  2021.
 * This software is not stable health-care system and not certificated any health-care authority(like CE, FDA)
 * Prohibit redistribution, modification, commercial use.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Model;

import androidx.annotation.NonNull;

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
    private String uid; // id(로그인)
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
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
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
        if (UtilityManager.checkString(appStatus))
            this.appStatus = appStatus;
        else
            this.appStatus = "0";
    }

    public String getAppName() {
        if (UtilityManager.checkString(this.appNickName))
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

    public static User getMyAccountInfo(Realm realm) {
        return realm.where(User.class).equalTo("uid", Config.getMyAccount(realm).getExt1()).findFirst();
    }

    public static RealmResults<User> getUserlist(Realm realm) {
        return realm.where(User.class).notEqualTo("uid", Config.getMyAccount(realm).getExt1()).findAll();
    }

    public interface UserListener {
        void onFindPersonalChatRoom(ChatRoom chatRoom);
    }

    public static void getChatroom(Realm realm, User user, UserListener userListener) {
        RealmResults<ChatRoomMember> chatList = realm.where(ChatRoomMember.class).equalTo("uid", user.getUid()).findAll();
        ChatRoom chatRoom = null;

        if (chatList.size() != 0) {

            /**s
             * 1대1채팅방을 찾았다면 그 값이 chatRoom
             * 찾지 못했다면 chatRoom은 null값
             */
            for (ChatRoomMember chatRoomMember : chatList) {
                chatRoom = realm.where(ChatRoom.class)
                        .equalTo("rid", chatRoomMember.getRid())
                        .and()
                        .equalTo("isGroupChat", false).findFirst();
            }
        }
        userListener.onFindPersonalChatRoom(chatRoom);
    }
}

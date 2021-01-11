/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Model;

import androidx.annotation.NonNull;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User2 extends RealmObject {
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
        this.appStatus = appStatus;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppFcmKey() {
        return appFcmKey;
    }

    public void setAppFcmKey(String appFcmKey) {
        this.appFcmKey = appFcmKey;
    }

}

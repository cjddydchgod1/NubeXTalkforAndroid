/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Model;

import android.content.Context;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import x.com.nubextalk.Manager.DateManager;
import x.com.nubextalk.Manager.UtilityManager;

public class ChatContent extends RealmObject {
    @NonNull
    @PrimaryKey
    private String cid;
    @NonNull
    private String rid;
    @NonNull
    private String uid;
    @NonNull
    private int type; // 0 = 일반 텍스트, 1 = 사진 , 9 = system
    @NonNull
    private String content;
    @NonNull
    private Date sendDate;

    @NonNull
    private Boolean isRead = false;

    @NonNull
    private Boolean isFirst = true;

    @NonNull
    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    @NonNull
    public String getRid() {
        return rid;
    }

    public void setRid(@NonNull String rid) {
        this.rid = rid;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public int getType() {
        return type;
    }

    public void setType(@NonNull int type) {
        this.type = type;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

    @NonNull
    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    @NonNull
    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    @NonNull
    public Boolean getFirst() {
        return isFirst;
    }

    public void setFirst(@NonNull Boolean first) {
        isFirst = first;
    }

    public static RealmResults<ChatContent> getAll(Realm realm) {
        return realm.where(ChatContent.class).findAll();
    }

    public static void createChat(Realm realm, Map data) {
        User myAccount = (User) User.getMyAccountInfo(realm);
        Date newDate = new Date();

        String uid = data.get("uid").toString();
        String cid = data.get("cid") == null
                ? uid.concat(String.valueOf(newDate.getTime())) : data.get("cid").toString();
        String rid = data.get("rid").toString();
        String content = data.get("content").toString();
        Integer type = Integer.parseInt(data.get("type").toString());
        Date sendDate = data.get("sendDate") == null
                ? newDate : DateManager.convertDatebyString(data.get("sendDate").toString(), "yyyy-MM-dd'T'HH:mm:ss");
        Boolean isFirst = Boolean.parseBoolean(data.get("isFirst").toString());
        Boolean isRead = myAccount.getUserId().equals(uid) ? true : false;

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ChatContent chatContent = new ChatContent();
                chatContent.setCid(cid);
                chatContent.setRid(rid);
                chatContent.setUid(uid);
                chatContent.setContent(content);
                chatContent.setType(type);
                chatContent.setSendDate(sendDate);
                chatContent.setIsRead(isRead);
                chatContent.setFirst(isFirst);
                realm.copyToRealmOrUpdate(chatContent);
                ChatRoom chatRoom = realm.where(ChatRoom.class).equalTo("rid", rid).findFirst();
                chatRoom.setUpdatedDate(sendDate);
                realm.copyToRealmOrUpdate(chatRoom);
            }
        });
    }

}

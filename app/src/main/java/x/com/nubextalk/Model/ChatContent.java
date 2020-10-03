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

import java.util.Date;
import java.util.Iterator;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
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
    private int type;
    @NonNull
    private String content;
    @NonNull
    private Date sendDate;

    @NonNull
    public String getCid() {
        return cid;
    }

    public void setCid(@NonNull String cid) { this.cid = cid; }

    @NonNull
    public String getRid() {
        return rid;
    }

    public void setRid(@NonNull String rid) { this.rid = rid; }

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) { this.uid = uid; }

    public int getType() {
        return type;
    }

    public void setType(@NonNull int type) { this.type = type; }

    @NonNull
    public String getContent() {
        return content;
    }

    public void setContent(@NonNull String content) { this.content = content; }

    @NonNull
    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(@NonNull Date sendDate) { this.sendDate = sendDate; }

    /**
     * Data 초기화 함수
     *
     * @param realm
     */
    public static void init(Context context, Realm realm) {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(UtilityManager.loadJson(context, "example_chat_content.json")); //json 파일 추가
            RealmList<ChatContent> list = new RealmList<>();
            for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                String cid = it.next();
                jsonObject.getJSONObject(cid).put("cid", cid);
                jsonArray.put(jsonObject.getJSONObject(cid));
            }


            realm.executeTransaction(realm1 -> {
                realm1.where(ChatContent.class).findAll().deleteAllFromRealm();
                realm1.createOrUpdateAllFromJson(ChatContent.class, jsonArray);
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static RealmResults<ChatContent> getAll(Realm realm) {
        return realm.where(ChatContent.class).findAll();
    }
}

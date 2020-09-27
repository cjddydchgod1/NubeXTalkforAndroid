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

    public void ChatContent(@NonNull String cid, @NonNull String rid, @NonNull String uid,
                            @NonNull int type, @NonNull String content, @NonNull Date sendDate) {
        this.cid = cid;
        this.rid = rid;
        this.uid = uid;
        this.type = type;
        this.content = content;
        this.sendDate = sendDate;
    }

    @NonNull
    public String getCid() {
        return cid;
    }

    @NonNull
    public String getRid() {
        return rid;
    }

    @NonNull
    public String getUid() {
        return uid;
    }

    public int getType() {
        return type;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    @NonNull
    public Date getSendDate() {
        return sendDate;
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
            jsonObject = new JSONObject(UtilityManager.loadJson(context, "")); //json 파일 추가
            RealmList<Example_Model_Address> list = new RealmList<>();
            for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                String cid = it.next();
                jsonObject.getJSONObject(cid).put("cid", cid);
                jsonArray.put(jsonObject.getJSONObject(cid));
            }

            realm.executeTransaction(realm1 -> {
                realm1.where(Example_Model_Address.class).findAll().deleteAllFromRealm();
                realm1.createOrUpdateAllFromJson(Example_Model_Address.class, jsonArray);
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static RealmResults<Example_Model_Address> getAll(Realm realm) {
        return realm.where(Example_Model_Address.class).findAll();
    }
}

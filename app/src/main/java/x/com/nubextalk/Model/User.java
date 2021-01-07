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

import java.util.Iterator;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import x.com.nubextalk.Manager.UtilityManager;

public class User extends RealmObject {
    @NonNull
    @PrimaryKey
    private String uid;
    @NonNull
    private String name;
    private String nickname;
    @NonNull
    private String profileImg;
    @NonNull
    private int status;
    @NonNull
    private String department;

    @NonNull
    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) { this.uid = uid; }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) { this.name = name; }

    @NonNull
    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(@NonNull String profileImg) {
        this.profileImg = profileImg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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
            jsonObject = new JSONObject(UtilityManager.loadJson(context, "example_user.json")); //json 파일 추가
            RealmList<User> list = new RealmList<>();
            for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                String uid = it.next();
                jsonObject.getJSONObject(uid).put("uid", uid);
                jsonArray.put(jsonObject.getJSONObject(uid));
            }

            realm.executeTransaction(realm1 -> {
                realm1.where(User.class).findAll().deleteAllFromRealm();
                realm1.createOrUpdateAllFromJson(User.class, jsonArray);
            });
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static RealmResults<User> getAll(Realm realm){
        return realm.where(User.class).findAll();
    }

}

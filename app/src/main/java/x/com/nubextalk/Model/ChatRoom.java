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

    @NonNull
    public String getRid() {
        return rid;
    }

    public void setRid(@NonNull String rid) { this.rid = rid; }

    @NonNull
    public String getRoomName() { return roomName; }

    public void setRoomName(@NonNull String roomName) { this.roomName = roomName; }

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

    @NonNull
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(@NonNull Date updatedDate) {
        this.updatedDate = updatedDate;
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
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static RealmResults<ChatRoom> getAll(Realm realm){
        return realm.where(ChatRoom.class).findAll();
    }



}

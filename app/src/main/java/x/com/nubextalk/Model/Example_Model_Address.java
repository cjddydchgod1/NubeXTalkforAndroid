/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import androidx.annotation.NonNull;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import x.com.nubextalk.Manager.UtilityManager;

/**
 * 예제용 Model
 */
public class Example_Model_Address extends RealmObject {
    @NonNull
    @PrimaryKey
    String oid;
    String tid;
    String name;
    String desc;
    String address;
    String phone;
    String email;
    String typeCode;
    String typeFont;
    String typeText;
    String runtime;
    String rating;
    String website;
    String url;
    boolean removed;
    String showdate;

    @NonNull
    public String getOid() {
        return oid;
    }
    public void setOid(@NonNull String oid) {
        this.oid = oid;
    }
    public String getTid() {
        return tid;
    }
    public void setTid(String tid) {
        this.tid = tid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getTypeCode() {
        return typeCode;
    }
    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
    public String getTypeFont() {
        return typeFont;
    }
    public void setTypeFont(String typeFont) {
        this.typeFont = typeFont;
    }
    public String getTypeText() {
        return typeText;
    }
    public void setTypeText(String typeText) {
        this.typeText = typeText;
    }
    public String getRuntime() {
        return runtime;
    }
    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }
    public String getRating() {
        return rating;
    }
    public void setRating(String rating) {
        this.rating = rating;
    }
    public String getWebsite() {
        return website;
    }
    public void setWebsite(String website) {
        this.website = website;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public boolean getRemoved() {
        return removed;
    }
    public void setRemoved(boolean removed) {
        this.removed = removed;
    }
    public String getShowdate() {
        return showdate;
    }
    public void setShowdate(String showdate) {
        this.showdate = showdate;
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
            jsonObject = new JSONObject(UtilityManager.loadJson(context, "example_address.json"));
            RealmList<Example_Model_Address> list = new RealmList<>();
            for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                String oid = it.next();
                jsonObject.getJSONObject(oid).put("oid", oid);
                jsonArray.put(jsonObject.getJSONObject(oid));
            }

            realm.executeTransaction(realm1 -> {
                realm1.where(Example_Model_Address.class).findAll().deleteAllFromRealm();
                realm1.createOrUpdateAllFromJson(Example_Model_Address.class, jsonArray);
            });
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static RealmResults<Example_Model_Address> getAll(Realm realm){
        return realm.where(Example_Model_Address.class).findAll();
    }

}


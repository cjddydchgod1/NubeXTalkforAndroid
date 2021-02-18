/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.UUID;

import androidx.annotation.NonNull;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import x.com.nubextalk.Manager.UtilityManager;

public class Config extends RealmObject {
    @NonNull
    @PrimaryKey
    private String oid;
    @NonNull
    private String CODENAME;
    @NonNull
    private String CODE;
    private String ext1;
    private String ext2;
    private String ext3;
    private String ext4;
    private boolean autoLogin;
    private boolean screenLock;
    private boolean alarm;

    public Config () {
        screenLock = false;
        alarm = true;
    }

    public String getOid() {
        return oid;
    }
    public void setOid(String oid) {
        this.oid = oid;
    }
    @NonNull
    public String getCODENAME() {
        return CODENAME;
    }
    public void setCODENAME(@NonNull String CODENAME) {
        this.CODENAME = CODENAME;
    }
    @NonNull
    public String getCODE() {
        return CODE;
    }
    public void setCODE(@NonNull String CODE) {
        this.CODE = CODE;
    }
    public String getExt1() {
        return ext1;
    }
    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }
    public String getExt2() {
        return ext2;
    }
    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }
    public String getExt3() {
        return ext3;
    }
    public void setExt3(String ext3) {
        this.ext3 = ext3;
    }
    public String getExt4() {
        return ext4;
    }
    public void setExt4(String ext4) {
        this.ext4 = ext4;
    }
    public boolean getAutoLogin() {
        return autoLogin;
    }
    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
    }
    public boolean getScreenLock() {
        return screenLock;
    }
    public void setScreenLock(boolean screenLock) {
        this.screenLock = screenLock;
    }
    public boolean getAlarm() {
        return alarm;
    }
    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }

    public static void init(Context context, Realm realm){
        realm.where(Config.class).findAll().deleteAllFromRealm();
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(UtilityManager.loadJson(context, "config.json"));
            RealmList<Config> list = new RealmList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                Config config = new Config();
                config.setCODENAME(jsonArray.getJSONObject(i).getString("CODENAME"));
                config.setCODE(jsonArray.getJSONObject(i).getString("CODE"));
                config.setExt1(jsonArray.getJSONObject(i).getString("ext1"));
                config.setExt2(jsonArray.getJSONObject(i).getString("ext2"));
                config.setExt3(jsonArray.getJSONObject(i).getString("ext3"));
                config.setExt4(jsonArray.getJSONObject(i).getString("ext4"));
                config.setAutoLogin(jsonArray.getJSONObject(i).getBoolean("autoLogin"));
                list.add(config);
            }
            realm.copyToRealmOrUpdate(list);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static Config getMyAccount(Realm realm){
        return realm.where(Config.class).equalTo("CODENAME", "MyAccount").findFirst();
    }

    public static Config getServerInfo(Realm realm){
        return realm.where(Config.class).equalTo("CODENAME", "ServerInfo").findFirst();
    }
    public static String getMyUID(Realm realm){
        return getMyAccount(realm).getExt1();
    }
}

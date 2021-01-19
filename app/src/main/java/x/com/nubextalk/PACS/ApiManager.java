/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.PACS;

import android.content.Context;

import io.realm.Realm;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.Model.User;

public class ApiManager {

    private Context context;
    private Realm realm;
    private String CONTEXT_PATH;

    public interface onApiListener{
        void onSuccess(Response response, String body);
        //void onFail();
    }

    public ApiManager(Context context, Realm realm) {
        this.context = context;
        this.realm = realm;

        Config serverInfo = Config.getServerInfo(realm);
        String ssl = serverInfo == null ? "http://" : serverInfo.getExt1();
        String host = serverInfo == null ? "192.168.3.156" : serverInfo.getExt2();
        String port = serverInfo == null ? "" : serverInfo.getExt3();
        this.CONTEXT_PATH = ssl + host + port;
    }

    /**
     * Login
     * @param listener
     */
    public void login(onApiListener listener){
        Config myAccount = Config.getMyAccount(realm);
        if (myAccount != null) {
            login(myAccount.getExt1(), myAccount.getExt2(), listener);
        }
    }

    /**
     * Login
     * @param id
     * @param pwd
     * @param listener
     */
    public void login(String id, String pwd, onApiListener listener) {
        RequestBody formBody = new FormBody.Builder()
                .add("userid", id)
                .add("password", pwd)
                .build();

        new Protocol(context)
                .setFormData(formBody)
                .setCallback(new Protocol.onCallback() {
                    @Override
                    public void onCallback(Response response, String body) {
                        if (response != null) {
                            String cookie = response
                                    .request().url().toString().split(";")[1];
                            if (cookie != null) {
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        Config myAccount = Config.getMyAccount(realm);
                                        if(myAccount == null){
                                            myAccount = new Config();
                                            myAccount.setCODENAME("MyAccount");
                                            myAccount.setCODE("MyAccount");
                                        }
                                        myAccount.setExt1(id);
                                        myAccount.setExt2(pwd);
                                        myAccount.setExt3(cookie.toUpperCase());
                                        realm.copyToRealmOrUpdate(myAccount);

                                        if (listener != null) {
                                            listener.onSuccess(response, body);
                                        }
                                    }
                                });
                            }
                        }
                    }
                })
                .exec(CONTEXT_PATH + "/loginProcess");
    }

    /**
     * 서버와 연결하지 않고 사용하는 Login
     * @param id
     * @param pwd
     */
    public void login(String id, String pwd) {
        realm.executeTransaction(realm1 -> {
            Config myAccount = Config.getMyAccount(realm);
            if(myAccount == null) {
                myAccount = new Config();
                myAccount.setCODE("MyAccount");
                myAccount.setCODE("MyAccount");
            }
            myAccount.setExt1(id);
            myAccount.setExt2(pwd);
            realm.copyToRealmOrUpdate(myAccount);
        });
    }

    /**
     * User 목록 Get
     * @param listener
     */
    public void getEmployeeList(onApiListener listener){
        getEmployeeList(null, listener);
    }

    /**
     * 특정 User 목록 Get
     * @param user
     * @param listener
     */
    public void getEmployeeList(User user, onApiListener listener){
        Config myAccount = Config.getMyAccount(realm);
        if(myAccount == null){
            return;
        }
        if(user == null){
            user = new User();
        }

        RequestBody formBody  = new FormBody.Builder()
                .add("code", user.getCode() == null ? "" : user.getCode())
                .add("employtype", user.getTypeCode() == null ? "" : user.getTypeCode())
                .add("userid", user.getUserId() == null ? "" : user.getUserId())
                .add("removed", user.getRemoved() == null ? "0" : user.getRemoved())
                .build();

        new Protocol(context)
                .setFormData(formBody)
                .setSessionId(myAccount.getExt3())
                                         .setCallback(new Protocol.onCallback() {
                                     @Override
                                     public void onCallback(Response response, String body) {
                                         if (listener != null) {
                                             listener.onSuccess(response, body);
                                         }
                                     }
                })
                .exec(CONTEXT_PATH + "/app/getEmployeeList");
    }

    /**
     * USer App 정보 업데이트
     * @param user
     * @param listener
     */
    public void setEmployeeAppInfo(User user, onApiListener listener){
        Config myAccount = Config.getMyAccount(realm);
        if(myAccount == null){
            return;
        }
        if(user == null){
            return;
        }
        if(user.getCode() == null){
            return;
        }

        RequestBody formBody  = new FormBody.Builder()
                .add("code", user.getCode())
                .add("employtype", user.getTypeCode())
                .add("APP_IMG_PATH", user.getAppImagePath())
                .add("APP_NAME", user.getAppName())
                .add("APP_STATUS", user.getAppStatus())
                .build();

        new Protocol(context)
                .setFormData(formBody)
                .setSessionId(myAccount.getExt3())
                .setCallback(new Protocol.onCallback() {
                    @Override
                    public void onCallback(Response response, String body) {
                        if (listener != null) {
                            listener.onSuccess(response, body);
                        }
                    }
                })
                .exec(CONTEXT_PATH + "/app/saveEmployeeInfo");
    }

    public void getSeries(String studyId, onApiListener listener){
        Config myAccount = Config.getMyAccount(realm);
        if(myAccount == null){
            return;
        }
        if(!UtilityManager.checkString(studyId)){
            return;
        }

        new Protocol(context)
                .setSessionId(myAccount.getExt3())
                .setCallback(new Protocol.onCallback() {
                    @Override
                    public void onCallback(Response response, String body) {
                        if (listener != null) {
                            listener.onSuccess(response, body);
                        }
                    }
                })
                .exec(CONTEXT_PATH + "/app/getSeries?studyId="+studyId);
    }




}

    

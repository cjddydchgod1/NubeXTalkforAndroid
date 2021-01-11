/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.User2;
import x.com.nubextalk.PACS.ApiManager;
import x.com.nubextalk.PACS.Protocol;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private ApiManager apiManager;
    private Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        realm = Realm.getInstance(UtilityManager.getRealmConfig());

        apiManager = new ApiManager(this, realm);

        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                apiManager.login("lee777", "tech1!", new ApiManager.onApiListener() {
                    @Override
                    public void onSuccess(Response response, String body) {
                        Log.d("RESUlT", response.toString());
                    }
                });
            }
        });

        Button btn2 = findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiManager.getEmployeeList(new ApiManager.onApiListener() {
                    @Override
                    public void onSuccess(Response response, String body) {
                        Log.d("RESUlT", body.toString());
                        /**
                         * userlist -> realm 저장
                         */
                        try{
                            /**
                             * String(body) -> JSONArray
                             */
                            JSONArray jsonArray = new JSONArray(body);
                            /**
                             * JSONArray -> JSONObject
                             * field명 바꾸기
                             * JSONObject -> JSONArray
                             */
                            int len = jsonArray.length();
                            for(int i=0; i<len; i++) {
                                JSONObject jsonObject = (JSONObject) (jsonArray.get(i));
                                jsonObject.put("userId", jsonObject.get("userid"))
                                        .put("lastName", jsonObject.get("lastname"))
                                        .put("typeCode", jsonObject.get("typecode"))
                                        .put("typeCodeName", jsonObject.get("typecodename"))
                                        .put("appImagePath", jsonObject.get("app_IMG_PATH"))
                                        .put("appStatus", jsonObject.get("app_STATUS"))
                                        .put("appName", jsonObject.get("app_NAME"))
                                        .put("appFcmKey", jsonObject.get("app_FCM_KEY"));
                                jsonObject.remove("userid"); jsonObject.remove("lastname");
                                jsonObject.remove("typecode"); jsonObject.remove("typecodename");
                                jsonObject.remove("app_IMG_PATH"); jsonObject.remove("app_STATUS");
                                jsonObject.remove("app_NAME"); jsonObject.remove("app_FCM_KEY");
                                jsonArray.put(jsonObject);
                            }
                            /**
                             * JSONArray -> Realm
                             */
                            realm.executeTransaction(realm1 -> {
                                realm1.where(User2.class).findAll().deleteAllFromRealm();
                                realm1.createOrUpdateAllFromJson(User2.class, jsonArray);
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        Button btn3 = findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User2 user = new User2();
                user.setCode("1");
                user.setAppImagePath("TEST");
                user.setAppName("TEST");
                user.setAppStatus("0");

                apiManager.setEmployeeAppInfo(user, new ApiManager.onApiListener() {
                    @Override
                    public void onSuccess(Response response, String body) {
                        Log.d("RESUlT", body.toString());
                    }
                });
            }
        });
    }
}

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

import java.util.ArrayList;

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

                apiManager.login("han03", "tech1!", new ApiManager.onApiListener() {
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
                    }
                });
            }
        });

        Button btn3 = findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User2 user = new User2();
                user.setCode("220");
                user.setTypeCode("50");
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

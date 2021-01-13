/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;
import okhttp3.Response;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.PACS.ApiManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private ApiManager apiManager;
    private Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        realm = Realm.getInstance(UtilityManager.getRealmConfig());
        final LoginActivity activity = this;
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
                                        .put("appImagePath", jsonObject.get("app_IMG_PATH").equals(null) ? "https://storage.googleapis.com/nubextalkforandroid.appspot.com/profiles/thumb_6G67LygR16Xcp0vX65iS?GoogleAccessId=firebase-adminsdk-qxfsz%40nubextalkforandroid.iam.gserviceaccount.com&Expires=16730323200&Signature=Jp1ORSOTdYpCVBYTROs3ZPXUWUZpMJ7TEROGu9M882RbsnSy%2FIeyZiUc6ECC3GYaAXXzS4gQSBmAo3KVcewQAz%2Bmi9hUq7JHEvvJ5wLZduK1JUBnB2dWBe1Mi1jV%2FBMJxqoJSGlD%2FtawK6VTYmnTHwDivOX0ut7iEE6fGhQ5Z2OhVVotGPupX41esM4hidnldmLPsQouOYcHxRbKlB6FS57C7T%2FL5hOIcZOGqwPqr6jubvvuF7To95V2czS6DjUI3cGkh7zSyWdbpilCnK2PXvsp8JQVck1bJAUAw05WHu9rSud8aFJpyo0eZlxwnfXkbvoWJLruB%2FgFJWl4im5GDg%3D%3D": jsonObject.get("app_IMG_PATH"))
                                        .put("appStatus", jsonObject.get("app_STATUS").equals(null) ? "0" : jsonObject.get("app_STATUS"))
                                        .put("appName", jsonObject.get("app_NAME").equals(null) ? "no" : jsonObject.get("app_NAME"))
                                        .put("appFcmKey", jsonObject.get("app_FCM_KEY").equals(null) ? null : jsonObject.get("app_FCM_KEY"));
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
                                realm1.where(User.class).findAll().deleteAllFromRealm();
                                realm1.createOrUpdateAllFromJson(User.class, jsonArray);
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(activity, MainActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });

        Button btn3 = findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
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

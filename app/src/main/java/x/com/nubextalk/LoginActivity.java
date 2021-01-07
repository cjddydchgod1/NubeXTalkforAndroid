/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmResults;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.Model.User;

public class LoginActivity extends AppCompatActivity {

    private FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    private DocumentReference hospital = fireStore.collection("hospital").document("w34qjptO0cYSJdAwScFQ");
    private String token;
    private Realm realm;
    private Config config;
    private String fcm;
    private String id1;
    private String test = "6G67LygR16Xcp0vX65iS";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        realm           = Realm.getInstance(UtilityManager.getRealmConfig());
        final LoginActivity activity = this;

        EditText id = findViewById(R.id.editId);
        TextView loginButton = findViewById(R.id.login);

        id1 = id.getText().toString();
        /**
         * 현재 기기의 fcm값을 가지고 온다.
         */
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TOKEN", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        token = task.getResult().getToken();
                        Log.d("TOKEN", token);
                    }
                });

        /**
         * 입력한 id1값이 firestore에 저장된 uid값과 일치하는지를 확인한다.
         */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // id1 == uid 일치하는 fcm값 가져오기
                hospital.collection("users").whereEqualTo("uid",test).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        /**
                         * 같은 값이 있다면
                         */
                        if (task.isSuccessful()) {
                            updateFCM();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    /**
                                     * fcm값을 가지고 온다.
                                     */
                                    for (DocumentSnapshot document : task.getResult()) {
                                        fcm = document.get("fcm").toString();
                                    }
                                    config = realm.where(Config.class).equalTo("oid",test).findFirst();
                                    /**
                                      * config가 없다면 생성
                                      */
                                    if(config == null) {
                                        Config model = new Config();
                                        model.setOid(test);
                                        model.setCODENAME("CODENAME");
                                        model.setCODE("CODE");
                                        model.setExt1(fcm);
                                        realm.copyToRealmOrUpdate(model);
                                    } else {
                                        /**
                                        * Config가 있다면 update를 한다.
                                        */
                                        if(config.getExt1() == null || config.getExt1().equals(fcm)) config.setExt1(fcm);
                                        else if(config.getExt2() == null || config.getExt2().equals(fcm)) config.setExt2(fcm);
                                        else if(config.getExt3() == null || config.getExt3().equals(fcm)) config.setExt3(fcm);
                                        else if(config.getExt4() == null || config.getExt4().equals(fcm)) config.setExt4(fcm);
                                        else if(config.getExt5() == null || config.getExt5().equals(fcm)) config.setExt5(fcm);
                                        else config.setExt1(fcm);
                                        realm.copyToRealmOrUpdate(config);
                                    }
                                }
                            });
                            Intent intent = new Intent(activity, MainActivity.class);
                            intent.putExtra("token", token);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(activity, "id가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
    public void updateFCM() {
        hospital.collection("users").document(test)
                .update("fcm", token).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("FirebaseStoreManager", "UpdateSuccess");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("FirebaseStoreManager", "UpdateFail");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Example;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;
import io.realm.RealmResults;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.Example_FireStore_Model_Address;
import x.com.nubextalk.Model.Example_Model_Address;
import x.com.nubextalk.R;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * FireBase Database - Cloud Store
 * 참조 : https://firebase.google.com/docs/firestore?authuser=0
 */
public class Example_FireBaseCloudStore extends AppCompatActivity implements View.OnClickListener {

    private Realm realm;
    private Button btn01, btn02, btn03, btn04, btn05, btn06,btn07,btn08;
    private FirebaseFirestore fireStore;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example_fire_base_cloud_store);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        realm = Realm.getInstance(UtilityManager.getRealmConfig());
        fireStore = FirebaseFirestore.getInstance();

        btn01 = findViewById(R.id.btn01);
        btn02 = findViewById(R.id.btn02);
        btn03 = findViewById(R.id.btn03);
        btn04 = findViewById(R.id.btn04);
        btn05 = findViewById(R.id.btn05);
        btn06 = findViewById(R.id.btn06);
        btn07 = findViewById(R.id.btn07);
        btn08 = findViewById(R.id.btn08);

        btn01.setOnClickListener(this);
        btn02.setOnClickListener(this);
        btn03.setOnClickListener(this);
        btn04.setOnClickListener(this);
        btn05.setOnClickListener(this);
        btn06.setOnClickListener(this);
        btn07.setOnClickListener(this);
        btn08.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        WriteBatch batch = fireStore.batch();
        Gson gson = new Gson();

        switch (v.getId()){
            case R.id.btn01:
                /**
                 * 데이터 초기화
                 * 데이터 일괄 입력 (Batch)- JSON
                 * 참조 : https://firebase.google.com/docs/firestore/manage-data/transactions?authuser=0
                 */
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(UtilityManager.loadJson(this, "example_address.json"));
                    for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                        String oid = it.next();
                        Example_FireStore_Model_Address model = new Gson().fromJson(jsonObject.getJSONObject(oid).toString(), Example_FireStore_Model_Address.class);
                        model.setOid(oid);

                        DocumentReference ref = fireStore.collection("Example_Address").document(oid);
                        batch.set(ref, model);
                    }
                    batch.commit();

                    /**
                     * Advanced 방법
                     */
                    /*jsonObject = new JSONObject(UtilityManager.loadJson(this, "example_address.json"));
                    for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                        String oid = it.next();

                        JSONObject json = jsonObject.getJSONObject(oid);
                        Map<String, Object> map = gson.fromJson(json.toString(), new HashMap<>().getClass());

                        DocumentReference ref = fireStore.collection("Example_Address").document(oid);
                        batch.set(ref, map);
                    }
                    batch.commit();*/
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn02:
                /**
                 * 데이터 Write - Document
                 * set : document Id 지정
                 * add : document Id Auto
                 * 참조 : https://firebase.google.com/docs/firestore/manage-data/add-data?authuser=0
                 */
                Example_FireStore_Model_Address model = new Example_FireStore_Model_Address();
                model.setOid("Test_OID");
                model.setTid("TEST");
                model.setName("Test Place");
                model.setDesc("Test Place Desc");
                model.setAddress("Test Place Address");
                model.setPhone("000-000-0000");
                model.setEmail("test@test.com");
                model.setTypeCode("TEST");
                model.setTypeFont("{fas-user}");
                model.setTypeText("테스트");
                model.setRuntime("24/7 Run");
                model.setRating("5.0");

                fireStore.collection("Example_Address").document("Test_OID").set(model);
                fireStore.collection("Example_Address").add(model);
                break;
            case R.id.btn03:
                /**
                 * 데이터 Update
                 */
                fireStore.collection("Example_Address").document("Test_OID")
                        .update("name", "Updated Test Place", "desc", "Updated Desc");
                break;
            case R.id.btn04:
                /**
                 * Data Query All
                 */
                fireStore.collection("Example_Address")
                         .get()
                         .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d("FireStore", document.getId() + " => " + document.getData());
                                        }
                                    }
                                }
                         });
                break;
            case R.id.btn05:
                /**
                 * Data Query Filter
                 * 참조 : https://firebase.google.com/docs/firestore/query-data/queries?authuser=0
                 */
                fireStore.collection("Example_Address")
                         .whereEqualTo("oid", "Test_OID")
                         .get()
                         .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("FireStore", document.getId() + " => " + document.getData());
                                    }
                                    //Result to Class List
                                    List<Example_FireStore_Model_Address> model = task.getResult().toObjects(Example_FireStore_Model_Address.class);
                                }
                            }
                         });
                break;
            case R.id.btn06:
                /**
                 * Migration FireStore to Realm (Firestore > Realm)
                 */
                fireStore.collection("Example_Address")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            try {
                                                JSONArray jsonArray = new JSONArray();
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    JSONObject json = new JSONObject(gson.toJson(document.getData()));
                                                    jsonArray.put(json);
                                                }
                                                realm.createOrUpdateAllFromJson(Example_Model_Address.class, jsonArray);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                break;
            case R.id.btn07:
                /**
                 * Migration Realm to Firestore (Realm > Firestore)
                 */
                RealmResults<Example_Model_Address> result01 = realm.where(Example_Model_Address.class).findAll();
                realm.copyFromRealm(result01);
                try {
                    for(Example_Model_Address address : result01){
                        String oid = address.getOid();

                        JSONObject json = new JSONObject(gson.toJson(realm.copyFromRealm(address)));
                        Map<String, Object> map = gson.fromJson(json.toString(), new HashMap<>().getClass());

                        DocumentReference ref = fireStore.collection("Example_Address").document(oid);
                        batch.set(ref, map);
                    }
                    batch.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn08:
                /**
                 * Delete Realm
                 */
                RealmResults<Example_Model_Address> result10 = realm.where(Example_Model_Address.class).findAll();
                if(result10 != null){
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            result10.deleteAllFromRealm();
                        }
                    });
                }
                break;
            default:
                break;

        }
    }
}

/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Manager.FireBase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import x.com.nubextalk.Model.User;

public class FirebaseStoreManager {
    private static FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    private static final String hid = "w34qjptO0cYSJdAwScFQ";
    private static final String uid = "6G67LygR16Xcp0vX65iS";
    private static final DocumentReference hospital = fireStore.collection("hospital").document(hid);
    public static void updateProfileImg(String img){
        /**
         * Storage -> Firestore
         */
        hospital.collection("users").document(uid)
                .update("profileImg", img).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {

                    }
                });
    }
    public static void getUser(Realm realm) {
        /**
         * Firestore -> Realm
         */
        Gson gson = new Gson();
        hospital.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.e("여기오냐?", "aaa");
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
                                        realm.createOrUpdateAllFromJson(User.class, jsonArray);
                                        Log.e("Success", "aaa");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            Log.e("Fail", "aaa");
                        }
                    }
                });
        Log.e("End", "aaa");
    }
}

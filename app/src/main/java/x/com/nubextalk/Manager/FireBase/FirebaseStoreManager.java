/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Manager.FireBase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import x.com.nubextalk.Module.Fragment.FriendListFragment;

public class FirebaseStoreManager {
    private FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    private final String hid = "w34qjptO0cYSJdAwScFQ";
    private DocumentReference hospital = fireStore.collection("hospital").document(hid);
    private String TAG = "FirebaseStoreManager";
    public void updateProfileImg(String img, String uid){
        /**
         * Storage -> Firestore
         */
        hospital.collection("users").document(uid)
                .update("profileImg", img).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "ImgSuccess");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "ImgFail");
                    }
                });
    }
    public void updateProfileStatus(int status, String uid){
        hospital.collection("users").document(uid)
                .update("status", status).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Status Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Status Fail");
                    }
                });
    }
}

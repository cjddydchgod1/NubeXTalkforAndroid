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
    private static FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    private static final String hid = "w34qjptO0cYSJdAwScFQ";
    private static final String uid = "6G67LygR16Xcp0vX65iS";
    private static final DocumentReference hospital = fireStore.collection("hospital").document(hid);
    public void updateProfileImg(String img){
        /**
         * Storage -> Firestore
         */
        hospital.collection("users").document(uid)
                .update("profileImg", img).addOnSuccessListener(new OnSuccessListener<Void>() {
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
}

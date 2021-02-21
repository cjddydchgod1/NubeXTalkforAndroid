/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Manager.FireBase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


public class FirebaseStoreManager {
    private FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
    private final String hid = "w34qjptO0cYSJdAwScFQ";
    private DocumentReference hospital = fireStore.collection("hospital").document(hid);
    private String TAG = "FirebaseStoreManager";

    public Task<Void> updateUser(String userid, String token) {
        Map<String, Object> data = new HashMap<>();
        data.put("uid", userid);
        data.put("fcm", token);
        return hospital.collection("users").document(userid).set(data);
    }
    public void deleteToken(String userid) {
        hospital.collection("users").document(userid).delete();
    }
    public void addChatContent(){ /*send chat*/ };

    public Task getReference(String documentPath){
        return hospital.collection(documentPath).get();
    }
}

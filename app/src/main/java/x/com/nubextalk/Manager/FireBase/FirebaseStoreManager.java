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

    public void updateProfileImg(String imgUrl, String uid) {
        hospital.collection("users").document(uid).update("profileImg", imgUrl);
    }
    public void updateProfileStatus(int status, String uid) {
        hospital.collection("users").document(uid).update("status", status);
    }
    public void updateUser(String userid, String token) {
        Map<String, Object> data = new HashMap<>();
        data.put("userid", userid);
        data.put("fcm", token);
        hospital.collection("users").document(userid).set(data);
    }

    public void addChatContent(){ /*send chat*/ };

    public Task getReference(String documentPath){
        return hospital.collection(documentPath).get();
    }
}

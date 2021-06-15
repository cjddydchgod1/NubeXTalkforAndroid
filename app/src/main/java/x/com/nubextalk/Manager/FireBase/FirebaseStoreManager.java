/*
 * Created By JONGHO, LEE, Researcher of Institute of Biomedical Engineering, TechHeim Co.,Ltd.
 * on  2021.
 * This software is not stable health-care system and not certificated any health-care authority(like CE, FDA)
 * Prohibit redistribution, modification, commercial use.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Manager.FireBase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import static x.com.nubextalk.Module.CodeResources.HOSPITAL_ID;


public class FirebaseStoreManager {
    private final DocumentReference mHospital = FirebaseFirestore.getInstance()
            .collection("hospital").document(HOSPITAL_ID);

    public Task<Void> updateUser(String uid, String token) {
        Map<String, Object> userToken = new HashMap<>();
        userToken.put("uid", uid);
        userToken.put("fcm", token);
        return mHospital.collection("users").document(uid).set(userToken, SetOptions.merge());
    }

    public Task<Void> deleteToken(String uid) {
        Map<String, Object> delToken = new HashMap<>();
        delToken.put("fcm", FieldValue.delete());
        return mHospital.collection("users").document(uid).update(delToken);
    }
}

/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Manager.FireBase;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import androidx.annotation.NonNull;
import io.realm.Realm;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.Config;

/**
 * Firebase Message Service
 * - onNewToken : Token 이 갱신될때 호출
 * - onMessageReceived : FCM이 수시신될떄 호출
 * - 참고 : https://firebase.google.com/docs/cloud-messaging/android/client?authuser=0
 */
public class FirebaseMsgService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("FCM_TOKEN_OnNew : ", s);

        Realm realm = Realm.getInstance(UtilityManager.getRealmConfig());
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Config userMe = realm.where(Config.class).equalTo("CODE", "USER_ME").findFirst();
                if(userMe == null){
                    userMe = new Config();
                    userMe.setCODENAME("USER");
                    userMe.setCODE("USER_ME");
                }
                userMe.setExt1(s);

                realm.copyToRealmOrUpdate(userMe);
            }
        });


        /**
         * FCM Token 확인 함수
         */
        /*FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TOKEN", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.d("TOKEN", token);
                    }
                });*/
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        Log.d("TOKEN", "RECEIVE_TOKEN\nCODE : " + data.get("CODE") + "\nDATE : " + data.get("date"));
        // Chatting Message(Notification Message)
        // System Message
    }
}

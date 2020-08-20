package x.com.nubextalk.Manager.FireBase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import io.realm.Realm;

public class FirebaseMsgService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("FCM_TOKEN_OnNew : ", s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        initRealm();
        /** TODO */
    }

    private void initRealm(){
        try{ Realm.init(this); } catch (Exception e){ }
    }

}

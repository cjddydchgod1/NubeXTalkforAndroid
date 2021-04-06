/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Manager;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

public class FcmTokenRefreshService extends IntentService {

    public FcmTokenRefreshService() {
        super(FcmTokenRefreshService.class.getSimpleName());
        Log.i("FcmToeknRefreshService", FcmTokenRefreshService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            // 해당 메소드는 main thread에서 돌리면 안된다.
            FirebaseInstanceId.getInstance().deleteInstanceId();
            /**
             * @author SeungTaek.Lim (2017.05.11)
             * getToken을 한 시점에는 null 값을 리턴하지만,
             * 이를 호출해야만 FirebaseInstanceIdService으로 onTokenRefresh 이벤트가 발생된다.
             */
            FirebaseInstanceId.getInstance().getToken();
            Log.i("FcmTokenRefreshService", "try to reresh fcm token.");
        } catch (IOException e) {
            Log.e("FcmTokenRefreshService", "fcm token delete fail : " + e.toString());
        }
    }
}

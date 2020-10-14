/*
 * Created by Jong ho Lee on 19/8/2016.
 * Copyright © 2015 FocusNews. All rights reserved.
 */

package x.com.nubextalk.Manager.FireBase;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.Sort;
import x.com.nubextalk.Model.Config;

/**
 * FireBase Functions 함수 기능 부분
 * - 각 Functions Name이 Firebase Function에 등록된 함수와 매칭되어야함
 * - Functions의 Callback addCompleteListener 등으로 구현
 * - 참고 : https://firebase.google.com/docs/functions/
 */
public class FirebaseFunctionsManager {

    public static final String FUNTION_TEST = "executeTest";

    /** Interface **/
    public interface OnCompleteListsner{
        void onComplete();
    }

    /**
     * EXECUTE TEST
     * */
    public static Task<HttpsCallableResult> executeTest(String token){
        return executeTest(token, null);
    }
    public static Task<HttpsCallableResult> executeTest(String token, OnCompleteListsner onCompleteListsner) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();

        Map<String, Object> params = new HashMap<>();
        params.put("token",  token);

        return functions
                .getHttpsCallable(FUNTION_TEST)
                .call(params);
    }

}

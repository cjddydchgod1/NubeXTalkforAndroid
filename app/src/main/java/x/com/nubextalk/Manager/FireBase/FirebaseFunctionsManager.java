/*
 * Created by Jong ho Lee on 19/8/2016.
 * Copyright © 2015 FocusNews. All rights reserved.
 */

package x.com.nubextalk.Manager.FireBase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

/**
 * FireBase Functions 함수 기능 부분
 * - 각 Functions Name이 Firebase Function에 등록된 함수와 매칭되어야함
 * - Functions의 Callback addCompleteListener 등으로 구현
 * - 참고 : https://firebase.google.com/docs/functions/
 */
public class FirebaseFunctionsManager {

    public static final String FUNTION_TEST = "executeTest";
    public static final String FUNCTION_CREATE_CHAT_ROOM = "createChatRoom";

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

    public static Task<HttpsCallableResult> createChatRoom(String token, Map value){
        return createChatRoom(token, value, null);
    }


    public static Task<HttpsCallableResult> createChatRoom(String token, Map value, OnCompleteListsner onCompleteListsner) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();

        Map<String, Object> params = new HashMap<>();
        params.put("token", token);
        params.put("hospital", value.get("hospital"));
//        params.put("members", value.get("members"));
        params.put("members", new String[]{"vpUrLKpjM1mzXsQDT5CH", "zwnQyY3IlK6OXqkaq6Hv"});
        params.put("title", value.get("title"));
        params.put("roomImgUrl", value.get("roomImgUrl"));

        Log.d("functions", "params: "+ params.toString());

        return functions
                .getHttpsCallable(FUNCTION_CREATE_CHAT_ROOM)
                .call(params);
    }


}

/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.PACS;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;

import io.realm.Realm;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.Config;

public class Protocol extends AsyncTask<String, Integer, ArrayList<Object>> {

    private Context context;
    private Handler handler;
    private RequestBody formData;
    private String sessionId;
    private onCallback onCallback;

    public interface onCallback{
        void onCallback(Response response, String body);
    }

    public Protocol (Context context){
        this.context = context;
    }

    //Callback 등록
    public Protocol setCallback(onCallback onCallback) {
        this.onCallback = onCallback;
        return this;
    }

    //Post Parameter 설정
    public Protocol setFormData(RequestBody formData) {
        this.formData = formData;
        return this;
    }

    //로그인 Session Id 설정
    public Protocol setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public Protocol exec(String url){
        if(UtilityManager.checkNetwork(context) == 0){
            //No Network
            if(onCallback != null){
                onCallback.onCallback(null, null);
            }
            return null;
        }

        if(!url.contains("login") && !UtilityManager.checkString(sessionId)){
            //No Login
            return null;
        }

        execute(url);
        return this;
    }

    @Override
    protected ArrayList<Object> doInBackground(String... arg0) {
        try{
            Log.d("PACS Protocol : ", arg0[0]);
            return getData(arg0[0]);
        }catch (Exception e ){
            Log.d(null, "FAIL");
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Object> response) {
        super.onPostExecute(response);
        try{
            if(onCallback != null){
                onCallback.onCallback(
                        (Response) response.get(0),
                        (String) response.get(1)
                );
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private ArrayList<Object> getData(String url){
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        Request.Builder builder = new Request.Builder()
                .url(url)
                .addHeader("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");

        if(UtilityManager.checkString(sessionId)){
            builder.addHeader("Cookie",sessionId);
        }
        if(formData != null){
            builder.post(formData);
        }

        try {
            Response response =client.newCall(builder.build()).execute();

            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(0, response);
            arrayList.add(1, response.body().string());

            return arrayList;
        } catch (IOException e) {
            return null;
        }
    }

}

    

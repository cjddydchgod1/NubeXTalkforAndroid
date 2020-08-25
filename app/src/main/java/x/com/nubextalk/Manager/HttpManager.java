/*
 * Created by Jong ho Lee on 12/8/2016
 * Copyright © 2015 FocusNews. All rights reserved.
 */

package x.com.nubextalk.Manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * HTTP 통신 모듈(Async)
 * 사용법 :
 *  1. new HttpManager( <url>, <Context>, <Handler> )
 *  2. new HttpManager( <url>, <Context>, <PassData>, <Handler> )
 *
 *  PC 크롬을 이용하여 Debug
 *      - 크롬 브라우저 주소창 chrome://inspect
 *      - Debug할 앱 선택, Debug 팝업
 *      - Network 탭에서 확인
 */
public class HttpManager extends AsyncTask<String, Integer, String> {

    String strUrl;
    Handler handler;
    Exception exception;
    Object passData;
    Context ctx;

    /**
     * @param url : 요청할 URL
     * @param ctx : Conext
     * @param handler : Callback Handler
     */
    public HttpManager(String url, Context ctx, Handler handler) {
        this.ctx = ctx;
        this.handler = handler;
        this.passData = "";
        execute(url);
    }

    /**
     *
     * @param url : 요청할 URL
     * @param ctx : Conext
     * @param passData : Callback Handler 까지 넘길 Data
     * @param handler : Callback Handler
     */
    public HttpManager(String url, Context ctx, Object passData, Handler handler) {
        this.ctx = ctx;
        this.handler = handler;
        this.passData = passData;
        execute(url);
    }


    @Override
    protected String doInBackground(String... arg0) {
        try{
            Log.d("HTTP MANAGER : ", arg0[0]);
            strUrl = getData(arg0[0]);
            return strUrl;
        }catch (Exception e ){
            Log.d(null, "JSON FAIL");
            return "JSON FAIL";
        }
    }

    String rst = "";
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try{
            rst = result;
            if(exception != null||isEmptyString(rst)){
                Message msg = handler.obtainMessage();
                msg.what = -1;
                ArrayList<Object> arrayList = new ArrayList<>();
                arrayList.add(0,ctx);
                arrayList.add(1,passData);
                arrayList.add(2,exception);
                msg.obj = arrayList;
                handler.sendMessage(msg);
                return;
            }else{
                Message msg = handler.obtainMessage();
                msg.what=0;
                ArrayList<Object> arrayList = new ArrayList<>();
                arrayList.add(0,ctx);
                arrayList.add(1,passData);
                arrayList.add(2,rst);
                msg.obj = arrayList;
                handler.sendMessage(msg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private String getData(String strUrl){
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        Request request = new Request.Builder()
                .url(strUrl)
                .addHeader("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36")
                .build();
        try {
            Response response = client.newCall(request).execute();

            return response.body().string();
        } catch (IOException e) {
            return (e.getMessage());
        }
    }

    private String getData2(String strUrl){
        StringBuilder sb = new StringBuilder();

        try{
            BufferedInputStream bis = null;
            URL url = new URL(strUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            int responseCode;

            con.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            responseCode = con.getResponseCode();

            if(responseCode == 200){
                bis = new BufferedInputStream(con.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(bis,"UTF-8"));
                String line = null;

                while((line = reader.readLine())!=null){
                    sb.append(line);
                }
                bis.close();
            }
        }catch (Exception e){
            e.printStackTrace();
            //local db file I/O
        }
        return sb.toString();
    }

    private boolean isEmptyString(String text) {
        return (text == null || text.trim().equals("null") || text.trim()
                .length() <= 0);
    }

    public static int checkNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return 1;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return 2;
        }
        return 0;
    }


}

    

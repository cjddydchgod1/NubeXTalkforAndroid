/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.PACS;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import io.realm.Realm;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import x.com.nubextalk.ChatRoomActivity;
import x.com.nubextalk.ImageViewActivity;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.SharePACSActivity;

public class PacsWebView extends WebView {

    private onJavaScriptListener listener;
    public interface onJavaScriptListener{
        void onCall(String func, String... arg);
    }
    public void setJavaScriptListener(onJavaScriptListener listener) {
        this.listener = listener;
    }

    public PacsWebView(Context context) {
        super(context);
        setWebViewSettings(context);
    }

    public PacsWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWebViewSettings(context);
    }

    public PacsWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWebViewSettings(context);
    }

    public PacsWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setWebViewSettings(context);
    }

    @Override
    public void loadUrl(String url) {
        if(getTag() == null){
            return;
        }
        url = getTag().toString() + url;
        super.loadUrl(url);
    }

    private void setWebViewSettings(Context context) {
        setWebViewClient(new WebViewClient());
        addJavascriptInterface(new JavaScriptBridge(), "NubeXApp");

        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        settings.setBuiltInZoomControls(false);
        settings.setSupportZoom(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    public boolean init(Realm realm){
        String CONTEXT_PATH = new ApiManager(getContext(), realm).getServerPath(realm);
        setTag(CONTEXT_PATH);

        Config myAccount = Config.getMyAccount(realm);
        if(myAccount == null){
            return false;
        }

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(CONTEXT_PATH, myAccount.getExt3());
        return true;
    }

    private class JavaScriptBridge{

        @JavascriptInterface
        public void shareApp(final String studyId, final String desc, final String thumbSrc){
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    if(UtilityManager.checkString(studyId)){
                        if(listener != null){
                            listener.onCall("shareApp", studyId, desc);
                        }
                    }
                }
            });
        }
    }
}

    

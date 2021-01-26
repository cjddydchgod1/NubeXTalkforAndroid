/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.PACS.ApiManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ImageViewActivity extends AppCompatActivity {

    private Realm realm;
    private ApiManager mApiManager;

    private WebView mWebView;
    private WebSettings mWebSettings;
    private String CONTEXT_PATH;

    private String rid;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        realm = Realm.getInstance(UtilityManager.getRealmConfig());
        mApiManager = new ApiManager(this, realm);
        CONTEXT_PATH = mApiManager.getServerPath(realm);

        mWebView = findViewById(R.id.webView);

        setWebViewSettings();

        Config myAccount = Config.getMyAccount(realm);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(CONTEXT_PATH, myAccount.getExt3());

        String studyId = getIntent().getStringExtra("studyId");
        rid = getIntent().getStringExtra("rid");
        if(UtilityManager.checkString(studyId)){
            mWebView.loadUrl(CONTEXT_PATH + "/mobile/app/?studyId="+studyId);
        }
        else{
            mWebView.loadUrl(CONTEXT_PATH + "/mobile/app/");
        }
    }

    private void setWebViewSettings() {
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.addJavascriptInterface(new JavaScriptBridge(), "NubeXApp");
        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setDomStorageEnabled(true);

        mWebSettings.setBuiltInZoomControls(false);
        mWebSettings.setSupportZoom(false);

        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    private class JavaScriptBridge{

        @JavascriptInterface
        public void shareApp(final String studyId){
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    if(UtilityManager.checkString(studyId)){
                        //Go Share Intent
                        /**
                         * rid값이 존재한다면 ChatRoom에서 바로 PACS를 접근한 것.
                         * rid값이 존재하지 않는다면 PACSReference에서 접근한 것.
                         */
                        if(UtilityManager.checkString(rid)) {
                            Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                            intent.putExtra("studyId",studyId);
                            intent.putExtra("description","EMPTY_PACS_DESCRIPTION");
                            intent.putExtra("rid",rid);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(ImageViewActivity.this, SharePACSActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("studyId", studyId);
                            bundle.putString("description", "EMPTY PACS DESCRIPTION");
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        finish();
                    }
                }
            });
        }
    }
}

/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import io.realm.Realm;
import x.com.nubextalk.ImageViewActivity;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.PACS.ApiManager;
import x.com.nubextalk.PACS.PacsWebView;
import x.com.nubextalk.R;
import x.com.nubextalk.SharePACSActivity;

public class PACSReferenceFragment extends Fragment implements PacsWebView.onJavaScriptListener {

    private Realm realm;
    private ApiManager mApiManager;

    private ViewGroup rootview;
    private PacsWebView mPacsWebView;

    private Context mContext;
    private Activity mActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        if(context instanceof Activity)
            mActivity = (Activity) context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        realm = Realm.getInstance(UtilityManager.getRealmConfig());
        mApiManager = new ApiManager(mContext, realm);

        rootview    = (ViewGroup) inflater.inflate(R.layout.fragment_pacs_reference, container, false);
        mPacsWebView    = rootview.findViewById(R.id.webView);
        /* 쿠키 생성 후 넣기 */
        try {
            mPacsWebView.init(realm);
        } catch (Exception e) {
            Log.e("e", e.toString());
        } finally {
            if(realm != null)
                realm.close();
        }

        mPacsWebView.setJavaScriptListener(this);
        mPacsWebView.loadUrl("/mobile/app/");
        return rootview;
    }

    @Override
    public void onDetach() {
        mContext = null;
        mActivity = null;
        super.onDetach();
    }

    @Override
    public void onCall(String func, String... arg) {
        switch (func){
            case "shareApp":
                Intent intent = new Intent(mActivity, SharePACSActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("studyId", arg[0]);
                bundle.putString("description", arg[1]);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }

    }
}
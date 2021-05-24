/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.realm.Realm;
import x.com.nubextalk.ChatRoomActivity;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.PACS.ApiManager;
import x.com.nubextalk.PACS.PacsWebView;
import x.com.nubextalk.R;
import x.com.nubextalk.SharePACSActivity;

import static x.com.nubextalk.Module.CodeResources.PATH_PACS_HOME;
import static x.com.nubextalk.Module.CodeResources.PATH_PACS_VIEWER;

public class PACSReferenceFragment extends Fragment implements PacsWebView.onJavaScriptListener {

    private Realm mRealm;
    private ApiManager mApiManager;

    private ViewGroup mRootview;
    private PacsWebView mPacsWebView;

    private Context mContext;
    private Activity mActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        if (context instanceof Activity)
            mActivity = (Activity) context;
        super.onAttach(context);
    }

    /** Modified By Jongho Lee*/
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mPacsWebView.saveState(outState);
    }

    /** Modified By Jongho Lee*/
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mPacsWebView.restoreState(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRealm = Realm.getInstance(UtilityManager.getRealmConfig());
        mApiManager = new ApiManager(mContext, mRealm);

        if (!UtilityManager.isTablet(mActivity)) {
//            mActivity.setTitle(TITLE_PACS);
        }

        mRootview = (ViewGroup) inflater.inflate(R.layout.fragment_pacs_reference, container, false);
        mPacsWebView = mRootview.findViewById(R.id.webView);
        /* 쿠키 생성 후 넣기 */
        try {
            mPacsWebView.init(mRealm);
        } catch (Exception e) {
            Log.e("e", e.toString());
        } finally {
            if (mRealm != null)
                mRealm.close();
        }
        mPacsWebView.setJavaScriptListener(this);

        String studyId = null;
        if (UtilityManager.isTablet(mContext)) {
            Bundle bundle = getArguments();
            studyId = bundle != null ? bundle.getString("studyId") : null;
        }

        if(savedInstanceState == null){
            if (UtilityManager.checkString(studyId)) {
                mPacsWebView.loadUrl(PATH_PACS_VIEWER + studyId);
            } else {
                mPacsWebView.loadUrl(PATH_PACS_HOME);
            }
        }
        return mRootview;
    }

    @Override
    public void onDetach() {
        mContext = null;
        mActivity = null;
        super.onDetach();
    }

    @Override
    public void onCall(String func, String... arg) {
        switch (func) {
            case "shareApp":
                if (mActivity instanceof ChatRoomActivity) {
                    ((ChatRoomActivity) mActivity).sendPacs(arg[0], arg[1], Realm.getInstance(UtilityManager.getRealmConfig()));
                } else {
                    Intent intent = new Intent(mActivity, SharePACSActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("studyId", arg[0]);
                    bundle.putString("description", arg[1]);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
        }
    }
}
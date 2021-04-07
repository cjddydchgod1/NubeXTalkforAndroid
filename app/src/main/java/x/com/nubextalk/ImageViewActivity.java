/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import io.realm.Realm;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.PACS.ApiManager;
import x.com.nubextalk.PACS.PacsWebView;

import static x.com.nubextalk.Module.CodeResources.PATH_PACS_HOME;
import static x.com.nubextalk.Module.CodeResources.PATH_PACS_VIEWER;

public class ImageViewActivity extends AppCompatActivity implements PacsWebView.onJavaScriptListener {

    private Realm mRealm;
    private ApiManager mApiManager;

    private PacsWebView mPacsWebView;
    private String CONTEXT_PATH;

    private String mRid;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        mRealm = Realm.getInstance(UtilityManager.getRealmConfig());
        mApiManager = new ApiManager(this, mRealm);
        CONTEXT_PATH = mApiManager.getServerPath(mRealm);

        mPacsWebView = findViewById(R.id.webView);
        mPacsWebView.init(mRealm);
        mPacsWebView.setJavaScriptListener(this);

        String studyId = getIntent().getStringExtra("studyId");
        mRid = getIntent().getStringExtra("rid");
        if (UtilityManager.checkString(studyId)) {
            mPacsWebView.loadUrl(PATH_PACS_VIEWER + studyId);
        } else {
            mPacsWebView.loadUrl(PATH_PACS_HOME);
        }
    }

    @Override
    public void onCall(String func, String... arg) {
        switch (func) {
            case "shareApp":
                if (UtilityManager.checkString(mRid)) {
                    Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
                    intent.putExtra("studyId", arg[0]);
                    intent.putExtra("description", arg[1]);
                    intent.putExtra("rid", mRid);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ImageViewActivity.this, SharePACSActivity.class);
                    Bundle bundle = new Bundle();
                    intent.putExtra("studyId", arg[0]);
                    intent.putExtra("description", arg[1]);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                finish();
                break;
        }
    }
}

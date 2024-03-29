/*
 * Created By JONGHO, LEE, Researcher of Institute of Biomedical Engineering, TechHeim Co.,Ltd.
 * on  2021.
 * This software is not stable health-care system and not certificated any health-care authority(like CE, FDA)
 * Prohibit redistribution, modification, commercial use.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.core.app.ActivityCompat;

import com.aquery.AQuery;
import com.joanzapata.iconify.widget.IconButton;
import com.ortiz.touchview.TouchImageView;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import io.realm.Realm;
import x.com.nubextalk.Manager.ImageManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatContent;

import static x.com.nubextalk.Module.CodeResources.ICON_DOWNLOAD;
import static x.com.nubextalk.Module.CodeResources.MSG_COMPLETE_DOWNLOAD;

public class ChatImageViewActivity extends Activity implements View.OnClickListener {
    private Realm mRealm;
    private AQuery mAquery;
    private Context mContext;
    private TouchImageView mTouchImageView;
    private IconButton mDownloadButton;
    private String mCid;
    private String mImagePath;
    private String mImageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_image_view);
        mContext = this;
        mRealm = Realm.getInstance(UtilityManager.getRealmConfig());
        mAquery = new AQuery(this);

        mTouchImageView = (TouchImageView) findViewById(R.id.touch_image_view);
        mDownloadButton = (IconButton) findViewById(R.id.download_button);

        mDownloadButton.setText(ICON_DOWNLOAD);

        Intent intent = getIntent();
        mCid = intent.getStringExtra("cid");

        ChatContent chat = mRealm.where(ChatContent.class).equalTo("cid", mCid).findFirst();

        mImagePath = chat.getContent();
        mImageUrl = chat.getExt1();

        mDownloadButton.setOnClickListener(this);

        mAquery.id(R.id.touch_image_view).image(mImagePath);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mRealm.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.download_button:
                download();
                break;
        }
    }

    private void download() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        } else {
            FutureTask futureTask = new FutureTask(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    Date date = new Date();
                    String name = mCid + "(" + date.toString() + ").jpg";
                    boolean result = new ImageManager(mContext).saveUrlToExternalStorage(mImageUrl, name) != null;
                    return result;
                }
            });
            new Thread(futureTask).start();
            try {
                if ((Boolean) futureTask.get())
                    mAquery.toast(MSG_COMPLETE_DOWNLOAD);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
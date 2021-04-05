/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.aquery.AQuery;
import com.ortiz.touchview.TouchImageView;

import io.realm.Realm;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.User;

public class ProfileImageViewActivity extends AppCompatActivity {
    private Realm mRealm;
    private AQuery mAquery;
    private TouchImageView mTouchImageView;
    private String mUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image_view);

        mRealm = Realm.getInstance(UtilityManager.getRealmConfig());
        mAquery = new AQuery(this);

        mTouchImageView = (TouchImageView) findViewById(R.id.touch_image_view);

        Intent intent = getIntent();
        mUid = intent.getStringExtra("uid");

        User user = mRealm.where(User.class).equalTo("uid", mUid).findFirst();

        mAquery.view(mTouchImageView).image(user.getAppImagePath());

    }
}
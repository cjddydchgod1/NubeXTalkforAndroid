/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.LinkedList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import x.com.nubextalk.item.Profile;

/**
 * Github Commint Message는 다음을 따라주시길 바랍니다.
 *
 * ex:)
 * [이종호] 2020.08.26
 * 1. 메세지1
 * 2. 메세지2
 * ....
 */
public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private final LinkedList<Profile> mFriendList = new LinkedList<>();
    private RecyclerView mRecycleView;
    private FriendListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFriendList.addLast(new Profile(0, 0, "친구 프로필"));
        mFriendList.addLast(new Profile(R.drawable.cat1, R.drawable.baseline_fiber_manual_record_black_24dp, "친구1"));
        mFriendList.addLast(new Profile(R.drawable.cat2, R.drawable.baseline_fiber_manual_record_black_24dp, "친구2"));
        mFriendList.addLast(new Profile(R.drawable.cat3, R.drawable.baseline_fiber_manual_record_black_24dp, "친구3"));
        mFriendList.addLast(new Profile(R.drawable.cat4, R.drawable.baseline_fiber_manual_record_black_24dp, "친구4"));
        mFriendList.addLast(new Profile(R.drawable.cat5, R.drawable.baseline_fiber_manual_record_black_24dp, "친구5"));
        mRecycleView = findViewById(R.id.friendRecycleview);
        mAdapter = new FriendListAdapter(this, mFriendList);
        mRecycleView.setAdapter(mAdapter);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initPermission() {
        PermissionListener pm = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                onBackPressed();
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            new TedPermission().with(this)
                    .setPermissionListener(pm)
                    .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ).check();
        }
        else{

        }
    }
}

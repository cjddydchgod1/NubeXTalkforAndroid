/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Module.Fragment.ChatListFragment;
import x.com.nubextalk.Module.Fragment.FriendListFragment;
import x.com.nubextalk.Module.Fragment.PACSReferenceFragment;
import x.com.nubextalk.Module.Fragment.SettingFragment;

import static x.com.nubextalk.Module.CodeResources.CHAT_ADD;
import static x.com.nubextalk.Module.CodeResources.MOVE_TO_CHAT_ROOM;

/**
 * Github Commint Message는 다음을 따라주시길 바랍니다.
 * ex:)
 * [이종호] 2020.08.26
 * 1. 메세지1
 * 2. 메세지2
 * ....
 */
public class MainActivity extends AppCompatActivity {

    private FriendListFragment mFriendListFrag = new FriendListFragment();
    private ChatListFragment mChatListFrag = new ChatListFragment();
    private PACSReferenceFragment mPacsReferenceFrag = new PACSReferenceFragment();
    private SettingFragment mSettingFrag = new SettingFragment();
    private FragmentManager mFragManager = getSupportFragmentManager();
    private FragmentTransaction mFragTransaction;
    private FragmentTransaction mFragTransaction2;
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SetTheme(Dark, Light)

        setContentView(R.layout.activity_main);

        //Setting toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting bottom navigation
        mBottomNavigationView = findViewById(R.id.bottom_nav);
        initBottomNavigation();

        // Begin fragment transaction
        int requestChatList = getIntent().getIntExtra("requestChatList", RESULT_CANCELED);
        mFragTransaction = mFragManager.beginTransaction();
        mFragTransaction2 = mFragManager.beginTransaction();

        if (requestChatList == RESULT_OK) {
            mFragTransaction.replace(R.id.main_frame_layout, mChatListFrag).commitAllowingStateLoss();
            mBottomNavigationView.setSelectedItemId(R.id.nav_chat_list);
        } else {
            mFragTransaction.replace(R.id.main_frame_layout, mFriendListFrag).commitAllowingStateLoss();
            mBottomNavigationView.setSelectedItemId(R.id.nav_friend_list);
        }
        if (UtilityManager.isTablet(this)) {
//            Toolbar pacsToolbar = findViewById(R.id.pacs_toolbar);
//            setSupportActionBar(pacsToolbar);
            mFragTransaction2.replace(R.id.main_pacs_layout, mPacsReferenceFrag).commitAllowingStateLoss();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        int requestChatList = intent.getIntExtra("requestChatList", RESULT_CANCELED);
        mFragTransaction = mFragManager.beginTransaction();
        if (requestChatList == RESULT_OK) {
            mFragTransaction.replace(R.id.main_frame_layout, mChatListFrag).commitAllowingStateLoss();
            mBottomNavigationView.setSelectedItemId(R.id.nav_chat_list);
        } else {
            mFragTransaction.replace(R.id.main_frame_layout, mFriendListFrag).commitAllowingStateLoss();
            mBottomNavigationView.setSelectedItemId(R.id.nav_friend_list);
        }
    }

    /**
     * 초기 하단 네비게이션 설정 및 프래그먼트 전환 리스너 설정
     **/
    private void initBottomNavigation() {
        //네비게이션 버튼 해당 프래그먼트로 전환
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mFragTransaction = mFragManager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.nav_friend_list:
                        mFragTransaction.replace(R.id.main_frame_layout, mFriendListFrag).commitAllowingStateLoss();
                        return true;
                    case R.id.nav_chat_list:
                        mFragTransaction.replace(R.id.main_frame_layout, mChatListFrag).commitAllowingStateLoss();
                        return true;
                    case R.id.nav_calendar:
                        mFragTransaction.replace(R.id.main_frame_layout, mPacsReferenceFrag).commitAllowingStateLoss();
                        return true;
                    case R.id.nav_setting:
                        mFragTransaction.replace(R.id.main_frame_layout, mSettingFrag).commitAllowingStateLoss();
                        return true;
                }
                return false;
            }
        });
    }

    public void startChatRoomActivity(Intent intent) {
        startActivityForResult(intent, MOVE_TO_CHAT_ROOM);
    }

    /**
     * startActivityForResult 함수를 통해 다른 Activity 에서 받아온 결과를 받아왔을때 수행하는 함수
     **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CHAT_ADD) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_frame_layout);
                if (fragment instanceof ChatListFragment) {
                    startChatRoomActivity(data);
                }
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == MOVE_TO_CHAT_ROOM) {
                mFragTransaction = mFragManager.beginTransaction();
                mFragTransaction.replace(R.id.main_frame_layout, mChatListFrag).commitAllowingStateLoss();
                mBottomNavigationView.setSelectedItemId(R.id.nav_chat_list);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void initPermission() {
        PermissionListener pm = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                //Toast.makeText(MainActivity.class, "", Toast.LENGTH_SHORT).show();
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
        } else {

        }
    }


}

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

import io.realm.Realm;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.Config;
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
    private Config mTutorialStatus;
    private Realm mRealm;

    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SetTheme(Dark, Light)

        //앱 초기 설치 후 로그인 했을 때 메인 화면 넘어가기 전에 튜토리얼 보여주기, 로그아웃 하고 다른 아이디로 로그인했을 떄도 보여줌
        mRealm = Realm.getInstance(UtilityManager.getRealmConfig());
        mTutorialStatus = Config.getTutorialStatus(mRealm);

        if (mTutorialStatus.getExt1().equals("first")) {
            Config.setTutorialStatus(mRealm, "not first");
            Intent tutorialIntent = new Intent(MainActivity.this, TutorialActivity.class);
            tutorialIntent.putExtra("fromSetting", "false");
            startActivity(tutorialIntent);
        }

        setContentView(R.layout.activity_main);

        //Setting toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting bottom navigation
        mBottomNavigationView = findViewById(R.id.bottom_nav);
        initBottomNavigation();

        /** Modified By Jongho Lee */
        //Init Fragment
        FragmentTransaction transaction = mFragManager.beginTransaction();
        transaction.add(R.id.main_frame_layout, mFriendListFrag).hide(mFriendListFrag);
        transaction.add(R.id.main_frame_layout, mChatListFrag).hide(mChatListFrag);
        transaction.add(R.id.main_frame_layout, mSettingFrag).hide(mSettingFrag);

        //Tablet PACS reference fragment control
        if (UtilityManager.isTablet(this)) {
            FragmentTransaction transactionTablet = mFragManager.beginTransaction();
            transactionTablet.add(R.id.main_pacs_layout, mPacsReferenceFrag).commitAllowingStateLoss();
        } else {
            transaction.add(R.id.main_frame_layout, mPacsReferenceFrag).hide(mPacsReferenceFrag);
        }

        int requestChatList = getIntent().getIntExtra("requestChatList", RESULT_CANCELED);
        if (requestChatList == RESULT_OK) {
            transaction.show(mChatListFrag);
            mBottomNavigationView.setSelectedItemId(R.id.nav_chat_list);
        } else {
            transaction.show(mFriendListFrag);
            mBottomNavigationView.setSelectedItemId(R.id.nav_friend_list);
        }

        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        /** Modified By Jongho Lee
         * TODO 위 oncreate getIntent()랑 중복 가능성 있음 체크해서 필요없는 로직은 삭제 -> oncreate 부분 로직 정리함
         * */
        FragmentTransaction transaction = mFragManager.beginTransaction();
        int requestChatList = intent.getIntExtra("requestChatList", RESULT_CANCELED);
        if (requestChatList == RESULT_OK) {
            transaction.show(mChatListFrag).commitAllowingStateLoss();
            mBottomNavigationView.setSelectedItemId(R.id.nav_chat_list);
        } else if (requestChatList == RESULT_FIRST_USER) {
            transaction.show(mSettingFrag).commitAllowingStateLoss();
            mBottomNavigationView.setSelectedItemId(R.id.nav_setting);
        }
        else {
            transaction.show(mFriendListFrag).commitAllowingStateLoss();
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
                /** Modified By Jongho Lee*/
                FragmentTransaction fragTransaction = mFragManager.beginTransaction();
                for (Fragment fragment : mFragManager.getFragments()) {
                    fragTransaction.hide(fragment);
                }
                switch (item.getItemId()) {
                    case R.id.nav_friend_list:
                        fragTransaction.show(mFriendListFrag);
                        break;
                    case R.id.nav_chat_list:
                        fragTransaction.show(mChatListFrag);
                        break;
                    case R.id.nav_calendar:
                        fragTransaction.show(mPacsReferenceFrag);
                        break;
                    case R.id.nav_setting:
                        fragTransaction.show(mSettingFrag);
                        break;
                }
                if (UtilityManager.isTablet(getApplicationContext())) {
                    fragTransaction.show(mPacsReferenceFrag);
                }
                fragTransaction.commitAllowingStateLoss();
                return true;
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

            if (requestCode == MOVE_TO_CHAT_ROOM) {
                /** Modified By Jongho Lee*/
                FragmentTransaction fragTransaction = mFragManager.beginTransaction();
                for (Fragment fragment : mFragManager.getFragments()) {
                    fragTransaction.hide(fragment);
                }
                fragTransaction.show(mChatListFrag);

                if (UtilityManager.isTablet(getApplicationContext())) {
                    fragTransaction.show(mPacsReferenceFrag);
                }
                fragTransaction.commitAllowingStateLoss();
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

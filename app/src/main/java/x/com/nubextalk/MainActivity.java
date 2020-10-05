/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Github Commint Message는 다음을 따라주시길 바랍니다.
 * ex:)
 * [이종호] 2020.08.26
 * 1. 메세지1
 * 2. 메세지2
 * ....
 */
public class MainActivity extends AppCompatActivity {
    private FriendListFragment friendListFrag = new FriendListFragment();
    private ChatListFragment chatListFrag = new ChatListFragment();
    private CalendarFragment calendarFrag = new CalendarFragment();
    private SettingFragment settingFrag = new SettingFragment();

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction fragmentTransaction;

    private Toolbar toolbar;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //툴바 설정
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //네비게이션 설정
        bottomNavigationView = findViewById(R.id.bottom_nav);

        //프래그먼트 전환 관리 설정 - 처음 실행시에는 친구목록 프래그먼트
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame_layout, friendListFrag).commitAllowingStateLoss();

        //네비게이션 버튼 해당 프래그먼트로 전환
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                fragmentTransaction = fragmentManager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.nav_friend_list:
                        fragmentTransaction.replace(R.id.main_frame_layout, friendListFrag).commitAllowingStateLoss();
                        return true;
                    case R.id.nav_chat_list:
                        fragmentTransaction.replace(R.id.main_frame_layout, chatListFrag).commitAllowingStateLoss();
                        return true;
                    case R.id.nav_calendar:
                        fragmentTransaction.replace(R.id.main_frame_layout, calendarFrag).commitAllowingStateLoss();
                        return true;
                    case R.id.nav_setting:
                        fragmentTransaction.replace(R.id.main_frame_layout, settingFrag).commitAllowingStateLoss();
                        return true;
                }
                return false;
            }
        });
    }

    //툴바 메뉴 설정
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);

        //검색창 입력 리스
        MenuItem searchItem = menu.findItem(R.id.toolbar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override //검색어 완료시
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override //검색어 입력시
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
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
        } else {

        }
    }
}

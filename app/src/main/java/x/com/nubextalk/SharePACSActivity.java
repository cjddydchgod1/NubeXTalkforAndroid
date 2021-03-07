/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import x.com.nubextalk.Module.Adapter.PACSPagerAdapter;
import x.com.nubextalk.Module.Fragment.PACSChatListFragment;
import x.com.nubextalk.Module.Fragment.PACSFriendListFragment;

public class SharePACSActivity extends AppCompatActivity {

    private PACSFriendListFragment friendListFragPACS = new PACSFriendListFragment();
    private PACSChatListFragment chatListFragPACS = new PACSChatListFragment();


    private Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_pacs);

        /**
         * ImageView에서 받아온 intent값을 bundle로 저장하여 Fragment에게 뿌려준다
         */
        Intent intent = getIntent();
        bundle = intent.getExtras();

        TabLayout tabLayout                 = findViewById(R.id.tablayout_PACS);
        ViewPager2 viewPager2               = findViewById(R.id.main_PACS_pager);
        PACSPagerAdapter pacsPagerAdapter   = new PACSPagerAdapter(this, bundle);

        viewPager2.setAdapter(pacsPagerAdapter);
        /** Tablayout과 ViewPager를 연결한다 **/
        new TabLayoutMediator(tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        if(position == 0)
                            tab.setText("친구");
                        else if(position == 1)
                            tab.setText("채팅");
                    }
                }).attach();

//        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
//                if (positionOffsetPixels == 0) {
//                    viewPager2.setCurrentItem(position);
//                }
//            }
//        });


        friendListFragPACS.setArguments(bundle);


    }
}
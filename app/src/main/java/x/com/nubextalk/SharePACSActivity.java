/*
 * Created By JONGHO, LEE, Researcher of Institute of Biomedical Engineering, TechHeim Co.,Ltd.
 * on  2021.
 * This software is not stable health-care system and not certificated any health-care authority(like CE, FDA)
 * Prohibit redistribution, modification, commercial use.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import x.com.nubextalk.Module.Adapter.PACSPagerAdapter;
import x.com.nubextalk.Module.Fragment.PACSChatListFragment;
import x.com.nubextalk.Module.Fragment.PACSFriendListFragment;

import static x.com.nubextalk.Module.CodeResources.TITLE_CHAT_LIST;
import static x.com.nubextalk.Module.CodeResources.TITLE_FRIEND_LIST;

public class SharePACSActivity extends AppCompatActivity {

    private PACSFriendListFragment mPACSFriendListFrag = new PACSFriendListFragment();
    private PACSChatListFragment mPACSChatListFrag = new PACSChatListFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_pacs);

        /**
         * ImageView에서 받아온 intent값을 bundle로 저장하여 Fragment에게 뿌려준다
         */
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        TabLayout tabLayout = findViewById(R.id.tablayout_PACS);
        ViewPager2 viewPager2 = findViewById(R.id.main_PACS_pager);
        PACSPagerAdapter pacsPagerAdapter = new PACSPagerAdapter(this, bundle);

        viewPager2.setAdapter(pacsPagerAdapter);
        /** Tablayout과 ViewPager를 연결한다 **/
        new TabLayoutMediator(tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        if (position == 0)
                            tab.setText(TITLE_FRIEND_LIST);
                        else if (position == 1)
                            tab.setText(TITLE_CHAT_LIST);
                    }
                }).attach();

        mPACSFriendListFrag.setArguments(bundle);
    }
}
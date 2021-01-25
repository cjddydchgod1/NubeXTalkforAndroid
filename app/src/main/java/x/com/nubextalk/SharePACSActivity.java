/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import x.com.nubextalk.Module.Fragment.PACSChatListFragment;
import x.com.nubextalk.Module.Fragment.PACSFriendListFragment;

public class SharePACSActivity extends AppCompatActivity implements View.OnClickListener {
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    private PACSFriendListFragment friendListFragPACS = new PACSFriendListFragment();
    private PACSChatListFragment chatListFragPACS = new PACSChatListFragment();

    private Button mFriendListBtn;
    private Button mChatListBtn;

    private String studyId;
    private String description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_pacs);

        mFriendListBtn  = findViewById(R.id.btn_friend_list);
        mChatListBtn    = findViewById(R.id.btn_chat_list);

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_PACS_frame_layout, friendListFragPACS).commitAllowingStateLoss();

        mFriendListBtn.setOnClickListener(this);
        mChatListBtn.setOnClickListener(this);

        Intent intent = getIntent();
        studyId     = intent.getExtras().getString("studyId");
        description = intent.getExtras().getString("description");

    }

    @Override
    public void onClick(View view) {
        fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        Fragment fragment;
        switch (view.getId()) {
            case R.id.btn_friend_list :
                fragmentTransaction.replace(R.id.main_PACS_frame_layout, friendListFragPACS).commitAllowingStateLoss();
                fragment = new PACSFriendListFragment();
                bundle.putString("studyId", studyId);
                bundle.putString("description", description);
                fragment.setArguments(bundle);
                break;
            case R.id.btn_chat_list :
                fragmentTransaction.replace(R.id.main_PACS_frame_layout, chatListFragPACS).commitAllowingStateLoss();
                fragment = new PACSChatListFragment();
                bundle.putString("studyId", studyId);
                bundle.putString("description", description);
                fragment.setArguments(bundle);
                break;

        }

    }
}
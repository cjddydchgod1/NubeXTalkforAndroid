/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import x.com.nubextalk.Module.Fragment.PACSChatListFragment;
import x.com.nubextalk.Module.Fragment.PACSFriendListFragment;

import static x.com.nubextalk.Module.CodeResources.REFERENCE_ITEM_SIZE;

public class PACSPagerAdapter extends FragmentStateAdapter {

    private Bundle mBundle;

    public PACSPagerAdapter(@NonNull FragmentActivity fragmentActivity, Bundle bundle) {
        super(fragmentActivity);
        this.mBundle = bundle;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        int index = getRealPosition(position);
        Fragment fragment;
        switch (index) {
            case 1:
                fragment = new PACSChatListFragment();
                fragment.setArguments(mBundle);
                return fragment;
            default:
                fragment = new PACSFriendListFragment();
                fragment.setArguments(mBundle);
                return fragment;
        }
    }

    @Override
    public int getItemCount() {
        return REFERENCE_ITEM_SIZE;
    }

    private int getRealPosition(int pos) {
        return pos % REFERENCE_ITEM_SIZE;
    }
}

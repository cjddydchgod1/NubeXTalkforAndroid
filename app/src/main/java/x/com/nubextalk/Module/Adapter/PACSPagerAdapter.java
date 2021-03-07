/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Adapter;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import x.com.nubextalk.Module.Fragment.PACSChatListFragment;
import x.com.nubextalk.Module.Fragment.PACSFriendListFragment;

public class PACSPagerAdapter extends FragmentStateAdapter {
    private static final int REFERENCE_ITEM_SIZE = 2;
    private Bundle bundle;

    public PACSPagerAdapter(@NonNull FragmentActivity fragmentActivity, Bundle bundle) {
        super(fragmentActivity);
        this.bundle = bundle;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        int index = getRealPosition(position);
        Fragment fragment;
        Log.e("adapter", Integer.toString(position));
        switch(index){
            case 1:
                fragment = new PACSChatListFragment();
                fragment.setArguments(bundle);
                return fragment;
            default:
                fragment = new PACSFriendListFragment();
                fragment.setArguments(bundle);
                return fragment;
        }
    }

    @Override
    public int getItemCount() {
        return REFERENCE_ITEM_SIZE;
    }

    private int getRealPosition(int pos) {
        return pos%REFERENCE_ITEM_SIZE;
    }
}

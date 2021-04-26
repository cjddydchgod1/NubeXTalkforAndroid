/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

import x.com.nubextalk.Module.Fragment.TutorialFragment;

public class TutorialPagerAdapter extends FragmentStateAdapter {

    private List<Integer> layouts = new ArrayList<>();

    public TutorialPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Integer> layouts) {
        super(fragmentActivity);
        this.layouts = layouts;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return TutorialFragment.newInstance(layouts.get(position));
    }

    @Override
    public int getItemCount() {
        return layouts.size();
    }
}

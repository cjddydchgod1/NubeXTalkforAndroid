/*
 * Created By JONGHO, LEE, Researcher of Institute of Biomedical Engineering, TechHeim Co.,Ltd.
 * on  2021.
 * This software is not stable health-care system and not certificated any health-care authority(like CE, FDA)
 * Prohibit redistribution, modification, commercial use.
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
        Fragment fragment = TutorialFragment.newInstance(layouts.get(position));
        return fragment;
    }

    @Override
    public int getItemCount() {
        return layouts.size();
    }
}

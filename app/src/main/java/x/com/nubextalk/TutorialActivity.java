/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import x.com.nubextalk.Manager.TutorialPageAnimManager;
import x.com.nubextalk.Module.Adapter.TutorialPagerAdapter;

public class TutorialActivity extends FragmentActivity {

    private ViewPager2 mTutorialViewPager;
    private FragmentStateAdapter mTutorialPagerAdapter;
    private List<Integer> mTutorialLayouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        mTutorialViewPager = findViewById(R.id.tutorial_viewpager);
        mTutorialViewPager.setPageTransformer(new TutorialPageAnimManager());
        mTutorialLayouts = new ArrayList<>();
        mTutorialLayouts.add(R.layout.tutorial_1);
        mTutorialLayouts.add(R.layout.tutorial_2);

        mTutorialPagerAdapter = new TutorialPagerAdapter(this, mTutorialLayouts);
        mTutorialViewPager.setAdapter(mTutorialPagerAdapter);
    }
}

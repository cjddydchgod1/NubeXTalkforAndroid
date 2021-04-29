/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;
import x.com.nubextalk.Manager.TutorialPageAnimManager;
import x.com.nubextalk.Module.Adapter.TutorialPagerAdapter;

public class TutorialActivity extends FragmentActivity {

    private ViewPager2 mTutorialViewPager;
    private FragmentStateAdapter mTutorialPagerAdapter;
    private List<Integer> mTutorialLayouts;
    private Button mBtnTutorialEnd;
    private CircleIndicator3 mStatusCircleIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        mStatusCircleIndicator = findViewById(R.id.tutorial_status);
        mTutorialViewPager = findViewById(R.id.tutorial_viewpager);
        mTutorialViewPager.setPageTransformer(new TutorialPageAnimManager());
        mTutorialLayouts = new ArrayList<>();
        mTutorialLayouts.add(R.layout.tutorial_1);
        mTutorialLayouts.add(R.layout.tutorial_2);
        mTutorialLayouts.add(R.layout.tutorial_3);
        mTutorialLayouts.add(R.layout.tutorial_4);
        mTutorialLayouts.add(R.layout.tutorial_5);
        mTutorialLayouts.add(R.layout.tutorial_6);
        mTutorialLayouts.add(R.layout.tutorial_7);
        mTutorialLayouts.add(R.layout.tutorial_8);
        mTutorialLayouts.add(R.layout.tutorial_8);
        mTutorialLayouts.add(R.layout.tutorial_10);


        mTutorialPagerAdapter = new TutorialPagerAdapter(this, mTutorialLayouts);
        mTutorialViewPager.setAdapter(mTutorialPagerAdapter);
        mStatusCircleIndicator.setViewPager(mTutorialViewPager);
        mTutorialViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mStatusCircleIndicator.animatePageSelected((position) % mTutorialLayouts.size());
            }
        });

        mBtnTutorialEnd = findViewById(R.id.tutorial_end_btn);
        mBtnTutorialEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TutorialActivity.this, LoginActivity.class));
            }
        });
    }
}

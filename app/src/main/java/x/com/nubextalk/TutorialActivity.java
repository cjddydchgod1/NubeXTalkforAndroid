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

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;
import x.com.nubextalk.Manager.TutorialPageAnimManager;
import x.com.nubextalk.Module.Adapter.TutorialPagerAdapter;

import static x.com.nubextalk.Module.CodeResources.CONTENT_DESC1;
import static x.com.nubextalk.Module.CodeResources.CONTENT_DESC2;
import static x.com.nubextalk.Module.CodeResources.CONTENT_DESC3;
import static x.com.nubextalk.Module.CodeResources.TITLE_DESC1;
import static x.com.nubextalk.Module.CodeResources.TITLE_DESC2;
import static x.com.nubextalk.Module.CodeResources.TITLE_DESC3;

public class TutorialActivity extends FragmentActivity {

    private ViewPager2 mTutorialViewPager;
    private FragmentStateAdapter mTutorialPagerAdapter;
    private List<Integer> mTutorialLayouts;
    private Button mBtnTutorialEnd;
    private CircleIndicator3 mStatusCircleIndicator;
    private TextView mTutorialDesc1;
    private TextView mTutorialDesc2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        mTutorialDesc1 = findViewById(R.id.tutorial_description1);
        mTutorialDesc2 = findViewById(R.id.tutorial_description2);
        mStatusCircleIndicator = findViewById(R.id.tutorial_status);
        mTutorialViewPager = findViewById(R.id.tutorial_viewpager);
        mTutorialViewPager.setPageTransformer(new TutorialPageAnimManager());
        mTutorialLayouts = new ArrayList<>();
        mTutorialLayouts.add(R.layout.tutorial_chat);
        mTutorialLayouts.add(R.layout.tutorial_pacs);
        mTutorialLayouts.add(R.layout.tutorial_status);

        mTutorialPagerAdapter = new TutorialPagerAdapter(this, mTutorialLayouts);
        mTutorialViewPager.setAdapter(mTutorialPagerAdapter);
        mStatusCircleIndicator.setViewPager(mTutorialViewPager);
        mTutorialViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mStatusCircleIndicator.animatePageSelected((position) % mTutorialLayouts.size());
                switch (position) {
                    case 0:
                        mTutorialDesc1.setText(TITLE_DESC1);
                        mTutorialDesc2.setText(CONTENT_DESC1);
                        break;
                    case 1:
                        mTutorialDesc1.setText(TITLE_DESC2);
                        mTutorialDesc2.setText(CONTENT_DESC2);
                        break;
                    case 2:
                        mTutorialDesc1.setText(TITLE_DESC3);
                        mTutorialDesc2.setText(CONTENT_DESC3);
                        break;
                }

            }
        });

        mBtnTutorialEnd = findViewById(R.id.tutorial_end_btn);
        mBtnTutorialEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TutorialActivity.this, MainActivity.class));
            }
        });
    }
}

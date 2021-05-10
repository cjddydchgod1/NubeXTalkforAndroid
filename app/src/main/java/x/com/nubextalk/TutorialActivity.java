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
                        mTutorialDesc1.setText("1.채팅 기능");
                        mTutorialDesc2.setText("채팅목록에서 새로운 1:1 채팅과 단체채팅방을 생성할 수 있습니다.");
                        break;
                    case 1:
                        mTutorialDesc1.setText("2.PACS 기능");
                        mTutorialDesc2.setText("하단 3번째 PACS메뉴를 통해 PACS 정보에 접근하고 채팅을 통해 내용을 공유할 수 있습니다.");
                        break;
                    case 2:
                        mTutorialDesc1.setText("3.근무 상황 기능");
                        mTutorialDesc2.setText("자신의 상태를 선택할 수 있으며 사용자 프로필 우상단의 아이콘을 통해 현재 근무 상태를 확인할 수 있습니다.");
                        break;
                }

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

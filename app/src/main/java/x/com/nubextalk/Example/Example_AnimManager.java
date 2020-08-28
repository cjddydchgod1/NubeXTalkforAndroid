/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Example;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;

import com.joanzapata.iconify.widget.IconTextView;

import androidx.appcompat.app.AppCompatActivity;
import x.com.nubextalk.Manager.AnimManager;
import x.com.nubextalk.R;

public class Example_AnimManager extends AppCompatActivity implements View.OnClickListener {

    Button mBtnAnim, mBtnAnim2, mBtnAnim3;
    IconTextView mTextViewResult;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example_anim_manager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTextViewResult = findViewById(R.id.txtResult);
        mBtnAnim = findViewById(R.id.btnAnim);
        mBtnAnim2 = findViewById(R.id.btnAnim2);
        mBtnAnim3 = findViewById(R.id.btnAnim3);

        mBtnAnim.setOnClickListener(this);
        mBtnAnim2.setOnClickListener(this);
        mBtnAnim3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mTextViewResult.setTranslationX(0);
        mTextViewResult.setTranslationY(0);
        mTextViewResult.setAlpha(1);
        mTextViewResult.setScaleX(1);
        mTextViewResult.setScaleY(1);

        switch (v.getId()){
            case R.id.btnAnim:
                /**
                 * x : -128dp, y: -128dp 로 이동 후 다시 원점(0,0)으로 복귀 애니메이션
                 */
                float dp128 = AnimManager.dpToPx(getApplicationContext(), 128);
                new AnimManager(
                        AnimManager.make(mTextViewResult, AnimManager.ANIM_SHORT).translationX(dp128 * -1).setInterpolator(new AccelerateInterpolator()),
                        AnimManager.make(mTextViewResult, AnimManager.ANIM_SHORT).translationY(dp128 * -1).setInterpolator(new AccelerateInterpolator())
                ).setEndListener(animation -> {
                    new AnimManager(
                            AnimManager.make(mTextViewResult, 800, 1000).translationX(0).setInterpolator(new BounceInterpolator()),
                            AnimManager.make(mTextViewResult, 800, 1000).translationY(0).setInterpolator(new BounceInterpolator())
                    ).start(AnimManager.TOGETHER);
                }).start(AnimManager.SEQ);
                break;
            case R.id.btnAnim2:
                /**
                 * Scale 2배 확대 후 원크기로(x1) 복귀 애니메이션
                 */
                new AnimManager(
                        AnimManager.make(mTextViewResult, AnimManager.ANIM_SHORT).scales(2).setInterpolator(new DecelerateInterpolator())
                ).setEndListener(animation -> {
                    new AnimManager(
                            AnimManager.make(mTextViewResult, 800, 1000).scales(1).setInterpolator(new DecelerateInterpolator())
                    ).start(AnimManager.TOGETHER);
                }).start(AnimManager.SEQ);
                break;
            case R.id.btnAnim3:
                /**
                 * Alpha 0(Hide) 후 1(Visible)로 복귀 애니메이션
                 */
                new AnimManager(
                        AnimManager.make(mTextViewResult, AnimManager.ANIM_SHORT).alpha(0).setInterpolator(new AccelerateInterpolator())
                ).setEndListener(animation -> {
                    new AnimManager(
                            AnimManager.make(mTextViewResult, 800, 1000).alpha(1).setInterpolator(new AccelerateInterpolator())
                    ).start(AnimManager.TOGETHER);
                }).start(AnimManager.SEQ);
                break;
        }
    }
}

/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Manager;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import com.bartoszlipinski.viewpropertyobjectanimator.ViewPropertyObjectAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * Animation Module
 *  - make로 Animation 정의
 *  - start로 Animation 동작
 *  - Animation Listener
 *
 * 사용법
 *  new AnimManager(
 *      AnimManager.make(<View>, <Duration>).alpha(1).setInterpolator(<Interporator>),
 *      AnimManager.make(<View>, <Duration>).alpha(0).setInterpolator(<Interporator>),
 *      ...
 *  ).start(<TOGETHER / SEQ>)
 * */
public class AnimManager implements Animator.AnimatorListener {
    private AnimatorSet mAnimatorSet;
    private List<Animator> animSets = new ArrayList<>();
    private int cnt = 0;

    public static final int SHORTEST = 300;
    public static final int SHORT = 800;
    public static final int LONG  = 1600;
    public static final String ANIM_SHORTEST    = "0";  //Animation Duration
    public static final String ANIM_SHORT       = "1";  //Animation Duration
    public static final String ANIM_LONG        = "2";  //Animation Duration
    public static final int TOGETHER    = 0;            //All Animation Play Together
    public static final int SEQ         = 1;            //All Animation Play Sequence

    /**
     *
     * @param animataions
     */
    public AnimManager(ViewPropertyObjectAnimator... animataions){
        for(ViewPropertyObjectAnimator anim : animataions){
            animSets.add(anim.get());
        }
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.addListener(this);
    }

    public void start(){
        if(mAnimatorSet != null){
            if(animSets.size() > 0){
                mAnimatorSet.playTogether(animSets);
                mAnimatorSet.start();
            }
        }
    }

    public void start(int mode){
        if(mAnimatorSet != null){
            if(animSets.size() > 0){
                if(mode == SEQ){
                    mAnimatorSet.playSequentially(animSets);
                }
                else{
                    mAnimatorSet.playTogether(animSets);
                }
                mAnimatorSet.start();
            }
        }
    }

    public void end(){
        mAnimatorSet.end();
    }

    /** Listener **/
    @Override
    public void onAnimationStart(Animator animation) {
        if(cnt == 0 && mStartListener != null){
            mStartListener.onStart(animation);
        }
        if(mStartStepListener != null){
            mStartStepListener.onStartStep(animation);
        }

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if(mEndListener != null){
            mEndListener.onEnd(animation);
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    /** InterFace **/
    private onStart mStartListener;
    private onEnd mEndListener;
    private onStartStep mStartStepListener;
    public interface onStart{
        void onStart(Animator animation);
    }
    public interface onStartStep{
        void onStartStep(Animator animation);
    }
    public interface onEnd{
        void onEnd(Animator animation);
    }
    public AnimManager setStartListener(onStart onStartListener) {
        this.mStartListener = onStartListener;
        return this;
    }
    public AnimManager setEndListener(onEnd onEndListener) {
        this.mEndListener = onEndListener;
        return this;
    }
    public AnimManager setStartStepListener(onStartStep onStartStepListener) {
        this.mStartStepListener = onStartStepListener;
        return this;
    }

    /**
     * Animation Factory 생성법 (ViewPropertyObjectAnimator)
     * ex : AnimManager.make(<View>, <Duration>).alpha(1).setInterpolator(<Interporator>)
     *
     * 지원 Animation(attribute)
     *  - Alpha, scaleX, scaleY, scrollY, height ... 등
     *
     * Interporator(분개) : Animation 시점시점과 종료시점까지의 변화 과정에 대한 벡터 궤적 값
     *  - 8가지 Interporator (AccelerateDecelerate, Accelerate, Aniticipate, Overshoot, Bounce, Cycle, Decelerate, Linear)
     *  - 참조 : https://medium.com/@gus0000123/android-animation-interpolar-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0-8d228f4fc3c3
     *
     * 자세한 사용법은 아래 라이브러리 홈페이지 참조
     *      https://github.com/blipinsk/ViewPropertyObjectAnimator
     */
    public static ViewPropertyObjectAnimator make(View view){
        return ViewPropertyObjectAnimator.animate(view);
    }
    public static ViewPropertyObjectAnimator make(View view, String config){
        if(config.equals(ANIM_SHORTEST)){
            return ViewPropertyObjectAnimator.animate(view).setInterpolator(new DecelerateInterpolator()).setDuration(SHORTEST).setStartDelay(0);
        }
        else if(config.equals(ANIM_SHORT)){
            return ViewPropertyObjectAnimator.animate(view).setInterpolator(new DecelerateInterpolator()).setDuration(SHORT).setStartDelay(0);
        }
        else{
            return ViewPropertyObjectAnimator.animate(view).setInterpolator(new DecelerateInterpolator()).setDuration(LONG).setStartDelay(0);
        }
    }
    public static ViewPropertyObjectAnimator make(View view, int duration){
        return ViewPropertyObjectAnimator.animate(view).setInterpolator(new DecelerateInterpolator()).setDuration(duration).setStartDelay(300);
    }
    public static ViewPropertyObjectAnimator make(View view, int duration, int startDelay){
        return ViewPropertyObjectAnimator.animate(view).setInterpolator(new DecelerateInterpolator()).setDuration(duration).setStartDelay(startDelay);
    }

    /** Methods **/

    /**
     * dp 값을 px 값으로 변환
     * @param ctx
     * @param val
     * @return
     */
    public static int dpToPx(Context ctx, int val) {
        return (int) (val * Resources.getSystem().getDisplayMetrics().density);
    }


    /**
     * px 값을 dp 값으로 변환
     * @param ctx
     * @param val
     * @return
     */
    public static int pxToDp(Context ctx, int val) {
        return (int) (val / Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * 화면의 크기 구하기
     *
     * @param wm
     * @return
     */
    public static DisplayMetrics getWindowMetrics(WindowManager wm){
        DisplayMetrics dp = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dp);
        return dp;
    }
}

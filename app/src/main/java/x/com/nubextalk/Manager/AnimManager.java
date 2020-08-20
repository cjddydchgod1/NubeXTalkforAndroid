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

public class AnimManager implements Animator.AnimatorListener {
    private AnimatorSet mAnimatorSet;
    private List<Animator> animSets = new ArrayList<>();
    private int cnt = 0;

    public static final int SHORTEST = 300;
    public static final int SHORT = 800;
    public static final int LONG  = 1600;
    public static final String ANIM_SHORTEST    = "0";
    public static final String ANIM_SHORT       = "1";
    public static final String ANIM_LONG        = "2";
    public static final int TOGETHER    = 0;
    public static final int SEQ         = 1;

    /** Constructor **/
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

    /** Animation Factory **/
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
    public static int dpToPx(Context ctx, int val) {
        return (int) (val * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(Context ctx, int val) {
        return (int) (val / Resources.getSystem().getDisplayMetrics().density);
    }

    public static DisplayMetrics getWindowMetrics(WindowManager wm){
        DisplayMetrics dp = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dp);
        return dp;
    }
}

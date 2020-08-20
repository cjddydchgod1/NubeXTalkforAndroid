package x.com.nubextalk.Module.FaNavi;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;


public class FaNaviView extends NavigationView {

    public FaNaviView(Context context) {
        super(context);
    }

    public FaNaviView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FaNaviView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setNavigationItemSelectedListener(@Nullable OnNavigationItemSelectedListener listener) {
        super.setNavigationItemSelectedListener(listener);
    }
}

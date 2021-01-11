package x.com.nubextalk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class LoadingActivity extends Activity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        startLoading();
    }

    private void startLoading() {
        final LoadingActivity activity = this;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    // 임시로 로그인하는 화면 만들기
                    startActivity(new Intent(activity, LoginActivity.class));
//                    startActivity(new Intent(activity, MainActivity.class));
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 2000);
    }
}

package x.com.nubextalk;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;

import static x.com.nubextalk.Module.CodeResources.*;

public class LoadingActivity extends Activity {

    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        /** Theme Mode 설정 **/
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int option = sharedPreferences.getInt(THEME_MODE, USER_MODE);
        switch (option) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case 2:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                }
                break;
        }
        startLoading();
    }

    private void startLoading() {
        final LoadingActivity activity = this;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                try {
                    // 로그인하는 화면
                    startActivity(new Intent(activity, LoginActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000);
    }
}

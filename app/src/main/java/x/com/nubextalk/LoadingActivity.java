/*
 * Created By JONGHO, LEE, Researcher of Institute of Biomedical Engineering, TechHeim Co.,Ltd.
 * on  2021.
 * This software is not stable health-care system and not certificated any health-care authority(like CE, FDA)
 * Prohibit redistribution, modification, commercial use.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;

import io.realm.Realm;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.Config;

import static x.com.nubextalk.Module.CodeResources.*;

public class LoadingActivity extends Activity {
    private Realm mRealm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(UtilityManager.getRealmConfig());
        /** Theme Mode 설정 **/
        Config theme = Config.getThemeMode(mRealm);
        if(theme == null) {
            theme = new Config();
            theme.setCODENAME("Theme");
            theme.setCODE("Theme");
            theme.setExt1(USER_MODE);
        }
        String theme_mode = theme.getExt1();

        switch (theme_mode) {
            case LIGHT_MODE:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case DARK_MODE:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case USER_MODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                }
                break;
        }

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
                    // 로그인하는 화면
                    startActivity(new Intent(activity, LoginActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000);
    }
}

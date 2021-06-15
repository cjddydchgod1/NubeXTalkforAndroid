/*
 * Created By JONGHO, LEE, Researcher of Institute of Biomedical Engineering, TechHeim Co.,Ltd.
 * on  2021.
 * This software is not stable health-care system and not certificated any health-care authority(like CE, FDA)
 * Prohibit redistribution, modification, commercial use.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import static x.com.nubextalk.Module.CodeResources.LIGHT_MODE;
import static x.com.nubextalk.Module.CodeResources.DARK_MODE;
import static x.com.nubextalk.Module.CodeResources.USER_MODE;

import io.realm.Realm;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.Config;

public class ThemeModeActivity extends AppCompatActivity {
    private RadioGroup mRadioGroup;
    private Realm mRealm;
    private Config mTheme;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_mode);
        mRealm = Realm.getInstance(UtilityManager.getRealmConfig());
        mTheme = Config.getThemeMode(mRealm);
        if(mTheme == null) {
            mTheme = new Config();
            mTheme.setCODENAME("Theme");
            mTheme.setCODE("Theme");
            mTheme.setExt1(USER_MODE);
        }
        String theme_mode = mTheme.getExt1();

        mRadioGroup  = findViewById(R.id.radioGroupTheme);
        RadioButton rb_1 = findViewById(R.id.rgt_btn1);
        RadioButton rb_2 = findViewById(R.id.rgt_btn2);
        RadioButton rb_3 = findViewById(R.id.rgt_btn3);
        rb_1.setChecked(theme_mode.equals(LIGHT_MODE));
        rb_2.setChecked(theme_mode.equals(DARK_MODE));
        rb_3.setChecked(theme_mode.equals(USER_MODE));
        changeTheme();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("requestChatList", RESULT_FIRST_USER);
        startActivity(intent);
    }

    public void changeTheme() {
        mRadioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
            mRealm.executeTransaction(realm1 -> {
                if(i == R.id.rgt_btn1){
                    mTheme.setExt1(LIGHT_MODE);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else if(i == R.id.rgt_btn2){
                    mTheme.setExt1(DARK_MODE);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else if(i == R.id.rgt_btn3){
                    mTheme.setExt1(USER_MODE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                    }
                }
                realm1.copyToRealmOrUpdate(mTheme);
            });
        });

    }
}
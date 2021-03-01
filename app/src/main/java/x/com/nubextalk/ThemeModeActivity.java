/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import static x.com.nubextalk.Module.CodeResources.*;
import x.com.nubextalk.Model.Config;

public class ThemeModeActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_mode);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        radioGroup = findViewById(R.id.radioGroupTheme);
        radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
            if(i == R.id.rgt_btn1){
                sharedPreferences.edit().putInt(THEME_MODE, LIGHT_MODE).apply();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if(i == R.id.rgt_btn2){
                sharedPreferences.edit().putInt(THEME_MODE, DARK_MODE).apply();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else if(i == R.id.rgt_btn3){
                sharedPreferences.edit().putInt(THEME_MODE, USER_MODE).apply();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                }
            }
        });
    }
}
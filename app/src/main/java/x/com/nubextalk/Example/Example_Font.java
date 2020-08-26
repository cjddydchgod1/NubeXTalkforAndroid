/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Example;

import androidx.appcompat.app.AppCompatActivity;
import x.com.nubextalk.R;

import android.os.Bundle;

import com.joanzapata.iconify.widget.IconTextView;

/**
 * Font Awesome 기반 Icon을 TextView, Button, ToggleButton 등으로 Rendering 할 수 있는 모듈
 * - Font Awesome : 웹 HTML5용 ICON 라이브러리
 * - Android Iconify 라이브러리를 이용하여 FontAwesome 라이브러리를 안드로이드에서도 사용할 수 있도록 만든 라이브러리
 *
 * [기본 사용법]
 * - {fas-check}
 * - 카테고리 Solid     : fas-
 * - 카테고리 Regular   : far-
 * - 카테고리 Brand     : fab-
 *
 *  FontAwesome Icon Sheet 참조
 *  https://fontawesome.com/icons?d=gallery&m=free
 */
public class Example_Font extends AppCompatActivity {

    private IconTextView mIconTextView;

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example_font);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mIconTextView = findViewById(R.id.txtResult);

        String icon = "";
        icon += "{fas-check} Check(Solid)\n\n";
        icon += "{fas-check #FF0000} Check(Solid) Color #FF0000\n\n";
        icon += "{fas-check 32dp} Check(Solid) Size 32dp\n\n\n\n";

        icon += "{far-user-circle} User-Circle(Reqular)\n\n";
        icon += "{far-user-circle #FF0000} User-Circle(Reqular) Color #FF0000\n\n";
        icon += "{far-user-circle 32dp} User-Circle(Reqular) Size 32dp\n\n\n\n";

        icon += "{fab-android} Android(Brand)\n\n";
        icon += "{fab-android #FF0000} Android(Brand) Color #FF0000\n\n";
        icon += "{fab-android 32dp} Android(Brand) Size 32dp\n\n\n\n";


        mIconTextView.setText(icon);
    }
}

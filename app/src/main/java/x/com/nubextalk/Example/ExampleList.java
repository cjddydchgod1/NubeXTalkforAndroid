/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Example;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import x.com.nubextalk.MainActivity;
import x.com.nubextalk.R;

/**
 * 예제 소스
 * 1. AnimManager
 * 2. FontAwesome
 * 3. HttpManager
 * 4. Jsoup
 *
 */
public class ExampleList extends AppCompatActivity implements View.OnClickListener {

    Button mBtnAnim, mBtnFont, mBtnHttp, mBtnJsoup, mBtnFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example_list);

        mBtnAnim = findViewById(R.id.btnAnim);
        mBtnFont = findViewById(R.id.btnFont);
        mBtnHttp = findViewById(R.id.btnHttp);
        mBtnJsoup = findViewById(R.id.btnJsoup);
        mBtnFunctions = findViewById(R.id.btnFunctions);

        mBtnAnim.setOnClickListener(this);
        mBtnFont.setOnClickListener(this);
        mBtnHttp.setOnClickListener(this);
        mBtnJsoup.setOnClickListener(this);
        mBtnFunctions.setOnClickListener(this);

        /**
         * FCM Token 확인 함수
         */
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TOKEN", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.d("TOKEN", token);
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAnim:
                startActivity(new Intent(ExampleList.this, Example_AnimManager.class));
                break;
            case R.id.btnFont:
                startActivity(new Intent(ExampleList.this, Example_Font.class));
                break;
            case R.id.btnHttp:
                startActivity(new Intent(ExampleList.this, Example_HttpManager.class));
                break;
            case R.id.btnJsoup:
                startActivity(new Intent(ExampleList.this, Example_Jsoup.class));
                break;
            case R.id.btnFunctions:
                startActivity(new Intent(ExampleList.this, Example_FireBaseFunctions.class));
                break;
        }
    }
}

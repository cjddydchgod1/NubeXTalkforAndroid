/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Example;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import x.com.nubextalk.Manager.HttpManager;
import x.com.nubextalk.R;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class Example_HttpManager extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnHttp;
    private TextView mTextViewResult;
    private String mTextResult;
    private String URL_EXAMPLE_URL = "https://www.kebhana.com/foreign/rate/wpfxd458_01p_01.do";

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example_http_manager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBtnHttp = findViewById(R.id.btnHttp);
        mTextViewResult = findViewById(R.id.txtResult);

        mBtnHttp.setOnClickListener(this);
        initTextView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnHttp:
                initTextView();

                String passData = "Passed Data";
                mTextResult = "Start Http Request\n";
                mTextResult += "[Request] Url : " + URL_EXAMPLE_URL + "\n";
                mTextResult += "[Request] Pass Data : " + passData + "\n";
                updateTextView();

                new HttpManager(URL_EXAMPLE_URL, getApplicationContext(), passData, new handler());
                break;
        }
    }

    void initTextView(){
        mTextResult = "";
        mTextViewResult.setText(mTextResult);
    }

    void updateTextView(){
        mTextViewResult.setText(mTextResult);
    }

    private class handler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            final ArrayList<Object> arrayList = (ArrayList<Object>) msg.obj;
            final Context context = (Context) arrayList.get(0);

            switch (msg.what) {
                case -1:
                    Log.e(getClass().getName(),"실패하였습니다");
                    break;
                case 0:
                    mTextResult += "\n\nReceive Request\n";
                    mTextResult += "[Receive] Url : "  + URL_EXAMPLE_URL + "\n";
                    mTextResult += "[Receive] Pass Data : " + arrayList.get(1) + "\n";
                    mTextResult += "[Receive] Request Result : " + "\n";
                    mTextResult += String.valueOf(arrayList.get(2));
                    updateTextView();
                    break;
            }
        }
    }
}

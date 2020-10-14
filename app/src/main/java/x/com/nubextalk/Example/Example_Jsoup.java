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
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * HTML, JSON 등의 문서를 Parsing 하는 라이브러리
 * - Document, Element(s) 단위로 Parsing 됨
 * - Parsing 문법은 CSS Query 기반
 * - 가이드라인 공식 홈페이지 : https://jsoup.org/cookbook/introduction/parsing-a-document
 */
public class Example_Jsoup extends AppCompatActivity implements View.OnClickListener {

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
        setContentView(R.layout.example_jsoup);
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

                mTextResult = "Start Http Request\n";
                updateTextView();

                new HttpManager(URL_EXAMPLE_URL, getApplicationContext(), new handler());
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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
                    mTextResult += "[Result]Request Result(Jsoup) : \n";
                    updateTextView();

                    try{
                        Document document = Jsoup.parse(String.valueOf(arrayList.get(2)));
                        Elements elements = document.select("tbody").select("tr");
                        for(Element el : elements){
                            String code = el.select("td").get(0).text();
                            String codeName = el.select("td").get(1).text();
                            String currency = el.select("td").get(8).text();

                            mTextResult += code + "(" + codeName + ") : " + currency + "원\n";
                        }
                        updateTextView();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}

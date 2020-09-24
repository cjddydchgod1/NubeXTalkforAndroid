/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Example;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconButton;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import x.com.nubextalk.Manager.AnimManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.Example_Model_Address;
import x.com.nubextalk.Module.Adapter.Example_AddressAdapter;
import x.com.nubextalk.R;

/**
 * 종합예제
 *  1. Realm DataBase
 *      - 가이드라인 공식 홈페이지 : https://realm.io/docs/java/5.8.0/#getting-started
 *
 *  2. DrawerLayout 사용법
 *  3. RecyclerView 사용법
 *
 */
public class Example_Address extends AppCompatActivity implements Example_AddressAdapter.onItemSelectedListener, View.OnClickListener {

    private Realm realm;
    private Example_AddressAdapter mAdapter;

    private Toolbar mToolbar;
    private LinearLayout mBottomWrapper;
    private IconTextView mTextResult;
    private IconButton mButtonSort1, mButtonSort2, mButtonFilter1, mButtonFilter2;
    private RecyclerView mRecycleView;

    private RealmResults<Example_Model_Address> mResults;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example_address);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        realm           = Realm.getInstance(UtilityManager.getRealmConfig());
        mTextResult     = findViewById(R.id.middleTitle);
        mButtonSort1    = findViewById(R.id.btnSort1);
        mButtonSort2    = findViewById(R.id.btnSort2);
        mButtonFilter1  = findViewById(R.id.btnFilter1);
        mButtonFilter2  = findViewById(R.id.btnFilter2);
        mBottomWrapper  = findViewById(R.id.bottomWrapper);
        mRecycleView    = findViewById(R.id.mRecyclerView);

        mResults = Example_Model_Address.getAll(realm);
        if(mResults.size() == 0){
            Example_Model_Address.init(this, realm);
            mResults = Example_Model_Address.getAll(realm);
        }

        mAdapter = new Example_AddressAdapter(mResults);
        mAdapter.setOnItemSelectedListener(this);

        mRecycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        mRecycleView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down));
        mRecycleView.setAdapter(mAdapter);
        mRecycleView.scheduleLayoutAnimation();

        mTextResult.setText(mResults.size() + "개의 결과");

        mButtonSort1.setOnClickListener(this);
        mButtonSort2.setOnClickListener(this);
        mButtonFilter1.setOnClickListener(this);
        mButtonFilter2.setOnClickListener(this);

    }

    /**
     * 주소 목록에서 item 선택시 이벤트
     */
    @Override
    public void onSelected(Example_Model_Address address) {
        initBottomSheet(address);
        new AnimManager(
                AnimManager.make(mBottomWrapper, AnimManager.SHORT).translationY(3000).translationY(0).setInterpolator(new DecelerateInterpolator())
        ).start(AnimManager.TOGETHER);
    }

    /**
     * Bottom Sheet 로드 함수
     * @param address
     */
    private void initBottomSheet(Example_Model_Address address) {
        LayerDrawable stars = (LayerDrawable) ((RatingBar) mBottomWrapper.findViewById(R.id.rowRatingBar)).getProgressDrawable();
        ((TextView) mBottomWrapper.findViewById(R.id.rowName)).setText(address.getName());
        ((TextView) mBottomWrapper.findViewById(R.id.rowRating)).setText(String.valueOf(address.getRating()));
        ((TextView) mBottomWrapper.findViewById(R.id.rowGroup)).setText(address.getTypeCode().toUpperCase());
        ((RatingBar) mBottomWrapper.findViewById(R.id.rowRatingBar)).setRating(Float.parseFloat(address.getRating()));

        mBottomWrapper.findViewById(R.id.mClose).setOnClickListener(v -> onBackPressed());

        LinearLayout mRowWrapper =  mBottomWrapper.findViewById(R.id.rowWrapper);
        mRowWrapper.removeAllViews();
        mRowWrapper.invalidate();

        LayoutInflater inflater = LayoutInflater.from(this);
        if(UtilityManager.checkString(address.getDesc())){
            LinearLayout l = (LinearLayout) inflater.inflate(R.layout.example_address_bottom_row, null, false);
            ((IconTextView)l.findViewById(R.id.icon)).setText("{fas-comment-dots}");
            ((IconTextView)l.findViewById(R.id.txt)).setText(address.getDesc());
            mRowWrapper.addView(l);
        }
        if(UtilityManager.checkString(address.getAddress())){
            LinearLayout l = (LinearLayout) inflater.inflate(R.layout.example_address_bottom_row, null, false);
            ((IconTextView)l.findViewById(R.id.icon)).setText("{fas-map-marker-alt}");
            ((IconTextView)l.findViewById(R.id.txt)).setText(address.getAddress());
            l.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(address.getUrl()))));
            l.setOnLongClickListener(v -> {
                UtilityManager.fireVibe(v.getContext(),60,30);
                ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("Copied Text", address.getAddress()));
                return true;
            });
            mRowWrapper.addView(l);
        }
        if(UtilityManager.checkString(address.getPhone())){
            LinearLayout l = (LinearLayout) inflater.inflate(R.layout.example_address_bottom_row, null, false);
            ((IconTextView)l.findViewById(R.id.icon)).setText("{fas-phone}");
            ((IconTextView)l.findViewById(R.id.txt)).setText(address.getPhone());
            l.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+address.getPhone()))));
            l.setOnLongClickListener(v -> {
                UtilityManager.fireVibe(v.getContext(),60,30);
                ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("Copied Text", address.getPhone()));
                return true;
            });
            mRowWrapper.addView(l);
        }
        if(UtilityManager.checkString(address.getEmail())){
            LinearLayout l = (LinearLayout) inflater.inflate(R.layout.example_address_bottom_row, null, false);
            ((IconTextView)l.findViewById(R.id.icon)).setText("{fas-envelope}");
            ((IconTextView)l.findViewById(R.id.txt)).setText(address.getEmail());
            l.setOnClickListener(v -> {
                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+address.getEmail()));
                i.putExtra(Intent.EXTRA_SUBJECT,"Inquiry");
                i.putExtra(Intent.EXTRA_TEXT, "Dear "+address.getName()+",\n\n\n");
                startActivity(i);
            });
            l.setOnLongClickListener(v -> {
                UtilityManager.fireVibe(v.getContext(),60,30);
                ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("Copied Text", address.getEmail()));
                return true;
            });
            mRowWrapper.addView(l);
        }
        if(UtilityManager.checkString(address.getWebsite())){
            LinearLayout l = (LinearLayout) inflater.inflate(R.layout.example_address_bottom_row, null, false);
            ((IconTextView)l.findViewById(R.id.icon)).setText("{fas-globe-americas}");
            ((IconTextView)l.findViewById(R.id.txt)).setText(address.getWebsite());
            ((IconTextView)l.findViewById(R.id.txt)).setLines(1);
            l.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(address.getWebsite()))));
            l.setOnLongClickListener(v -> {
                UtilityManager.fireVibe(v.getContext(),60,30);
                ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("Copied Text", address.getWebsite()));
                return true;
            });
            mRowWrapper.addView(l);
        }
        if(UtilityManager.checkString(address.getRuntime())){
            LinearLayout l = (LinearLayout) inflater.inflate(R.layout.example_address_bottom_row, null, false);
            ((IconTextView)l.findViewById(R.id.icon)).setText("{fas-clock}");
            ((IconTextView)l.findViewById(R.id.txt)).setText(address.getRuntime().replaceAll(",","\n"));
            l.setOnLongClickListener(v -> {
                UtilityManager.fireVibe(v.getContext(),60,30);
                ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("Copied Text", address.getRuntime()));
                return true;
            });
            mRowWrapper.addView(l);
        }
        mBottomWrapper.findViewById(R.id.rowWRapperName).setOnLongClickListener(v -> {
            UtilityManager.fireVibe(v.getContext(),60,30);
            ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("Copied Text", address.getName()));
            return true;
        });

    }

    @Override
    public void onBackPressed() {
        if(mBottomWrapper.getTranslationY() == 0){
            new AnimManager(
                    AnimManager.make(mBottomWrapper, AnimManager.SHORT).translationY(0).translationY(3000).setInterpolator(new DecelerateInterpolator())
            ).start(AnimManager.TOGETHER);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSort1:
                mResults = realm.where(Example_Model_Address.class).sort("name", Sort.ASCENDING).findAll();
                break;
            case R.id.btnSort2:
                mResults = realm.where(Example_Model_Address.class).sort("name", Sort.DESCENDING).findAll();
                break;
            case R.id.btnFilter1:
                mResults = realm.where(Example_Model_Address.class).equalTo("typeCode", "RESTAURANT").findAll();
                break;
            case R.id.btnFilter2:
                mResults = realm.where(Example_Model_Address.class).equalTo("typeCode", "VIEWPOINT").findAll();
                break;
        }
        mAdapter.update(mResults);
    }
}

/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.joanzapata.iconify.widget.IconTextView;

import io.realm.Realm;
import x.com.nubextalk.HowToUseActivity;
import x.com.nubextalk.LoginActivity;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.R;
import x.com.nubextalk.ThemeModeActivity;

import static x.com.nubextalk.Module.CodeResources.*;

public class SettingFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private ViewGroup rootview;
    private Context mContext;
    private Activity mActivity;
    private Realm realm;
    private LinearLayout mWrapperApp, mWrapperAccount, mWrapperVesionInfo;
    private IconTextView mWrapperHowToUse;
    private Config myAccount;
    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        if(context instanceof Activity)
            mActivity = (Activity) context;
        super.onAttach(context);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        realm               = Realm.getInstance(UtilityManager.getRealmConfig());
        rootview            = (ViewGroup) inflater.inflate(R.layout.fragment_setting, container, false);

        mWrapperHowToUse      = rootview.findViewById(R.id.wrapperHowToUse);

        cleanView(mWrapperApp           = rootview.findViewById(R.id.wrapperApp));
        cleanView(mWrapperAccount       = rootview.findViewById(R.id.wrapperAccount));
        cleanView(mWrapperVesionInfo    = rootview.findViewById(R.id.wrapperVesionInfo));

        myAccount = Config.getMyAccount(realm);

        initView();
        return rootview;
    }

    private void cleanView(LinearLayout linearLayout) {
        linearLayout.removeAllViews();
        linearLayout.invalidate();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        RelativeLayout l;

        /** APP **/
        l = (RelativeLayout) inflater.inflate(R.layout.item_settings_switch, null, false);
        ((IconTextView) l.findViewById(R.id.titleRow)).setText("화면 잠금");
        Switch lockSwitch = l.findViewById(R.id.switchRow);
        lockSwitch.setChecked(myAccount.getScreenLock());
        lockSwitch.setOnCheckedChangeListener(this);
        lockSwitch.setTag(EXE_SCREENLOCK);
        l.setOnClickListener(v -> lockSwitch.performClick());
        mWrapperApp.addView(l);

        l = (RelativeLayout) inflater.inflate(R.layout.item_settings_switch, null, false);
        ((IconTextView) l.findViewById(R.id.titleRow)).setText("알람 받기");
        Switch AlarmSwitch = l.findViewById(R.id.switchRow);
        AlarmSwitch.setChecked(myAccount.getAlarm());
        AlarmSwitch.setOnCheckedChangeListener(this);
        AlarmSwitch.setTag(EXE_ALARM);
        l.setOnClickListener(v -> AlarmSwitch.performClick());
        mWrapperApp.addView(l);

        /** dark mode **/
        l = (RelativeLayout) inflater.inflate(R.layout.item_settings_simple, null, false);
        ((IconTextView) l.findViewById(R.id.titleRow)).setText("테마설정");
        l.setTag(EXE_SCREENMODE);
        l.setOnClickListener(this);
        mWrapperApp.addView(l);

        /** USER **/
        l = (RelativeLayout) inflater.inflate(R.layout.item_settings_simple, null, false);
        ((IconTextView) l.findViewById(R.id.titleRow)).setText("로그아웃");
        l.setTag(EXE_LOGOUT);
        l.setOnClickListener(this);
        mWrapperAccount.addView(l);

        /** How to use **/
        mWrapperHowToUse.setTag(EXE_HOWTOUSE);
        mWrapperHowToUse.setOnClickListener(this);

        /** Version info **/
        l = (RelativeLayout) inflater.inflate(R.layout.item_settings_simple, null, false);
        ((IconTextView) l.findViewById(R.id.titleRow)).setText("Nube X Talk 1.0.0");
        l.setTag(EXE_VERSIONINFO);
        l.setOnClickListener(this);
        mWrapperVesionInfo.addView(l);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch ((int) view.getTag()) {
            case EXE_SCREENMODE:
                intent = new Intent(mActivity, ThemeModeActivity.class);
                startActivity(intent);
                break;
            case EXE_LOGOUT:
                realm.executeTransaction(realm1 -> {
                    Config config = Config.getMyAccount(realm1);
                    config.setAutoLogin(false);
                    realm1.copyToRealmOrUpdate(config);
                });
                intent = new Intent(mActivity, LoginActivity.class);
                startActivity(intent);
                mActivity.finish();
                break;
            case EXE_HOWTOUSE:
                intent = new Intent(mActivity, HowToUseActivity.class);
                startActivity(intent);
                break;
            case EXE_VERSIONINFO:
                break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch ((int)buttonView.getTag()) {
            case EXE_SCREENLOCK:
                if(isChecked)
                    myAccount.setScreenLock(true);
                else
                    myAccount.setScreenLock(false);
                break;
            case EXE_ALARM:
                if(isChecked)
                    myAccount.setAlarm(true);
                else
                    myAccount.setAlarm(false);
                break;
        }
    }
}
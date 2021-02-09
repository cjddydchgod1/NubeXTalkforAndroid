/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.joanzapata.iconify.widget.IconTextView;

import io.realm.Realm;
import x.com.nubextalk.LoginActivity;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.R;
import static x.com.nubextalk.Module.CodeResources.*;

public class SettingFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private ViewGroup rootview;
    private Realm realm;
    private LinearLayout mWrapperScreenLock, mWrapperSetAlarm, mWrapperSetTheme, mWrapperLogout, mWrapperHowToUse, mWrapperVesionInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        realm               = Realm.getInstance(UtilityManager.getRealmConfig());
        rootview            = (ViewGroup) inflater.inflate(R.layout.fragment_setting, container, false);

        cleanView(mWrapperScreenLock    = rootview.findViewById(R.id.wrapperScreenLock));
        cleanView(mWrapperSetAlarm      = rootview.findViewById(R.id.wrapperSetAlarm));
        cleanView(mWrapperSetTheme      = rootview.findViewById(R.id.wrapperSetTheme));
        cleanView(mWrapperHowToUse      = rootview.findViewById(R.id.wrapperHowToUse));
        cleanView(mWrapperLogout        = rootview.findViewById(R.id.wrapperLogout));
        cleanView(mWrapperVesionInfo    = rootview.findViewById(R.id.wrapperVesionInfo));


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
        lockSwitch.setChecked(false);
        lockSwitch.setOnCheckedChangeListener(this);
        lockSwitch.setTag(EXE_SCREENLOCK);
        l.setOnClickListener(v -> lockSwitch.performClick());
        mWrapperScreenLock.addView(l);

        l = (RelativeLayout) inflater.inflate(R.layout.item_settings_switch, null, false);
        ((IconTextView) l.findViewById(R.id.titleRow)).setText("알람 설정");
        Switch AlarmSwitch = l.findViewById(R.id.switchRow);
        AlarmSwitch.setChecked(false);
        AlarmSwitch.setOnCheckedChangeListener(this);
        AlarmSwitch.setTag(EXE_ALARM);
        l.setOnClickListener(v -> AlarmSwitch.performClick());
        mWrapperSetAlarm.addView(l);

        /** dark mode **/
        l = (RelativeLayout) inflater.inflate(R.layout.item_settings_simple, null, false);
        ((IconTextView) l.findViewById(R.id.titleRow)).setText("테마설정");
        l.setTag(EXE_SCREENMODE);
        l.setOnClickListener(this);
        mWrapperSetTheme.addView(l);

        /** USER **/
        l = (RelativeLayout) inflater.inflate(R.layout.item_settings_simple, null, false);
        ((IconTextView) l.findViewById(R.id.titleRow)).setText("로그아웃");
        l.setTag(EXE_LOGOUT);
        l.setOnClickListener(this);
        mWrapperLogout.addView(l);

        /** how to use **/
        l = (RelativeLayout) inflater.inflate(R.layout.item_settings_simple, null, false);
        ((IconTextView) l.findViewById(R.id.titleRow)).setText("도움말");
        l.setTag(EXE_HOWTOUSE);
        l.setOnClickListener(this);
        mWrapperHowToUse.addView(l);

        /** Version info **/
        l = (RelativeLayout) inflater.inflate(R.layout.item_settings_simple, null, false);
        ((IconTextView) l.findViewById(R.id.titleRow)).setText("Vesion 1.0.0");
        l.setTag(EXE_VERSIONINFO);
        l.setOnClickListener(this);
        mWrapperVesionInfo.addView(l);
    }

    @Override
    public void onClick(View view) {
        Context context = getContext();
        switch ((int) view.getTag()) {
            case EXE_HOWTOUSE:
                break;
            case EXE_LOGOUT:
                realm.executeTransaction(realm1 -> {
                    Config config = Config.getMyAccount(realm1);
                    config.setExt5(null);
                    realm1.copyToRealmOrUpdate(config);
                });
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            case EXE_VERSIONINFO:
                break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch ((int)buttonView.getTag()) {
            case EXE_SCREENLOCK:
                break;
            case EXE_ALARM:
                break;
        }
    }
}
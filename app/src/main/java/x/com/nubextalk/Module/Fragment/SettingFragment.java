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
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.joanzapata.iconify.widget.IconTextView;

import io.realm.Realm;
import x.com.nubextalk.HowToUseActivity;
import x.com.nubextalk.LoginActivity;
import x.com.nubextalk.Manager.FcmTokenRefreshService;
import x.com.nubextalk.Manager.FireBase.FirebaseStoreManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.R;
import x.com.nubextalk.ThemeModeActivity;

import static x.com.nubextalk.Module.CodeResources.*;

public class SettingFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private ViewGroup mRootview;
    private Context mContext;
    private Activity mActivity;
    private Realm mRealm;
    private LinearLayout mWrapperApp, mWrapperAccount, mWrapperVesionInfo;
    private IconTextView mWrapperHowToUse;
    private Config mMyAccount;
    private Config mAlarm;
    private Config mAutoLogin;
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
        mRealm = Realm.getInstance(UtilityManager.getRealmConfig());
        mRootview = (ViewGroup) inflater.inflate(R.layout.fragment_setting, container, false);

        mWrapperApp           = mRootview.findViewById(R.id.wrapperApp);
        mWrapperAccount       = mRootview.findViewById(R.id.wrapperAccount);
        mWrapperVesionInfo    = mRootview.findViewById(R.id.wrapperVesionInfo);

        mActivity.setTitle(TITLE_SETTING);

        mWrapperHowToUse      = mRootview.findViewById(R.id.wrapperHowToUse);

        mMyAccount = Config.getMyAccount(mRealm);
        mAlarm = Config.getAlarm(mRealm);
        mAutoLogin = Config.getAutoLogin(mRealm);


        return mRootview;
    }

    @Override
    public void onResume() {
        initView();
        super.onResume();
    }

    @Override
    public void onDetach() {
        mRealm.close();
        super.onDetach();
    }

    private void cleanView(LinearLayout linearLayout) {
        linearLayout.removeAllViews();
        linearLayout.invalidate();
    }

    private void initView() {
        cleanView(mWrapperApp);
        cleanView(mWrapperAccount);
        cleanView(mWrapperVesionInfo);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        RelativeLayout l, l1;

        /** APP **/

        l = (RelativeLayout) inflater.inflate(R.layout.item_settings_switch, null, false);
        ((IconTextView) l.findViewById(R.id.titleRow)).setText(ALARM);
        SwitchCompat AlarmSwitch = l.findViewById(R.id.switchRow);
        AlarmSwitch.setChecked(mAlarm.getExt1().equals("true"));
        AlarmSwitch.setOnCheckedChangeListener(this);
        AlarmSwitch.setTag(EXE_ALARM);
        l.setOnClickListener(v -> AlarmSwitch.performClick());
        mWrapperApp.addView(l);

        /** dark mode **/
        l = (RelativeLayout) inflater.inflate(R.layout.item_settings_simple, null, false);
        ((IconTextView) l.findViewById(R.id.titleRow)).setText(SETTING_THEME);
        l.setTag(EXE_THEME);
        l.setOnClickListener(this);
        mWrapperApp.addView(l);

        /** USER **/
        l = (RelativeLayout) inflater.inflate(R.layout.item_settings_simple, null, false);
        ((IconTextView) l.findViewById(R.id.titleRow)).setText(LOGOUT);
        l.setTag(EXE_LOGOUT);
        l.setOnClickListener(this);
        mWrapperAccount.addView(l);

        /** How to use **/
        mWrapperHowToUse.setTag(EXE_HOW_TO_USE);
        mWrapperHowToUse.setOnClickListener(this);

        /** Version info **/
        l = (RelativeLayout) inflater.inflate(R.layout.item_settings_simple, null, false);
        ((IconTextView) l.findViewById(R.id.titleRow)).setText(VERSION);
        l.setTag(EXE_VERSION_INFO);
        l.setOnClickListener(this);
        mWrapperVesionInfo.addView(l);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch ((int) view.getTag()) {
            case EXE_THEME:
                intent = new Intent(mActivity, ThemeModeActivity.class);
                startActivity(intent);
                break;
            case EXE_LOGOUT:
                mRealm.executeTransaction(realm1 -> {
                    mAutoLogin.setExt1("false");
                    realm1.copyToRealmOrUpdate(mAutoLogin);
                });
                /** Firebase에서 Token값 삭제 **/
                FirebaseStoreManager firebaseStoreManager = new FirebaseStoreManager();
                firebaseStoreManager.deleteToken(mMyAccount.getExt1());

                /** token삭제 후 재발급 **/
                Intent tokenIntent = new Intent(mActivity, LoginActivity.class);
                tokenIntent.setClass(mActivity.getApplication(), FcmTokenRefreshService.class);
                mActivity.startService(tokenIntent);

                intent = new Intent(mActivity, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case EXE_HOW_TO_USE:
                intent = new Intent(mActivity, HowToUseActivity.class);
                startActivity(intent);
                break;
            case EXE_VERSION_INFO:
                break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mRealm.executeTransaction(realm1 -> {
            switch ((int)buttonView.getTag()) {
                case EXE_ALARM:
                    if(isChecked) {
                        mAlarm.setExt1("true");
                    }
                    else {
                        mAlarm.setExt1("false");
                    }
                    break;
            }
            mRealm.copyToRealmOrUpdate(mAlarm);
        });
    }
}
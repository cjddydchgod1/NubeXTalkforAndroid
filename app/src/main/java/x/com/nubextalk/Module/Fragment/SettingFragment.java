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
import x.com.nubextalk.Module.CodeResources;
import x.com.nubextalk.R;
import x.com.nubextalk.Module.CodeResources.*;

import static x.com.nubextalk.Module.CodeResources.EXE_LOGOUT;
import static x.com.nubextalk.Module.CodeResources.EXE_SWITCH;

public class SettingFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private ViewGroup rootview;
    private Realm realm;
    private LinearLayout mWrapperUser, mWrapperApp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        realm           = Realm.getInstance(UtilityManager.getRealmConfig());
        rootview        = (ViewGroup) inflater.inflate(R.layout.fragment_setting, container, false);
        mWrapperUser    = rootview.findViewById(R.id.wrapperUser);
        mWrapperApp     = rootview.findViewById(R.id.wrapperApp);

        initView();
        return rootview;
    }

    private void cleanView() {
        mWrapperUser.removeAllViews();
        mWrapperUser.invalidate();

        mWrapperApp.removeAllViews();
        mWrapperApp.invalidate();
    }

    private void initView() {
        cleanView();

        LayoutInflater inflater = LayoutInflater.from(getContext());
        RelativeLayout l;

        /** USER **/
        l = (RelativeLayout) inflater.inflate(R.layout.item_settings_simple, null, false);
        ((IconTextView) l.findViewById(R.id.titleRow)).setText("로그아웃");
        l.setTag(EXE_LOGOUT);
        l.setOnClickListener(this);
        mWrapperUser.addView(l);

        /** APP **/
        l = (RelativeLayout) inflater.inflate(R.layout.item_settings_switch, null, false);
        ((IconTextView) l.findViewById(R.id.titleRow)).setText("화면 잠금");
        Switch mSwitch = l.findViewById(R.id.switchRow);
        mSwitch.setChecked(false);
        mSwitch.setOnCheckedChangeListener(this);
        mSwitch.setTag(EXE_SWITCH);
        l.setOnClickListener(v -> mSwitch.performClick());
        mWrapperApp.addView(l);
    }

    @Override
    public void onClick(View view) {
        Context context = getContext();
        switch ((int) view.getTag()) {
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
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch ((int)buttonView.getTag()) {
            case EXE_SWITCH:
                break;
        }
    }
}
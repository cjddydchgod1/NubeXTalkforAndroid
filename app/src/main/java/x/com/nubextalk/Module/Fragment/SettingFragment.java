/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import io.realm.Realm;
import x.com.nubextalk.LoginActivity;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.R;

public class SettingFragment extends Fragment {
    private ViewGroup rootview;
    private Realm realm;
    private Button logout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        realm           = Realm.getInstance(UtilityManager.getRealmConfig());
        rootview        = (ViewGroup) inflater.inflate(R.layout.fragment_setting, container, false);
        logout = rootview.findViewById(R.id.logoutButton);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realm.executeTransaction(realm1 -> {
                    Config config = Config.getMyAccount(realm1);
                    config.setExt5(null);
                    realm1.copyToRealmOrUpdate(config);
                });
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return rootview;
    }
}
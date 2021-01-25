/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.aquery.AQuery;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.Module.Adapter.FriendListAdapter;
import x.com.nubextalk.Module.Case.FriendlistCase;
import x.com.nubextalk.R;

public class PACSFriendListFragment extends Fragment implements FriendListAdapter.onItemSelectedListener   {
    private ViewGroup rootview;
    private Realm realm;
    private ArrayList<User> mUserList;
    private FriendListAdapter mAdapter;
    private AQuery aq;
    private RecyclerView mRecyclerView;

    private String lastChecked;
    private RadioButton lastRadioButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview        = (ViewGroup) inflater.inflate(R.layout.fragment_pacs_friend_list, container, false);
        realm           = Realm.getInstance(UtilityManager.getRealmConfig());
        mRecyclerView   = rootview.findViewById(R.id.friend_list_PACS_recycleview);
        aq              = new AQuery(getActivity());
        mUserList       = new ArrayList<>();

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        getData();

        mAdapter = new FriendListAdapter(getActivity() ,mUserList, aq, FriendlistCase.RADIO);
        mAdapter.setOnItemSelectedListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        return rootview;
    }

    @Override
    public void onSelected(User address, RadioButton radioButton) {
        if(!address.getUserId().equals(lastChecked) && lastRadioButton != null) {
            lastRadioButton.setChecked(false);
        }
        radioButton.setChecked(true);
        lastRadioButton = radioButton;
        lastChecked = address.getUserId();
        Toast.makeText(getActivity(), address.getAppNickName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSelected(User address) {

    }

    public void getData() {
        try {
            RealmResults<User> mResults = realm.where(User.class).notEqualTo("userId", Config.getMyUID(realm)).findAll();
            mUserList.addAll(realm.copyFromRealm(mResults));
        } catch (Exception e) {
            Log.e("e", e.toString());
        } finally {
            if(realm != null)
                realm.close();
        }
    }
}
/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.moonmonkeylabs.realmsearchview.RealmSearchView;
import io.realm.Realm;
import x.com.nubextalk.Module.Adapter.ChatAddSearchAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatAddFragment extends Fragment {
    private Realm realm;
    private RealmSearchView realmSearchView;
    private ChatAddSearchAdapter mAdapter;

    // TODO: Rename and change types and number of parameters
    public static ChatAddFragment newInstance() {
        ChatAddFragment fragment = new ChatAddFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_chat_add, container, false);
        realm = Realm.getDefaultInstance();
        realmSearchView = rootView.findViewById(R.id.chat_add_member_search_view);
        mAdapter = new ChatAddSearchAdapter(getActivity(), realm, "name");
        realmSearchView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }
}
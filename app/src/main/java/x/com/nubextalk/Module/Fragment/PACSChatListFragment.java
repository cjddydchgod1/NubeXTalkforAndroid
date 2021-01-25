/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.aquery.AQuery;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.Module.Adapter.ChatListAdapter;
import x.com.nubextalk.Module.Adapter.FriendListAdapter;
import x.com.nubextalk.Module.Case.ChatlistCase;
import x.com.nubextalk.R;

public class PACSChatListFragment extends Fragment implements ChatListAdapter.OnItemSelectedListener  {
    private ViewGroup rootview;
    private Realm realm;
    private RealmResults<ChatRoom> chatRoomResults;

    private RecyclerView mRecyclerView;
    private ChatListAdapter mAdapter;

    private String lastChecked;
    private RadioButton lastRadioButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview        = (ViewGroup) inflater.inflate(R.layout.fragment_pacs_chat_list, container, false);
        realm           = Realm.getInstance(UtilityManager.getRealmConfig());
        mRecyclerView   = rootview.findViewById(R.id.chat_list_PACS_recyclerview);
        chatRoomResults = ChatRoom.getAll(realm);

        mAdapter = new ChatListAdapter(getActivity(), chatRoomResults, ChatlistCase.RADIO);
        mAdapter.setItemSelectedListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        realm.addChangeListener(new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm realm) {
                refreshChatList();
            }
        });

        return rootview;
    }

    @Override
    public void onItemSelected(ChatRoom chatRoom) {
    }

    @Override
    public void onItemSelected(ChatRoom chatRoom, RadioButton radioButton) {
        if(!chatRoom.getRid().equals(lastChecked) && lastRadioButton != null) {
            lastRadioButton.setChecked(false);
        }
        radioButton.setChecked(true);
        lastRadioButton = radioButton;
        lastChecked = chatRoom.getRid();
        Toast.makeText(getActivity(), chatRoom.getRoomName(), Toast.LENGTH_SHORT).show();
    }

    public void refreshChatList() {
        mAdapter.notifyDataSetChanged();
        mAdapter.sortChatRoomByDate();
    }
}
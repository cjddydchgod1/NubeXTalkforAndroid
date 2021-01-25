/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.aquery.AQuery;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import x.com.nubextalk.ChatRoomActivity;
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
    private Button confirmBtn;
    private RecyclerView mRecyclerView;
    private ChatListAdapter mAdapter;

    private ChatRoom lastChecked;
    private RadioButton lastRadioButton;

    private String studyId;
    private String description;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview        = (ViewGroup) inflater.inflate(R.layout.fragment_pacs_chat_list, container, false);
        realm           = Realm.getInstance(UtilityManager.getRealmConfig());
        mRecyclerView   = rootview.findViewById(R.id.chat_list_PACS_recyclerview);
        confirmBtn      = rootview.findViewById(R.id.btn_confirm_PACS);
        chatRoomResults = ChatRoom.getAll(realm);

        mAdapter = new ChatListAdapter(getActivity(), chatRoomResults, ChatlistCase.RADIO);
        mAdapter.setItemSelectedListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        /**
         * Bundle (studyId, Description) 받아오기
         */
        Bundle bundle = getArguments();
        studyId     = bundle.getString("studyId");
        description = bundle.getString("description");

        confirmBtn.setOnClickListener(view -> {
            if(lastChecked != null){
                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                intent.putExtra("rid", lastChecked.getRid());
                intent.putExtra("studyId", studyId);
                intent.putExtra("description", description);
                startActivity(intent);
                /**
                 * Finish Activity (ImageViewActivity, SharePACSActivity)
                 * 작성
                 */
            } else {
                Toast.makeText(getActivity(), "선택된 채팅 목록이 없습니다.", Toast.LENGTH_SHORT).show();
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
        lastChecked = chatRoom;
    }
}
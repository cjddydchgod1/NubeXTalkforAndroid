/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
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
import static x.com.nubextalk.Module.CodeResources.NON_RADIO;
import static x.com.nubextalk.Module.CodeResources.RADIO;
import x.com.nubextalk.R;

public class PACSChatListFragment extends Fragment implements ChatListAdapter.OnItemSelectedListener  {
    private ViewGroup rootview;
    private Realm realm;
    private RealmResults<ChatRoom> chatRoomResults;
    private Button confirmBtn;
    private RecyclerView mRecyclerView;
    private ChatListAdapter mAdapter;

    private ChatRoom lastChecked;

    private String studyId;
    private String description;

    private Context mContext;
    private Activity mActivity;

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
        rootview        = (ViewGroup) inflater.inflate(R.layout.fragment_pacs_chat_list, container, false);
        realm           = Realm.getInstance(UtilityManager.getRealmConfig());
        mRecyclerView   = rootview.findViewById(R.id.chat_list_PACS_recyclerview);
        confirmBtn      = rootview.findViewById(R.id.btn_confirm_PACS);

        /**
         * Chatlist data를 받아온다.
         */
        getData();

        mAdapter = new ChatListAdapter(mActivity, chatRoomResults, RADIO);
        mAdapter.setItemSelectedListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));

        /**
         * Bundle (studyId, Description) 받아오기
         */
        Bundle bundle = getArguments();
        studyId     = bundle.getString("studyId");
        description = bundle.getString("description");

        confirmBtn.setOnClickListener(view -> {
            if(lastChecked != null){
                Intent intent = new Intent(mActivity, ChatRoomActivity.class);
                intent.putExtra("rid", lastChecked.getRid());
                intent.putExtra("studyId", studyId);
                intent.putExtra("description", description);
                startActivity(intent);
            } else {
                Toast.makeText(mActivity, "선택된 채팅 목록이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        return rootview;
    }

    @Override
    public void onDetach() {
        mContext = null;
        mActivity = null;
        super.onDetach();
    }

    @Override
    public void onItemSelected(ChatRoom chatRoom) {
        lastChecked = chatRoom;
    }


    public void getData() {
        try {
            chatRoomResults = ChatRoom.getAll(realm);
        } catch (Exception e) {
            Log.e("e", e.toString());
        } finally {
            if(realm != null)
                realm.close();
        }
    }
}
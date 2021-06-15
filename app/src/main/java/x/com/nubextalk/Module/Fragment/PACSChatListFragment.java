/*
 * Created By JONGHO, LEE, Researcher of Institute of Biomedical Engineering, TechHeim Co.,Ltd.
 * on  2021.
 * This software is not stable health-care system and not certificated any health-care authority(like CE, FDA)
 * Prohibit redistribution, modification, commercial use.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmResults;
import x.com.nubextalk.ChatRoomActivity;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Module.Adapter.ChatListAdapter;
import x.com.nubextalk.R;

import static x.com.nubextalk.Module.CodeResources.MSG_EMPTY_CHAT_LIST;
import static x.com.nubextalk.Module.CodeResources.TITLE_CHAT_LIST;

public class PACSChatListFragment extends Fragment implements ChatListAdapter.OnItemSelectedListener {
    private ViewGroup mRootview;
    private Realm mRealm;
    private RealmResults<ChatRoom> mChatRoomList;
    private RecyclerView mRecyclerView;
    private ChatListAdapter mAdapter;

    private ChatRoom mLastChecked;

    private String mStudyId;
    private String mDescription;

    private Context mContext;
    private Activity mActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        if (context instanceof Activity)
            mActivity = (Activity) context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity.setTitle(TITLE_CHAT_LIST);
        mRootview = (ViewGroup) inflater.inflate(R.layout.fragment_pacs_chat_list, container, false);
        mRealm = Realm.getInstance(UtilityManager.getRealmConfig());
        mRecyclerView = mRootview.findViewById(R.id.chat_list_PACS_recyclerview);
        Button confirmBtn = mRootview.findViewById(R.id.btn_confirm_PACS);

        /**
         * Chatlist data를 받아온다.
         */
        getData();

        mAdapter = new ChatListAdapter(mActivity, mChatRoomList, true);
        mAdapter.setItemSelectedListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));

        /**
         * Bundle (studyId, Description) 받아오기
         */
        Bundle bundle = getArguments();
        mStudyId = bundle.getString("studyId");
        mDescription = bundle.getString("description");

        confirmBtn.setOnClickListener(view -> {
            if (mLastChecked != null) {
                Intent intent = new Intent(mActivity, ChatRoomActivity.class);
                intent.putExtra("rid", mLastChecked.getRid());
                intent.putExtra("studyId", mStudyId);
                intent.putExtra("description", mDescription);
                startActivity(intent);
            } else {
                Toast.makeText(mActivity, MSG_EMPTY_CHAT_LIST, Toast.LENGTH_SHORT).show();
            }
        });
        return mRootview;
    }

    @Override
    public void onDetach() {
        mContext = null;
        mActivity = null;
        super.onDetach();
    }

    @Override
    public void onItemSelected(ChatRoom chatRoom) {
        mLastChecked = chatRoom;
    }


    public void getData() {
        try {
            mChatRoomList = ChatRoom.getAll(mRealm);
        } catch (Exception e) {
            Log.e("e", e.toString());
        } finally {
            if (mRealm != null)
                mRealm.close();
        }
    }
}
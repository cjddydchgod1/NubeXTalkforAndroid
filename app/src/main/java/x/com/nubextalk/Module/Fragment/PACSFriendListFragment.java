/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Fragment;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.aquery.AQuery;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import x.com.nubextalk.ChatAddActivity;
import x.com.nubextalk.ChatRoomActivity;
import x.com.nubextalk.MainActivity;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatRoom;
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
    private Button confirmBtn;

    private User lastChecked;
    private RadioButton lastRadioButton;

    private String studyId;
    private String description;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview        = (ViewGroup) inflater.inflate(R.layout.fragment_pacs_friend_list, container, false);
        realm           = Realm.getInstance(UtilityManager.getRealmConfig());
        mRecyclerView   = rootview.findViewById(R.id.friend_list_PACS_recycleview);
        confirmBtn      = rootview.findViewById(R.id.btn_confirm_PACS);
        aq              = new AQuery(getActivity());
        mUserList       = new ArrayList<>();

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        /**
         * User data를 받아온다
         */
        getData();

        mAdapter = new FriendListAdapter(getActivity() ,mUserList, aq, FriendlistCase.RADIO);
        mAdapter.setOnItemSelectedListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        /**
         * Bundle (studyId, Description) 받아오기
         */
        Bundle bundle = getArguments();
        studyId = bundle.getString("studyId");
        description = bundle.getString("description");
        confirmBtn.setOnClickListener(view -> {
            if(lastChecked != null) {
                ChatRoom chatRoom = User.getChatroom(realm, lastChecked);
                if(chatRoom==null){
                    // 새로만든 채팅이 없다면 새로 만든다.
                    ArrayList<User> list = new ArrayList<>();
                    list.add(lastChecked);

                    Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                    new ChatAddActivity().createNewChat(realm, getContext(), list, "", new ChatAddActivity.onNewChatCreatedListener() {
                        @Override
                        public void onCreate(String rid) {
                            intent.putExtra("rid", rid);
                            intent.putExtra("studyId", studyId);
                            intent.putExtra("description", description);
                            startActivity(intent);
                        }
                    });
                } else {
                    Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                    intent.putExtra("rid", chatRoom.getRid());
                    intent.putExtra("studyId", studyId);
                    intent.putExtra("description", description);
                    startActivity(intent);
                    /**
                     * Finish Activity (ImageViewActivity, SharePACSActivity)
                     * 작성
                     */
                }
                getActivity().finish();
            } else {
                Toast.makeText(getActivity(), "선택된 친구 목록이 없습니다.", Toast.LENGTH_SHORT).show();
            }

        });

        return rootview;
    }

    @Override
    public void onSelected(User address, RadioButton radioButton) {
        if(!address.equals(lastChecked) && lastRadioButton != null) {
            lastRadioButton.setChecked(false);
        }
        radioButton.setChecked(true);
        lastRadioButton = radioButton;
        lastChecked = address;

    }

    @Override
    public void onSelected(User address) {

    }

    public void getData() {
        try {
            mUserList.addAll(realm.copyFromRealm(User.getUserlist(realm)));
        } catch (Exception e) {
            Log.e("e", e.toString());
        } finally {
            if(realm != null)
                realm.close();
        }
    }
}
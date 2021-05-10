/*
 * Created By Jong Ho, Lee on  2021.
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

import com.aquery.AQuery;

import java.util.ArrayList;

import io.realm.Realm;
import x.com.nubextalk.ChatAddActivity;
import x.com.nubextalk.ChatRoomActivity;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.Module.Adapter.FriendListAdapter;
import x.com.nubextalk.R;

import static x.com.nubextalk.Module.CodeResources.MSG_EMPTY_FRIEND_LIST;
import static x.com.nubextalk.Module.CodeResources.TITLE_FRIEND_LIST;

public class PACSFriendListFragment extends Fragment implements FriendListAdapter.onItemSelectedListener {
    private ViewGroup mRootview;
    private Realm mRealm;
    private ArrayList<User> mUserList;
    private FriendListAdapter mAdapter;
    private AQuery mAquery;
    private RecyclerView mRecyclerView;

    private User mLastChecked;

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
        mActivity.setTitle(TITLE_FRIEND_LIST);
        mRootview = (ViewGroup) inflater.inflate(R.layout.fragment_pacs_friend_list, container, false);
        mRealm = Realm.getInstance(UtilityManager.getRealmConfig());
        mRecyclerView = mRootview.findViewById(R.id.friend_list_PACS_recycleview);
        mAquery = new AQuery(mActivity);
        mUserList = new ArrayList<>();
        Button confirmBtn = mRootview.findViewById(R.id.btn_confirm_PACS);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        /**
         * User data를 받아온다
         */
        getData();

        mAdapter = new FriendListAdapter(mActivity, mUserList, mAquery, true);
        mAdapter.setOnItemSelectedListener(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        /**
         * Bundle (studyId, Description) 받아오기
         */
        Bundle bundle = getArguments();
        mStudyId = bundle.getString("studyId");
        mDescription = bundle.getString("description");
        confirmBtn.setOnClickListener(view -> {
            if (mLastChecked != null) {
                User.getChatroom(mRealm, mLastChecked, new User.UserListener() {
                    @Override
                    public void onFindPersonalChatRoom(ChatRoom chatRoom) {
                        if (chatRoom == null) {
                            // 새로만든 채팅이 없다면 새로 만든다.
                            ArrayList<User> list = new ArrayList<>();
                            list.add(mLastChecked);

                            Intent intent = new Intent(mActivity, ChatRoomActivity.class);
                            new ChatAddActivity().createNewChat(mRealm, mContext, list, "", new ChatAddActivity.onNewChatCreatedListener() {
                                @Override
                                public void onCreate(String rid) {
                                    intent.putExtra("rid", rid);
                                    intent.putExtra("studyId", mStudyId);
                                    intent.putExtra("description", mDescription);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Intent intent = new Intent(mActivity, ChatRoomActivity.class);
                            intent.putExtra("rid", chatRoom.getRid());
                            intent.putExtra("studyId", mStudyId);
                            intent.putExtra("description", mDescription);
                            startActivity(intent);
                        }
                    }
                });
            } else {
                Toast.makeText(mActivity, MSG_EMPTY_FRIEND_LIST, Toast.LENGTH_SHORT).show();
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
    public void onSelected(User address) {
        mLastChecked = address;
    }

    public void getData() {
        try {
            mUserList.addAll(mRealm.copyFromRealm(User.getUserlist(mRealm)));
        } catch (Exception e) {
            Log.e("e", e.toString());
        } finally {
            if (mRealm != null)
                mRealm.close();
        }
    }
}
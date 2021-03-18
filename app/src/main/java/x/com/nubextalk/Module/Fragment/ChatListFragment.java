/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import x.com.nubextalk.ChatAddActivity;
import x.com.nubextalk.ChatRoomActivity;
import x.com.nubextalk.Manager.FireBase.FirebaseFunctionsManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.Module.Adapter.ChatListAdapter;
import x.com.nubextalk.R;

import static x.com.nubextalk.Module.CodeResources.ADD_MEMBER;
import static x.com.nubextalk.Module.CodeResources.ALARM;
import static x.com.nubextalk.Module.CodeResources.ALARM_OFF;
import static x.com.nubextalk.Module.CodeResources.ALARM_ON;
import static x.com.nubextalk.Module.CodeResources.EXIT;
import static x.com.nubextalk.Module.CodeResources.FIX_TOP;
import static x.com.nubextalk.Module.CodeResources.HOSPITAL_ID;
import static x.com.nubextalk.Module.CodeResources.RENAME_TITLE;
import static x.com.nubextalk.Module.CodeResources.TITLE_CHAT_LIST;
import static x.com.nubextalk.Module.CodeResources.TITLE_CHAT_SETTING;

public class ChatListFragment extends Fragment implements ChatListAdapter.OnItemLongSelectedListener,
        ChatListAdapter.OnItemSelectedListener {
    private Realm mRealm;
    private RealmResults<ChatRoom> mChatRoomList;
    private Context mContext;
    private Activity mActivity;

    private String mHid;

    private RecyclerView mRecyclerView;
    private ChatListAdapter mAdapter;

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
        ViewGroup rootView  = (ViewGroup) inflater.inflate(R.layout.fragment_chat_list, container, false);
        mActivity.setTitle(TITLE_CHAT_LIST);
        mHid                = HOSPITAL_ID;
        mRealm              = Realm.getInstance(UtilityManager.getRealmConfig());
        mRecyclerView       = rootView.findViewById(R.id.fragment_chat_list_view);
        mChatRoomList       = ChatRoom.getAll(mRealm);
        FloatingActionButton fab_sub1 = rootView.findViewById(R.id.chat_fab_sub1);

        mAdapter = new ChatListAdapter(getActivity(), mChatRoomList, false);
        mAdapter.setItemLongSelectedListener(this);
        mAdapter.setItemSelectedListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mRealm.addChangeListener(new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm realm) {
                refreshChatList();
            }
        });


        fab_sub1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ChatAddActivity.class).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mRealm != null) {
            mRealm.removeAllChangeListeners();
            mRealm.close();
            mRealm = null;
        }
    }

    /**
     * 채팅방 목록 중 아이템 하나 꾹 눌렀을 떄 이벤트
     */
    @Override
    public void onItemLongSelected(ChatRoom chatRoom) {
        boolean fixTop = chatRoom.getSettingFixTop();
        boolean alarm = chatRoom.getSettingAlarm();

        String[] menuArray = new String[]{ALARM, ADD_MEMBER, FIX_TOP, RENAME_TITLE, EXIT};
        menuArray[0] = alarm ? menuArray[0].concat(ALARM_OFF) : menuArray[0].concat(ALARM_ON);
        menuArray[2] = fixTop ? menuArray[2].concat(ALARM_OFF) : FIX_TOP;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(TITLE_CHAT_SETTING)
                .setItems(menuArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int pos) {
                        switch (pos) {
                            case 0: /**채팅방 알림 설정 이벤트**/
                                updateChatRoomAlarm(chatRoom);
                                break;
                            case 1: /**대화상대 추가 이벤트**/
                                startActivity(new Intent(mContext, ChatAddActivity.class)
                                        .putExtra("rid", chatRoom.getRid()));
                                break;
                            case 2: /**채팅방 상단 고정 이벤트**/
                                updateChatRoomFixTop(chatRoom);
                                break;
                            case 3: /**채팅방 이름 편집 이벤트**/
                                openDialog(chatRoom.getRid());
                                break;
                            case 4: /**채팅방 나가기 이벤트**/
                                exitChatRoom(chatRoom);
                                break;
                        }
                    }
                }).create().show();
    }

    /**
     * 채팅 목록에서 중 하나 누르면 채팅 activity 로 전환
     **/
    @Override
    public void onItemSelected(@NonNull ChatRoom chatRoom) {
        Intent intent = new Intent(mActivity, ChatRoomActivity.class);
        intent.putExtra("rid", chatRoom.getRid());
        startActivity(intent);
    }

    private void openDialog(String chatRoomId) {
        DialogFragment dialogFragment = new RoomNameModificationDialogFragment(mRealm, chatRoomId);
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(getParentFragmentManager(), "Change RoomName");
    }

    public void exitChatRoom(ChatRoom chatRoom) {
        if (chatRoom.getIsGroupChat()) {
            User me = (User) User.getMyAccountInfo(mRealm);
            ChatRoom.deleteChatRoom(mRealm, chatRoom.getRid());
            FirebaseFunctionsManager.exitChatRoom(mHid, me.getUid(), chatRoom.getRid());
        } else {
            ChatRoom.deleteChatRoom(mRealm, chatRoom.getRid());
        }
    }

    /**
     * 채팅방 목록 상단고정 realm 설정 및 해제
     *
     * @param chatRoom
     */
    public void updateChatRoomFixTop(ChatRoom chatRoom) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                boolean fixTop = chatRoom.getSettingFixTop();
                chatRoom.setSettingFixTop(!fixTop);
                realm.copyToRealmOrUpdate(chatRoom);
            }
        });
    }

    /**
     * 채팅방 목록 알림 realm 설정 및 해제
     *
     * @param chatRoom
     */
    public void updateChatRoomAlarm(ChatRoom chatRoom) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                boolean alarm = chatRoom.getSettingAlarm();
                chatRoom.setSettingAlarm(!alarm);
                realm.copyToRealmOrUpdate(chatRoom);
            }
        });
    }

    /**
     * 채팅목록 리싸이클러뷰 데이터 재설정
     */
    public void refreshChatList() {
        mAdapter.notifyDataSetChanged();
        mAdapter.sortChatRoomByDate();
    }
}
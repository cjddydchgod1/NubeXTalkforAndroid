/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import javax.annotation.Nullable;

import io.realm.ObjectChangeSet;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmObjectChangeListener;
import io.realm.RealmResults;
import x.com.nubextalk.AddChatMemberActivity;
import x.com.nubextalk.ChatAddActivity;
import x.com.nubextalk.ChatRoomActivity;
import x.com.nubextalk.MainActivity;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.ChatRoomMember;
import x.com.nubextalk.Module.Adapter.ChatListAdapter;
import x.com.nubextalk.Module.Case.ChatlistCase;
import x.com.nubextalk.R;

public class ChatListFragment extends Fragment implements ChatListAdapter.OnItemLongSelectedListener,
        ChatListAdapter.OnItemSelectedListener, View.OnClickListener {
    private Realm realm;
    private RealmResults<ChatRoom> chatRoomResults;

    private RecyclerView mRecyclerView;
    private ChatListAdapter mAdapter;

    private FloatingActionButton fab_main, fab_sub1, fab_sub2;
    private boolean isFabOpen = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_chat_list, container, false);
        realm = Realm.getInstance(UtilityManager.getRealmConfig());
        mRecyclerView = rootView.findViewById(R.id.fragment_chat_list_view);
        chatRoomResults = ChatRoom.getAll(realm);

        mAdapter = new ChatListAdapter(getActivity(), chatRoomResults, ChatlistCase.NON_RADIO);
        mAdapter.setItemLongSelectedListener(this);
        mAdapter.setItemSelectedListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        realm.addChangeListener(new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm realm) {
                refreshChatList();
            }
        });

        fab_main = rootView.findViewById(R.id.chat_fab_main);
        fab_sub1 = rootView.findViewById(R.id.chat_fab_sub1);
        fab_sub2 = rootView.findViewById(R.id.chat_fab_sub2);
        fab_main.setOnClickListener(this);
        fab_sub1.setOnClickListener(this);
        fab_sub2.setOnClickListener(this);

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

    /**
     * 채팅방 목록 중 아이템 하나 꾹 눌렀을 떄 이벤트
     */
    @Override
    public void onItemLongSelected(ChatRoom chatRoom) {
        boolean fixTop = chatRoom.getSettingFixTop();
        boolean alarm = chatRoom.getSettingAlarm();

        String[] menuArray = new String[]{"알림", "대화상대 추가", "상단 고정", "나가기"};
        menuArray[0] = alarm ? menuArray[0].concat(" 해제") : menuArray[0].concat(" 켜기");
        menuArray[2] = fixTop ? menuArray[2].concat(" 해제") : "상단 고정";

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("채팅방 설정")
                .setItems(menuArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int pos) {
                        switch (pos) {
                            case 0: /**채팅방 알림 설정 이벤트**/
                                updateChatRoomAlarm(chatRoom);
                                refreshChatList();
                                break;
                            case 1: /**대화상대 추가 이벤트**/
                                startActivity(new Intent(getContext(), AddChatMemberActivity.class)
                                        .putExtra("rid", chatRoom.getRid()));
                                break;
                            case 2: /**채팅방 상단 고정 이벤트**/
                                updateChatRoomFixTop(chatRoom);
                                refreshChatList();
                                break;
                            case 3: /**채팅방 나가기 이벤트**/
                                ChatRoom.deleteChatRoom(realm, chatRoom.getRid());
                                refreshChatList();
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
        Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
        intent.putExtra("rid", chatRoom.getRid());
//        ((MainActivity) getActivity()).startChatRoomActivity(intent);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(ChatRoom chatRoom, RadioButton radioButton) {
    }

    @Override
    public void onClick(@NonNull View v) {
        switch (v.getId()) {
            case R.id.chat_fab_main:
                toggleFab();
                break;

            case R.id.chat_fab_sub1:
                toggleFab();
//                ((MainActivity) getActivity()).startChatAddActivity();
                startActivity(new Intent(getContext(), ChatAddActivity.class));
                break;

            case R.id.chat_fab_sub2:
                toggleFab();
                break;
        }
    }

    /**
     * 채팅방 목록 상단고정 realm 설정 및 해제
     * @param chatRoom
     */
    public void updateChatRoomFixTop(ChatRoom chatRoom) {
        realm.executeTransaction(new Realm.Transaction() {
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
     * @param chatRoom
     */
    public void updateChatRoomAlarm(ChatRoom chatRoom) {
        realm.executeTransaction(new Realm.Transaction() {
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

    private void toggleFab() {
        if (isFabOpen) {
            fab_main.setImageResource(R.drawable.ic_floating_btn_24);
            fab_sub1.hide();
            fab_sub2.hide();
            isFabOpen = false;
        } else {
            fab_main.setImageResource(R.drawable.ic_baseline_close_24);
            fab_sub1.show();
            fab_sub2.show();
            isFabOpen = true;
        }
    }
}
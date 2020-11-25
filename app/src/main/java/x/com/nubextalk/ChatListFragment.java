/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.realm.Realm;
import io.realm.RealmResults;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.ChatRoomMember;
import x.com.nubextalk.Module.Adapter.ChatListAdapter;

public class ChatListFragment extends Fragment implements ChatListAdapter.OnItemLongSelectedListener,
        ChatListAdapter.OnItemSelectedListener, View.OnClickListener {
    private Realm realm;
    private RealmResults<ChatRoom> chatRoomResults;
    private RealmResults<ChatContent> chatContentResults;

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
        chatContentResults = ChatContent.getAll(realm);
        if (chatContentResults.size() == 0) ChatContent.init(getContext(), realm);
        if (chatRoomResults.size() == 0) ChatRoom.init(getContext(), realm);

        mAdapter = new ChatListAdapter(getActivity(), chatRoomResults);
        mAdapter.setItemLongSelectedListener(this::onItemLongSelected);
        mAdapter.setItemSelectedListener(this::onItemSelected);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        fab_main = rootView.findViewById(R.id.chat_fab_main);
        fab_sub1 = rootView.findViewById(R.id.chat_fab_sub1);
        fab_sub2 = rootView.findViewById(R.id.chat_fab_sub2);
        fab_main.setOnClickListener(this::onClick);
        fab_sub1.setOnClickListener(this::onClick);
        fab_sub2.setOnClickListener(this::onClick);

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
     * 채팅 목록에서 채팅 꾹 눌렀을 떄 이벤트
     */
    @Override
    public void onItemLongSelected(ChatRoom chatRoom) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("채팅방 설정")
                .setItems(R.array.menu_chat_long_click, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int pos) {
                        String[] items = getResources().getStringArray(R.array.menu_chat_long_click);
                        switch (pos) {
                            case 0: /**채팅방 알림 설정 이벤트 구현**/
                                break;
                            case 1: /**대화상대 추가 이벤트 구현**/
                                break;
                            case 2: /**채팅방 상단 고정 이벤트 구현**/
                                break;
                            case 3: /**채팅방 나가기 이벤트 구현**/

                                exitChatRoom(chatRoom);
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
        ((MainActivity) getActivity()).startChatRoomActivity(intent);
    }

    @Override
    public void onClick(@NonNull View v) {
        switch (v.getId()) {
            case R.id.chat_fab_main:
                toggleFab();
                break;

            case R.id.chat_fab_sub1:
                toggleFab();
                ((MainActivity) getActivity()).startChatAddActivity();
                break;

            case R.id.chat_fab_sub2:
                toggleFab();
                break;
        }
    }

    public void exitChatRoom(ChatRoom chatRoom) {
        //1. 눌린 채팅방 rid 통해 uid 받아오기
        String rid = chatRoom.getRid();

        //2. 채팅방 rid 통해 ChatRoomMember 에 rid 해당하는 row 삭제
        RealmResults<ChatRoomMember> chatRoomMembers = realm.where(ChatRoomMember.class).equalTo("rid", rid).findAll();
        for (ChatRoomMember member : chatRoomMembers) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    member.deleteFromRealm();
                }
            });
        }
        //3. 채팅방 rid 통해 ChatContent 에 rid 해당하는 row 삭제
        RealmResults<ChatContent> chatContents = realm.where(ChatContent.class).equalTo("rid", rid).findAll();
        for (ChatContent content : chatContents) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    content.deleteFromRealm();
                }
            });
        }
        //4. 채팅방 rid 통해 ChatRoom 삭제
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ChatRoom chatRoom = realm.where(ChatRoom.class).equalTo("rid", rid).findFirst();
                chatRoom.deleteFromRealm();
            }
        });
    }

    public void refreshChatList() {
        mAdapter.notifyDataSetChanged();
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
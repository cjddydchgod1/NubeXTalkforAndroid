/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Module.Adapter.ChatListAdapter;

public class ChatListFragment extends Fragment implements ChatListAdapter.OnItemLongSelectedListner {
    private Realm realm;
    private RealmResults<ChatRoom> chatRoomResults;
    private RealmResults<ChatContent> chatContentResults;

    private RecyclerView mRecyclerView;
    private ChatListAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_chat_list, container, false);
        realm = Realm.getDefaultInstance();
        mRecyclerView = rootView.findViewById(R.id.fragment_chat_list_view);
        chatRoomResults = ChatRoom.getAll(realm);
        chatContentResults = ChatContent.getAll(realm);
        if (chatContentResults.size() == 0) ChatContent.init(getContext(), realm);
        if (chatRoomResults.size() == 0) ChatRoom.init(getContext(), realm);

        mAdapter = new ChatListAdapter(getActivity(), chatRoomResults);
        mAdapter.setItemLongSelectedListner(this::onItemLongSelected);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

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
                        Toast.makeText(getActivity(), items[pos], Toast.LENGTH_SHORT).show();
                    }
                }).create().show();

    }
}
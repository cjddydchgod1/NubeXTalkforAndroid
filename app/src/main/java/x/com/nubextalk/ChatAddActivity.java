/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import co.moonmonkeylabs.realmsearchview.RealmSearchView;
import io.realm.Realm;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.Module.Adapter.ChatAddMemberAdapter;
import x.com.nubextalk.Module.Adapter.ChatAddSearchAdapter;

public class ChatAddActivity extends AppCompatActivity implements
        ChatAddSearchAdapter.OnItemSelectedListner, View.OnClickListener {
    private Realm realm;
    private RealmSearchView realmSearchView;
    private RecyclerView selectedMemberView;
    private ChatAddSearchAdapter mAdapter;
    private ChatAddMemberAdapter memberAdapter;
    private ArrayList<User> userList = new ArrayList<User>();

    private Button chatAddConfirmButton;
    private Button chatAddCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_add);

        chatAddConfirmButton = findViewById(R.id.chat_add_confirm_btn);
        chatAddCancelButton = findViewById(R.id.chat_add_cancel_btn);
        chatAddConfirmButton.setOnClickListener(this::onClick);
        chatAddCancelButton.setOnClickListener(this::onClick);


        realm = Realm.getDefaultInstance();
        realmSearchView = findViewById(R.id.chat_add_member_search_view);
        mAdapter = new ChatAddSearchAdapter(this, realm, "name");
        mAdapter.setItemSelectedListener(this::onItemSelected);
        realmSearchView.setAdapter(mAdapter);

        selectedMemberView = findViewById(R.id.chat_added_member_view);
        memberAdapter = new ChatAddMemberAdapter(this, userList);
        selectedMemberView.
                setLayoutManager(new LinearLayoutManager(
                        this, LinearLayoutManager.HORIZONTAL, false));
        selectedMemberView.setAdapter(memberAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }

    /**
     * 사용자 아이템 클릭
     * **/
    @Override
    public void onItemSelected(User user) {
        String userName = user.getName();
        memberAdapter.addItem(user);
        memberAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_add_confirm_btn:

                break;

            case R.id.chat_add_cancel_btn:
                onBackPressed();
                break;
        }
    }
}
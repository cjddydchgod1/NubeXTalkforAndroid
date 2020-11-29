/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import co.moonmonkeylabs.realmsearchview.RealmSearchView;
import io.realm.Realm;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.ChatRoomMember;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.Module.Adapter.ChatAddMemberAdapter;
import x.com.nubextalk.Module.Adapter.ChatAddSearchAdapter;

public class ChatAddActivity extends AppCompatActivity implements
        ChatAddSearchAdapter.OnItemSelectedListner, View.OnClickListener {
    private Realm realm;
    private RealmSearchView realmSearchView;
    private RecyclerView selectedMemberView;
    private EditText chatRoomNameInput;
    private ChatAddSearchAdapter mAdapter;
    private ChatAddMemberAdapter memberAdapter;
    private ArrayList<User> userList = new ArrayList<User>();


    private Button chatAddConfirmButton;
    private Button chatAddCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_add);

        chatRoomNameInput = findViewById(R.id.chat_add_chat_room_input);
        chatAddConfirmButton = findViewById(R.id.chat_add_confirm_btn);
        chatAddCancelButton = findViewById(R.id.chat_add_cancel_btn);
        chatAddConfirmButton.setOnClickListener(this::onClick);
        chatAddCancelButton.setOnClickListener(this::onClick);


        realm = Realm.getInstance(UtilityManager.getRealmConfig());
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
     **/
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
                createNewChat();
                break;

            case R.id.chat_add_cancel_btn:
                onBackPressed();
                break;
        }
    }

    public boolean createNewChat() {
        String roomName = chatRoomNameInput.getText().toString();

        /**확인 버튼을 누르면 새로운 ChatRoom 생성하고 해당 rid 를 ChatRoomMember 에 rid, uid 넣어서 생성**/
        ArrayList<User> selectedUser = memberAdapter.getUserList();
        String rid = getRandomString().toString();
        ChatRoom newChatRoom = new ChatRoom();
        newChatRoom.setRid(rid);
        if (selectedUser.size() == 1) { /** 선택된 유저가 한명일 때 **/
            if (!roomName.isEmpty()) { // 채팅방 이름을 입력했을 때
                newChatRoom.setRoomName(roomName);
            } else { // 채팅방 이름 입력 안했을 때 = 상대방 이름으로 채팅방 이름 설정
                newChatRoom.setRoomName(selectedUser.get(0).getName());
            }
            newChatRoom.setRoomImg(selectedUser.get(0).getProfileImg());
        } else {
            if (!roomName.isEmpty()) {
                newChatRoom.setRoomName(roomName);
            } else {
                Toast.makeText(this, "채팅방 이름을 입력해주세요.", Toast.LENGTH_SHORT ).show();
                return false;
            }
            newChatRoom.setRoomImg("");
        }

        newChatRoom.setUpdatedDate(new Date());

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(newChatRoom);

                for (User user : selectedUser) {
                    ChatRoomMember chatMember = new ChatRoomMember();
                    chatMember.setRid(rid);
                    chatMember.setUid(user.getUid());
                    /**
                     * ChatRoomMember 모델이 Primary Key 가 없어서 copyToRealmOrUpdate 함수는
                     * 사용하지 못하기 때문에 copyToRealm 함수를 사용함.
                     * 참고: https://stackoverflow.com/questions/40999299/android-create-realm-table-without-primary-key
                     **/
                    realm.copyToRealm(chatMember);

                }
            }
        });

        setResult(RESULT_OK); //MainActivity 로 결과 전달
        finish();
        return true;
    }

    public StringBuffer getRandomString() {
        StringBuffer temp = new StringBuffer();
        Random rnd = new Random();
        rnd.setSeed(System.currentTimeMillis());
        for (int i = 0; i < 20; i++) {
            int rIndex = rnd.nextInt(3);
            switch (rIndex) {
                case 0:
                    // a-z
                    temp.append((char) ((int) (rnd.nextInt(26)) + 97));
                    break;
                case 1:
                    // A-Z
                    temp.append((char) ((int) (rnd.nextInt(26)) + 65));
                    break;
                case 2:
                    // 0-9
                    temp.append((rnd.nextInt(10)));
                    break;
            }
        }
        return temp;
    }
}
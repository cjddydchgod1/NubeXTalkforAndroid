/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import x.com.nubextalk.Manager.FireBase.FirebaseFunctionsManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.ChatRoomMember;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.Module.Adapter.ChatAddMemberAdapter;
import x.com.nubextalk.Module.Adapter.ChatAddSearchAdapter;

public class AddChatMemberActivity extends AppCompatActivity implements
        ChatAddSearchAdapter.OnItemSelectedListener, View.OnClickListener {
    private ArrayList<User> userList = new ArrayList<User>();
    private ArrayList<User> addedUserList = new ArrayList<User>();
    private Button chatAddConfirmButton;
    private Button chatAddCancelButton;
    private ChatAddMemberAdapter selectedMemberAdapter;
    private ChatAddSearchAdapter realmSearchAdapter;
    private EditText userNameInput;
    private InputMethodManager imm;
    private RecyclerView realmMemberSearchView;
    private RecyclerView selectedMemberView;
    private Realm realm;
    private String chatRoomId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat_member);

        Intent intent = getIntent();
        chatRoomId = intent.getExtras().getString("rid");

        realm = Realm.getInstance(UtilityManager.getRealmConfig());
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        userNameInput = findViewById(R.id.add_chat_member_input);
        userNameInput.setSingleLine(true);
        userNameInput.addTextChangedListener(textWatcher);
        chatAddConfirmButton = findViewById(R.id.add_chat_confirm_btn);
        chatAddCancelButton = findViewById(R.id.add_chat_cancel_btn);
        chatAddConfirmButton.setOnClickListener(this);
        chatAddCancelButton.setOnClickListener(this);

        initUserList(); //리싸이클러뷰에 사용될 사용자 데이터 리스트 초기화

        //사용자 검색 및 목록 리싸이클러뷰 설정
        realmMemberSearchView = findViewById(R.id.add_chat_member_search_view);
        realmSearchAdapter = new ChatAddSearchAdapter(this, userList);
        realmSearchAdapter.setItemSelectedListener(this);
        realmMemberSearchView.
                setLayoutManager(new LinearLayoutManager(
                        this, LinearLayoutManager.VERTICAL, false));
        realmMemberSearchView.setAdapter(realmSearchAdapter);

        //선택된 사용자 표시 리싸이클러뷰 설정
        selectedMemberView = findViewById(R.id.add_chat_member_added_view);
        selectedMemberAdapter = new ChatAddMemberAdapter(this, addedUserList);
        selectedMemberView.
                setLayoutManager(new LinearLayoutManager(
                        this, LinearLayoutManager.HORIZONTAL, false));
        selectedMemberView.setAdapter(selectedMemberAdapter);
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
     * 리싸이클러뷰에 표시될 사용자들 담을 데이터 초기화 함수 - 기존 채팅방에 있는 사용자는 보여주지 않는다
     */
    public void initUserList() {
        RealmResults<User> users = User.getUserlist(this.realm);
        RealmResults<ChatRoomMember> chatRoomMembers = ChatRoom.getChatRoomUsers(this.realm, chatRoomId);

        for (User user : users) {
            userList.add(user);
        }

        //기존 채팅방 사용자들이 있으면 userList 데이터에서 삭제
        for (ChatRoomMember chatRoomMember : chatRoomMembers) {
            Iterator<User> userIterator = userList.iterator();
            while (userIterator.hasNext()) {
                if (chatRoomMember.getUid().equals(userIterator.next().getUserId())) {
                    userIterator.remove();
                }
            }
        }
    }

    /**
     * 사용자 검색해서 해당 사용자가 있으면 보여주는 함수
     *
     * @param name 사용자 이름
     */
    private void searchUser(String name) {
        if (!this.realm.where(User.class).contains("appName", name).
                or().contains("appNickName", name).findAll().isEmpty()) {
            RealmResults<User> users = this.realm.where(User.class).contains("appName", name).
                    or().contains("appNickName", name).findAll();
            realmSearchAdapter.deleteAllItem();
            for (User user : users) {
                if(!isExistUser(realm, chatRoomId, user.getUserId())){
                    realmSearchAdapter.addItem(user);
                }
            }
        } else {
            realmSearchAdapter.deleteAllItem();
            initUserList();
            realmSearchAdapter.setItem(userList);
        }
    }

    /**
     * 해당 채팅방에 사용자가 있는지 확인하는 함수
     * @param realm
     * @param roomId
     * @param uid
     * @return
     */
    private boolean isExistUser(Realm realm, String roomId, String uid) {
        return !realm.where(ChatRoomMember.class).equalTo("rid", roomId).and()
                .equalTo("uid", uid).findAll().isEmpty();
    }

    /**
     * 사용자 아이템 클릭
     **/
    @Override
    public void onItemSelected(User user) {
        selectedMemberAdapter.addItem(user);

        userNameInput.setText("");
        imm.hideSoftInputFromWindow(realmMemberSearchView.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_chat_confirm_btn:
                if (addChatMember(chatRoomId)) {
                    Toast.makeText(this, "대화 상대가 추가되었습니다.", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(this, "대화 상대 추가가 실패되었습니다.", Toast.LENGTH_SHORT)
                            .show();
                }
                finish();
            case R.id.add_chat_cancel_btn:
                Log.d("ADD_CHAT_MEMBER", "back pressed!");
                onBackPressed();
        }
    }

    /**
     * 기존 대화방 ChatRoom 에서 선택된 사용자들 추가
     *
     * @param chatRoomId
     * @return
     */
    private boolean addChatMember(String chatRoomId) {
        String hospital = "w34qjptO0cYSJdAwScFQ";
        ArrayList<User> selectedUser = selectedMemberAdapter.getUserList();
        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        try {
            for (User user : selectedUser) {
                Map<String, Object> data = new HashMap<>();
                //FireStore 에 해당 채팅방에 사용자 추가
                fs.collection("hospital").document(hospital)
                        .collection("chatRoom").document(chatRoomId)
                        .collection("chatRoomMember")
                        .document(user.getCode()).set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // FireStore 채팅방에 사용자 추가 후 해당 사용자들에게 fcm 메세지로 알림. 근데 이건 필요 없는 것 같음...
                                Map<String, Object> data = new HashMap<>();
                                JSONArray jsonArray = new JSONArray();
                                jsonArray.put(user.getUserId());
                                data.put("hospitalId", hospital);
                                data.put("membersId", jsonArray);
                                data.put("chatRoomId", chatRoomId);
                                FirebaseFunctionsManager.notifyToChatRoomAddedUser(data);
                            }
                        });
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 사용자 이름 검색창에서 텍스트 입력 감지에 사용되는 TextWatcher
     */
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            searchUser(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
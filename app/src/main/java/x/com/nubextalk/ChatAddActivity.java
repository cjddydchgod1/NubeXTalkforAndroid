/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import android.content.Context;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.Module.Adapter.ChatAddMemberAdapter;
import x.com.nubextalk.Module.Adapter.ChatAddSearchAdapter;

public class ChatAddActivity extends AppCompatActivity implements
        ChatAddSearchAdapter.OnItemSelectedListener, View.OnClickListener {
    private ArrayList<User> userList = new ArrayList<User>();
    private ArrayList<User> addedUserList = new ArrayList<User>();
    private Button chatAddConfirmButton;
    private Button chatAddCancelButton;
    private ChatAddMemberAdapter selectedMemberAdapter;
    private ChatAddSearchAdapter realmSearchAdapter;
    private EditText chatRoomNameInput;
    private EditText userNameInput;
    private InputMethodManager imm;
    private RecyclerView realmMemberSearchView;
    private RecyclerView selectedMemberView;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_add);

        realm = Realm.getInstance(UtilityManager.getRealmConfig());
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        chatRoomNameInput = findViewById(R.id.chat_add_chat_room_input);
        chatRoomNameInput.setSingleLine(true);
        userNameInput = findViewById(R.id.chat_add_chat_user_input);
        userNameInput.setSingleLine(true);
        userNameInput.addTextChangedListener(textWatcher);
        chatAddConfirmButton = findViewById(R.id.chat_add_confirm_btn);
        chatAddConfirmButton.setOnClickListener(this);
        chatAddCancelButton = findViewById(R.id.chat_add_cancel_btn);
        chatAddCancelButton.setOnClickListener(this);

        initUserList(); //리싸이클러뷰에 사용될 사용자 데이터 리스트 초기화

        //사용자 검색 및 목록 리싸이클러뷰 설정
        realmMemberSearchView = findViewById(R.id.chat_add_member_search_view);
        realmSearchAdapter = new ChatAddSearchAdapter(this, userList);
        realmSearchAdapter.setItemSelectedListener(this);
        realmMemberSearchView.
                setLayoutManager(new LinearLayoutManager(
                        this, LinearLayoutManager.VERTICAL, false));
        realmMemberSearchView.setAdapter(realmSearchAdapter);

        //선택된 사용자 표시 리싸이클러뷰 설정
        selectedMemberView = findViewById(R.id.chat_added_member_view);
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
     * interface
     */
    public interface onNewChatCreatedListener {
        void onCreate(String rid);
    }

    /**
     * 리싸이클러뷰에 표시될 사용자들 담을 데이터 초기화 함수
     */
    public void initUserList() {
        RealmResults<User> users = User.getUserlist(this.realm);
        for (User user : users) {
            if (!userList.contains(user)) {
                userList.add(user);
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
                if (!user.getUserId().contentEquals(Config.getMyAccount(realm).getExt1())) {
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
     * 리싸이클러뷰에서 아이템 선택 시 수행되는 함수
     **/
    @Override
    public void onItemSelected(User user) {
        selectedMemberAdapter.addItem(user);

        //사용자 아이템을 클릭한 뒤 사용자 검색창 초기화 및 키보드 숨기기
        userNameInput.setText("");
        imm.hideSoftInputFromWindow(realmMemberSearchView.getWindowToken(), 0);
        realmSearchAdapter.deleteAllItem();
        initUserList();
        realmSearchAdapter.setItem(userList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_add_confirm_btn:
                ArrayList<User> selectedUser = selectedMemberAdapter.getUserList();
                String roomName = chatRoomNameInput.getText().toString();
                Log.d("CHATROOM", "roomName: " + roomName );

                //선택된 유저가 한명일 때
                if (selectedUser.size() == 1) {
                    ChatRoom chatRoom = User.getChatroom(realm, selectedUser.get(0));
                    //선택된 유저와의 채팅방이 없으면 새로운 채팅방 생성
                    if (chatRoom == null) {
                        Intent intent = new Intent(this, ChatRoomActivity.class);

                        // 1:1 채팅방 생성의 경우 서버에 기존 1:1 채팅방이 있는지 확인
                        // 존재하는 경우에는 해당 채팅방의 rid 를 서버로부터 받아와 로컬에 채팅방 만듦
                        // 존재하지 않는 경우에는 로컬에서 새로 만듦
                        createNewChat(this.realm, this, selectedUser, roomName, new onNewChatCreatedListener() {
                            @Override
                            public void onCreate(String rid) {
                                intent.putExtra("rid", rid);
                                startActivity(intent);
                            }
                        });
                    } else { //기존에 선택된 유저와 채팅방이 있으면 그 채팅방으로 이동
                        Intent intent = new Intent(this, ChatRoomActivity.class);
                        intent.putExtra("rid", chatRoom.getRid());
                        startActivity(intent);
                    }

                } else { //선택된 유저가 여러명일 때, 단톡방
                    if (roomName.isEmpty()) {
                        Toast.makeText(this, "단체 채팅방 이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(this, ChatRoomActivity.class);
                        createNewChat(this.realm, this, selectedUser, roomName, new onNewChatCreatedListener() {
                            @Override
                            public void onCreate(String rid) {
                                intent.putExtra("rid", rid);
                                startActivity(intent);
                            }
                        });
                    }
                }
                break;

            case R.id.chat_add_cancel_btn:
                onBackPressed();
                break;
        }
    }

    /**
     * 새로운 realm ChatRoom 생성 함수
     *
     * @param realm
     * @param list  사용자 User 리스트
     * @param name  채팅방 이름
     */
    public static void createNewChat(Realm realm, Context context, ArrayList<User> list, String name,
                                     onNewChatCreatedListener onNewChatCreatedListener) {
        String hospital = "w34qjptO0cYSJdAwScFQ";
        Map<String, Object> data = new HashMap<>();
        data.put("hospital", hospital);
        final String[] rid = {null};

        ArrayList<String> userIdList = new ArrayList<>();
        for (User user : list) {
            userIdList.add(user.getUserId());
        }

        if (list.size() == 1) { // 1:1 채팅인 경우
            if (!name.isEmpty()) { // 채팅방 이름을 입력했을 때
                data.put("title", name);
            } else { // 채팅방 이름 입력 안했을 때 = "" 빈 내용으로 입력
                data.put("title", list.get(0).getAppName());
            }
            data.put("roomImgUrl", list.get(0).getAppImagePath());
        } else { // 단톡방인 경우
            data.put("title", name);
            data.put("roomImgUrl", list.get(0).getAppImagePath());
        }

        ChatRoom.createChatRoom(realm, data, userIdList, new ChatRoom.onChatRoomCreatedListener() {
            @Override
            public void onCreate(ChatRoom chatRoom) { //로컬 realm 에 ChatRoom 생성되었음을 확인
                rid[0] = chatRoom.getRid();
                onNewChatCreatedListener.onCreate(rid[0]); //생성된 ChatRoom 의 rid 를 리스너 콜백으로 넘겨줌
            }
        });
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
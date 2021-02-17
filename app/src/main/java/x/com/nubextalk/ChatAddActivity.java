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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

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
import x.com.nubextalk.Model.ChatRoomMember;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.Module.Adapter.ChatAddMemberAdapter;
import x.com.nubextalk.Module.Adapter.ChatAddSearchAdapter;

public class ChatAddActivity extends AppCompatActivity implements
        ChatAddSearchAdapter.OnItemSelectedListener, View.OnClickListener {
    private ArrayList<User> userList = new ArrayList<User>();
    private ArrayList<ChatAddActivityUser> chatAddActivityUserArrayList = new ArrayList<>();
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
    private String chatRoomId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_add);

        Intent intent = getIntent();
        //기존 채팅방에서 대화상대 추가한 경우는 기존 채팅방의 멤버들을 가져오기 위해 rid 값을 받아옴
        if (intent.hasExtra("rid")) {
            chatRoomId = intent.getExtras().getString("rid");
        }

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
//        selectedMemberAdapter = new ChatAddMemberAdapter(this, addedUserList);
        selectedMemberAdapter = new ChatAddMemberAdapter(this, chatAddActivityUserArrayList);

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
     * 기존 채팅방에서 '대화상대 추가' 를 통해 온 경우에는 기존 채팅방 사용자 데이터를 추가해줌
     */
    public void initUserList() {
        RealmResults<User> users = User.getUserlist(this.realm);

        for (User user : users) {
            if (!userList.contains(user)) {
                userList.add(user);
            }
        }

        if (chatRoomId != null) {
            RealmResults<ChatRoomMember> chatRoomMembers = ChatRoom.getChatRoomUsers(realm, chatRoomId);
            for (ChatRoomMember chatRoomMember : chatRoomMembers) {
                User user = realm.where(User.class).equalTo("userId", chatRoomMember.getUid()).findFirst();
                if (user != null) {
                    chatAddActivityUserArrayList.add(new ChatAddActivityUser(user, true));
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
        RealmResults<User> users = this.realm.where(User.class).contains("appName", name).
                or().contains("appNickName", name).findAll();
        if (!users.isEmpty()) {
            realmSearchAdapter.deleteAllItem();
            for (User user : users) {
                if (!user.getUserId().contentEquals(Config.getMyAccount(realm).getExt1())) {
                    realmSearchAdapter.addItem(user);
                }
            }
        } else {
            realmSearchAdapter.deleteAllItem();
            realmSearchAdapter.setItem(userList);
        }
    }

    /**
     * 리싸이클러뷰에서 사용자 아이템 선택시 아이템 추가
     **/
    @Override
    public void onItemSelected(User user) {
        ChatAddActivityUser chatRoomUserStatus = new ChatAddActivityUser(user, false);
        selectedMemberAdapter.addItem(chatRoomUserStatus);

        //사용자 아이템을 클릭한 뒤 사용자 검색창 초기화 및 키보드 숨기기
        userNameInput.setText("");
        imm.hideSoftInputFromWindow(realmMemberSearchView.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_add_confirm_btn:
                ArrayList<ChatAddActivityUser> selectedUser = selectedMemberAdapter.getItemList();
                ArrayList<User> userArrayList = new ArrayList<>();

                for (ChatAddActivityUser chatRoomUserStatus : selectedUser) {
                    userArrayList.add(chatRoomUserStatus.getUser());
                }

                String roomName = chatRoomNameInput.getText().toString();

                //선택된 유저가 한명일 때
                if (selectedUser.size() == 1) {
                    ChatRoom chatRoom = User.getChatroom(realm, selectedUser.get(0).getUser());
                    //선택된 유저와의 채팅방이 없으면 새로운 채팅방 생성
                    if (chatRoom == null) {
                        Intent intent = new Intent(this, ChatRoomActivity.class);

                        // 1:1 채팅방 생성의 경우 서버에 기존 1:1 채팅방이 있는지 확인
                        // 존재하는 경우에는 해당 채팅방의 rid 를 서버로부터 받아와 로컬에 채팅방 만듦
                        // 존재하지 않는 경우에는 로컬에서 새로 만듦
                        createNewChat(this.realm, this, userArrayList, roomName, new onNewChatCreatedListener() {
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

                } else if (selectedUser.size() > 1) { //선택된 유저가 여러명일 때, 단톡방
                    if (roomName.isEmpty()) {
                        roomName = getUserName(userArrayList);
                    }
                    Intent intent = new Intent(this, ChatRoomActivity.class);
                    createNewChat(this.realm, this, userArrayList, roomName, new onNewChatCreatedListener() {
                        @Override
                        public void onCreate(String rid) {
                            intent.putExtra("rid", rid);
                            startActivity(intent);
                        }
                    });
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
     * @param context
     * @param list    사용자 User 리스트
     * @param name    채팅방 이름
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
        } else if (list.size() > 1) { // 단톡방인 경우
            data.put("title", name);
            data.put("roomImgUrl", list.get(0).getAppImagePath());
        }

        ChatRoom.createChatRoom(realm, data, userIdList, new ChatRoom.OnChatRoomCreatedListener() {
            @Override
            public void onCreate(ChatRoom chatRoom) { //로컬 realm 에 ChatRoom 생성되었음을 확인
                rid[0] = chatRoom.getRid();
                onNewChatCreatedListener.onCreate(rid[0]); //생성된 ChatRoom 의 rid 를 리스너 콜백으로 넘겨줌
            }
        });
    }

    public String getUserName(ArrayList<User> userArrayList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < userArrayList.size(); i++) {
            String name = userArrayList.get(i).getAppName();
            stringBuilder.append(name);
            if (i != userArrayList.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
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

    /**
     * 사용자가 기존 채팅방에 있었던 사용자인지 새롭게 추가되는 사용자인지 확인하기 위한 정보를 담은 클래스
     */
    public static class ChatAddActivityUser{
        private User user;
        private Boolean isAlreadyChatRoomUser;

        public ChatAddActivityUser(User user, Boolean isAlreadyChatRoomUser) {
            this.user = user;
            this.isAlreadyChatRoomUser = isAlreadyChatRoomUser;
        }

        public User getUser() {
            return this.user;
        }

        public Boolean getIsAlreadyChatRoomUser() {
            return this.isAlreadyChatRoomUser;
        }
    }
}
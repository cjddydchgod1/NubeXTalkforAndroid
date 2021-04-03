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

import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import x.com.nubextalk.Manager.FireBase.FirebaseFunctionsManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.ChatRoomMember;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.Module.Adapter.ChatAddMemberAdapter;
import x.com.nubextalk.Module.Adapter.ChatAddSearchAdapter;

import static x.com.nubextalk.Module.CodeResources.EMPTY;
import static x.com.nubextalk.Module.CodeResources.HOSPITAL_ID;
import static x.com.nubextalk.Module.CodeResources.MSG_INVITE_MEMBER;

public class ChatAddActivity extends AppCompatActivity implements
        ChatAddSearchAdapter.OnItemSelectedListener, View.OnClickListener {
    private ArrayList<User> mUserList = new ArrayList<>();
    private ArrayList<ChatAddActivityUser> mAddUserList = new ArrayList<>();
    private ChatAddMemberAdapter mSelectedMemberAdapter;
    private ChatAddSearchAdapter mRealmSearchAdapter;
    private EditText mUserNameInput;
    private InputMethodManager mInputMethodManager;
    private RecyclerView mRealmMemberSearchView;
    private RecyclerView mSelectedMemberView;
    private Realm mRealm;
    private String mRid;
    private Context mContext;
    private Button mConfirmBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_add);

        Intent intent = getIntent();
        //기존 채팅방에서 대화상대 추가한 경우는 기존 채팅방의 멤버들을 가져오기 위해 rid 값을 받아옴
        if (intent.hasExtra("rid")) {
            mRid = intent.getExtras().getString("rid");
        }
        mContext = this;

        mRealm = Realm.getInstance(UtilityManager.getRealmConfig());
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mUserNameInput = findViewById(R.id.chat_add_chat_user_input);
        mUserNameInput.setSingleLine(true);
        mUserNameInput.addTextChangedListener(textWatcher);
        mConfirmBtn = findViewById(R.id.chat_add_confirm_btn);
        Button cancelBtn = findViewById(R.id.chat_add_cancel_btn);

        mConfirmBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        initUserList(); //리싸이클러뷰에 사용될 사용자 데이터 리스트 초기화

        //사용자 검색 및 목록 리싸이클러뷰 설정
        mRealmMemberSearchView = findViewById(R.id.chat_add_member_search_view);
        mRealmSearchAdapter = new ChatAddSearchAdapter(this, mUserList);
        mRealmSearchAdapter.setItemSelectedListener(this);
        mRealmMemberSearchView.
                setLayoutManager(new LinearLayoutManager(
                        this, LinearLayoutManager.VERTICAL, false));
        mRealmMemberSearchView.setAdapter(mRealmSearchAdapter);

        //선택된 사용자 표시 리싸이클러뷰 설정
        mSelectedMemberView = findViewById(R.id.chat_added_member_view);
        mSelectedMemberAdapter = new ChatAddMemberAdapter(this, mAddUserList);

        mSelectedMemberView.
                setLayoutManager(new LinearLayoutManager(
                        this, LinearLayoutManager.HORIZONTAL, false));
        mSelectedMemberView.setAdapter(mSelectedMemberAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRealm != null) {
            mRealm.close();
            mRealm = null;
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
        RealmResults<User> users = User.getUserlist(this.mRealm);

        for (User user : users) {
            if (!mUserList.contains(user)) {
                mUserList.add(user);
            }
        }

        if (mRid != null) {
            RealmResults<ChatRoomMember> chatRoomMembers = ChatRoom.getChatRoomUsers(mRealm, mRid);
            for (ChatRoomMember chatRoomMember : chatRoomMembers) {
                User user = mRealm.where(User.class).equalTo("uid", chatRoomMember.getUid()).findFirst();
                if (user != null) {
                    mAddUserList.add(new ChatAddActivityUser(user, true));
                }
            }
            removeExistUserItem(mUserList);
        }
    }

    /**
     * 사용자 검색해서 해당 사용자가 있으면 보여주는 함수
     *
     * @param name 사용자 이름
     */
    private void searchUser(String name) {
        RealmResults<User> users = this.mRealm.where(User.class).contains("appName", name).
                or().contains("appNickName", name).findAll();
        ArrayList<User> userArrayList = new ArrayList<>();
        if (!users.isEmpty()) {
            mRealmSearchAdapter.deleteAllItem();

            for (User user : users) {
                if (!user.getUid().contentEquals(Config.getMyAccount(mRealm).getExt1())) {
                    userArrayList.add(user);
                }
            }
            removeExistUserItem(userArrayList);
            for (User user : userArrayList) {
                mRealmSearchAdapter.addItem(user);
            }

        } else {
            mRealmSearchAdapter.deleteAllItem();
            mRealmSearchAdapter.setItem(mUserList);
        }
    }

    /**
     * 리싸이클러뷰에서 사용자 아이템 선택시 아이템 추가
     **/
    @Override
    public void onItemSelected(User user) {
        ChatAddActivityUser chatRoomUserStatus = new ChatAddActivityUser(user, false);
        mSelectedMemberAdapter.addItem(chatRoomUserStatus);
        mSelectedMemberView.scrollToPosition(mSelectedMemberAdapter.getItemCount() - 1);

        //사용자 아이템을 클릭한 뒤 사용자 검색창 초기화 및 키보드 숨기기
        mUserNameInput.setText(EMPTY);
        mInputMethodManager.hideSoftInputFromWindow(mRealmMemberSearchView.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_add_confirm_btn:
                mConfirmBtn.setClickable(false); //중복 채팅방 생성 방지

                ArrayList<ChatAddActivityUser> selectedUser = mSelectedMemberAdapter.getItemList();
                ArrayList<User> userArrayList = new ArrayList<>();

                for (ChatAddActivityUser chatRoomUserStatus : selectedUser) {
                    userArrayList.add(chatRoomUserStatus.getUser());
                }

                if (mRid == null) { //새로운 채팅방 생성
                    userArrayList.add(User.getMyAccountInfo(mRealm));
                    //선택된 유저가 한명일 때 - 1:1 채팅방
                    if (selectedUser.size() == 1) {
                        Intent intent = new Intent(this, ChatRoomActivity.class);
                        User.getChatroom(mRealm, selectedUser.get(0).getUser(), new User.UserListener() {
                            @Override
                            public void onFindPersonalChatRoom(ChatRoom chatRoom) {
                                //선택된 유저와의 채팅방이 없으면 새로운 채팅방 생성
                                if (chatRoom == null) {
                                    String roomName = getUserNameSequence(userArrayList);

                                    // 1:1 채팅방 생성의 경우 서버에 기존 1:1 채팅방이 있는지 확인
                                    // 존재하는 경우에는 해당 채팅방의 rid 를 서버로부터 받아와 로컬에 채팅방 만듦
                                    // 존재하지 않는 경우에는 로컬에서 새로 만듦
                                    createNewChat(mRealm, mContext, userArrayList, roomName, new onNewChatCreatedListener() {
                                        @Override
                                        public void onCreate(String rid) {
                                            intent.putExtra("rid", rid);
                                            startActivity(intent);
                                        }
                                    });
                                } else { //기존에 선택된 유저와 채팅방이 있으면 그 채팅방으로 이동
                                    intent.putExtra("rid", chatRoom.getRid());
                                    startActivity(intent);
                                }
                            }
                        });


                    } else if (selectedUser.size() > 1) { //선택된 유저가 여러명일 때, 단톡방
                        String roomName = getUserNameSequence(userArrayList);
                        Intent intent = new Intent(this, ChatRoomActivity.class);
                        createNewChat(this.mRealm, this, userArrayList, roomName, new onNewChatCreatedListener() {
                            @Override
                            public void onCreate(String rid) {
                                intent.putExtra("rid", rid);
                                startActivity(intent);
                            }
                        });
                    }
                } else { //기존 채팅방에서 대화 상대 추가
                    ChatRoom chatRoom = mRealm.where(ChatRoom.class).equalTo("rid", mRid).findFirstAsync();
                    Intent intent = new Intent(this, ChatRoomActivity.class);

                    chatRoom.addChangeListener(new RealmChangeListener<ChatRoom>() {
                        @Override
                        public void onChange(ChatRoom chatRoom1) {
                            if (chatRoom1.isValid()) {
                                if (chatRoom1.getIsGroupChat()) { //단체 채팅방인 경우 선택한 사용자(들)만 초대

                                    //기존에 있던 유저들은 초대에서 제외
                                    for (ChatAddActivityUser chatAddActivityUser : selectedUser) {
                                        if (chatAddActivityUser.getIsAlreadyChatRoomUser()) {
                                            userArrayList.remove(chatAddActivityUser.getUser());
                                        }
                                    }

                                    inviteChatUser(mRealm, HOSPITAL_ID, mRid, userArrayList);
                                    onBackPressed();

                                } else { // 기존 1:1 채팅방에서 새로운 대화상대를 초대하는 경우 새로운 단체 채팅방 생성
                                    if (userArrayList.size() != 2) {
                                        String roomName = getUserNameSequence(userArrayList);
                                        createNewChat(mRealm, mContext, userArrayList, roomName, new onNewChatCreatedListener() {
                                            @Override
                                            public void onCreate(String rid) {
                                                intent.putExtra("rid", rid);
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                }
                            }

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
     * @param userList 사용자 User 리스트
     * @param roomName 채팅방 이름
     */
    public static void createNewChat(Realm realm, Context context, ArrayList<User> userList, String roomName,
                                     onNewChatCreatedListener onNewChatCreatedListener) {
        Map<String, Object> data = new HashMap<>();
        data.put("hid", HOSPITAL_ID);
        final String[] rid = {null};

        ArrayList<String> userIdList = new ArrayList<>();
        for (User user : userList) {
            userIdList.add(user.getUid());
        }

        data.put("title", roomName);
        data.put("roomImgUrl", userList.get(0).getAppImagePath());

        ChatRoom.createChatRoom(realm, data, userIdList, new ChatRoom.OnChatRoomCreatedListener() {
            @Override
            public void onCreate(ChatRoom chatRoom) { //로컬 realm 에 ChatRoom 생성되었음을 확인
                rid[0] = chatRoom.getRid();
                onNewChatCreatedListener.onCreate(rid[0]); //생성된 ChatRoom 의 rid 를 리스너 콜백으로 넘겨줌
            }
        });
    }

    /**
     * 유저 리스트를 입력받아 유저들의 이름 시퀀스 문자열을 반환해주는 함수
     *
     * @param userArrayList
     * @return
     */
    public String getUserNameSequence(ArrayList<User> userArrayList) {
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
     * 기존 채팅방 유저가 있는 경우 유저 검색 리스트에 기존 채팅방 유저가 나오지 않도록 데이터 제거 해주는 함수
     *
     * @param userList
     */
    public void removeExistUserItem(ArrayList<User> userList) {
        RealmResults<ChatRoomMember> chatRoomMembers = ChatRoom.getChatRoomUsers(mRealm, mRid);
        for (ChatRoomMember chatRoomMember : chatRoomMembers) {
            Iterator<User> userIterator = userList.iterator();
            while (userIterator.hasNext()) {
                if (chatRoomMember.getUid().equals(userIterator.next().getUid())) {
                    userIterator.remove();
                }
            }
        }
    }

    /**
     * 기존 채팅방 대화 상대 추가 함수
     * 1:1 채팅방에서 대화 상대 추가의 경우 새로운 realm 채팅방 생성
     * 단체 채팅방에서 대화 상대 추가의 경우 대화 상대 FireStore 에 추가 후 기존 단체 채팅방 유저들에게 알림
     *
     * @param realm
     * @param hid
     * @param rid
     * @param userList
     */
    public void inviteChatUser(Realm realm, String hid, String rid, ArrayList<User> userList) {
        Boolean isGroupChat = false;
        ChatRoom chatRoom = realm.where(ChatRoom.class).equalTo("rid", rid).findFirst();
        if (chatRoom != null) {
            isGroupChat = chatRoom.getIsGroupChat();

            if (isGroupChat) { //단체 채팅방인 경우
                FirebaseFirestore fs = FirebaseFirestore.getInstance();
                for (User user : userList) {
                    Map<String, Object> data = new HashMap<>();
                    fs.collection("hospital").document(hid)
                            .collection("chatRoom").document(rid)
                            .collection("chatRoomMember")
                            .document(user.getUid()).set(data);
                }

                String uid = User.getMyAccountInfo(realm).getUid();
                Map<String, Object> chat = new HashMap<>();
                String cid = uid.concat(String.valueOf(new Date().getTime()));
                chat.put("rid", rid);
                chat.put("uid", uid);
                chat.put("cid", cid);
                chat.put("content", MSG_INVITE_MEMBER);
                chat.put("type", "9");

                ChatContent.createChat(realm, chat);

                Map<String, Object> data = new HashMap<>();
                JSONArray jsonArray = new JSONArray();

                for (User user : userList) {
                    jsonArray.put(user.getUid());
                }

                data.put("hid", hid);
                data.put("rid", rid);
                data.put("uid", uid);
                data.put("cid", cid);
                data.put("membersId", jsonArray);

                FirebaseFunctionsManager.notifyToChatRoomAddedUser(data);

            } else { // 1:1 채팅방인 경우
                ArrayList<User> userArrayList = new ArrayList<>(userList);
                RealmResults<ChatRoomMember> realmResults = ChatRoom.getChatRoomUsers(realm, rid);

                for (ChatRoomMember chatRoomMember : realmResults) {
                    User user = realm.where(User.class).equalTo("uid", chatRoomMember.getUid()).findFirst();
                    if (user != null) {
                        userArrayList.add(user);
                    }
                }

                Intent intent = new Intent(this, ChatRoomActivity.class);
                ChatAddActivity.createNewChat(realm, this, userArrayList, getUserNameSequence(userArrayList), new onNewChatCreatedListener() {
                    @Override
                    public void onCreate(String rid) {
                        intent.putExtra("rid", rid);
                        startActivity(intent);
                    }
                });
            }
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

    /**
     * 사용자가 기존 채팅방에 있었던 사용자인지 새롭게 추가되는 사용자인지 확인하기 위한 정보를 담은 클래스
     */
    public static class ChatAddActivityUser {
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
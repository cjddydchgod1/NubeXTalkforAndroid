/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import co.moonmonkeylabs.realmsearchview.RealmSearchView;
import io.realm.Realm;
import x.com.nubextalk.Manager.FireBase.FirebaseFunctionsManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.ChatRoomMember;
import x.com.nubextalk.Model.Config;
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
    private FirebaseFirestore fs;


    private Button chatAddConfirmButton;
    private Button chatAddCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_add);
        fs = FirebaseFirestore.getInstance();

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
        ArrayList<User> selectedUser = memberAdapter.getUserList();
        String roomName = chatRoomNameInput.getText().toString();
        switch (v.getId()) {
            case R.id.chat_add_confirm_btn:
                if(createNewChat(realm, selectedUser, roomName)) {
                    setResult(RESULT_OK); //MainActivity 로 결과 전달
                    finish();
                } else {
                    Toast.makeText(this, "채팅방 이름을 입력해주세요.", Toast.LENGTH_SHORT ).show();
                }
                break;

            case R.id.chat_add_cancel_btn:
                onBackPressed();
                break;
        }
    }

    public static boolean createNewChat(Realm realm, ArrayList<User> list, String name) {
        Config myProfile = realm.where(Config.class).equalTo("CODENAME", "MyAccount").findFirst();
//        String token = myProfile.getExt1();
        String token = myProfile.getExt4();
        Gson gson = new Gson();
        String hospital = "w34qjptO0cYSJdAwScFQ";
        Map<String, Object> value = new HashMap<>();
        value.put("token", token);
        value.put("hospital", hospital);

        JSONArray jsonArray = new JSONArray();
        for(User user : list){
            jsonArray.put(user.getUid());
        }
//        jsonArray.put(myProfile.getOid());
        jsonArray.put(myProfile.getExt1());
        value.put("members",  jsonArray);
        Log.d("test", value.toString());


        if (list.size() == 1) { /** 선택된 유저가 한명일 때 **/
            if (!name.isEmpty()) { // 채팅방 이름을 입력했을 때
                value.put("title", name);
            } else { // 채팅방 이름 입력 안했을 때 = 상대방 이름으로 채팅방 이름 설정
                value.put("title", list.get(0).getName());
            }
            value.put("roomImgUrl", list.get(0).getProfileImg());
        } else {
            if (!name.isEmpty()) {
                value.put("title", name);
            } else {
                return false;
            }
            value.put("roomImgUrl", list.get(0).getProfileImg());
        }
        FirebaseFunctionsManager.createChatRoom(token, value);


//        /**확인 버튼을 누르면 새로운 ChatRoom 생성하고 해당 rid 를 ChatRoomMember 에 rid, uid 넣어서 생성**/
//        ArrayList<User> list = memberAdapter.getUserList();
//        String rid = getRandomString().toString();
//        ChatRoom newChatRoom = new ChatRoom();
//        newChatRoom.setRid(rid);
//        if (selectedUser.size() == 1) { /** 선택된 유저가 한명일 때 **/
//            if (!roomName.isEmpty()) { // 채팅방 이름을 입력했을 때
//                newChatRoom.setRoomName(roomName);
//            } else { // 채팅방 이름 입력 안했을 때 = 상대방 이름으로 채팅방 이름 설정
//                newChatRoom.setRoomName(selectedUser.get(0).getName());
//            }
//            newChatRoom.setRoomImg(selectedUser.get(0).getProfileImg());
//        } else {
//            if (!roomName.isEmpty()) {
//                newChatRoom.setRoomName(roomName);
//            } else {
//                Toast.makeText(this, "채팅방 이름을 입력해주세요.", Toast.LENGTH_SHORT ).show();
//                return false;
//            }
//            newChatRoom.setRoomImg("");
//        }
//
//        Date date = new Date();
//        newChatRoom.setUpdatedDate(date);
//
//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                realm.copyToRealmOrUpdate(newChatRoom);
//
//                for (User user : selectedUser) {
//                    ChatRoomMember chatMember = new ChatRoomMember();
//                    chatMember.setRid(rid);
//                    chatMember.setUid(user.getUid());
//                    /**
//                     * ChatRoomMember 모델이 Primary Key 가 없어서 copyToRealmOrUpdate 함수는
//                     * 사용하지 못하기 때문에 copyToRealm 함수를 사용함.
//                     * 참고: https://stackoverflow.com/questions/40999299/android-create-realm-table-without-primary-key
//                     **/
//                    realm.copyToRealm(chatMember);
//
//                }
//                ChatContent chat = new ChatContent();
//                chat.setCid(); // Content ID 자동으로 유니크한 값 설정
//                chat.setRid(rid); // RID 채팅방 아이디
//                chat.setType(9); // 시스템 메세지
//                chat.setContent("채팅방이 개설 되었습니다.");
//                chat.setIsRead(true);
//                chat.setSendDate(date);
//                realm.copyToRealmOrUpdate(chat);
//
//
//            }
//        });


        return true;
    }
}
/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import co.moonmonkeylabs.realmsearchview.RealmSearchView;
import io.realm.Realm;
import io.realm.RealmList;
import x.com.nubextalk.Manager.DateManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.Module.Adapter.ChatAddMemberAdapter;
import x.com.nubextalk.Module.Adapter.ChatAddSearchAdapter;

public class ChatAddActivity extends AppCompatActivity implements
        ChatAddSearchAdapter.OnItemSelectedListner, View.OnClickListener {
    private Realm realm;
    private RecyclerView realmMemberSearchView;
    private RecyclerView selectedMemberView;
    private EditText chatRoomNameInput;
    private ChatAddSearchAdapter realmSearchAdapter;
    private ChatAddMemberAdapter selectedMemberAdapter;
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
        realmMemberSearchView = findViewById(R.id.chat_add_member_search_view);
        realmSearchAdapter = new ChatAddSearchAdapter(this, realm);
        realmSearchAdapter.setItemSelectedListener(this);
        realmMemberSearchView.setAdapter(realmSearchAdapter);

        selectedMemberView = findViewById(R.id.chat_added_member_view);
        selectedMemberAdapter = new ChatAddMemberAdapter(this, userList);
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
     * 사용자 아이템 클릭
     **/
    @Override
    public void onItemSelected(User user) {
        selectedMemberAdapter.addItem(user);
        selectedMemberAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        ArrayList<User> selectedUser = selectedMemberAdapter.getUserList();
        String roomName = chatRoomNameInput.getText().toString();
        switch (v.getId()) {
            case R.id.chat_add_confirm_btn:
                if (createNewChat(realm, selectedUser, roomName)) {
                    setResult(RESULT_OK); //MainActivity 로 결과 전달
                    finish();
                } else {
                    Toast.makeText(this, "채팅방 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
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
     * @return
     */
    public static boolean createNewChat(Realm realm, ArrayList<User> list, String name) {
        Config myProfile = realm.where(Config.class).equalTo("CODENAME", "MyAccount").findFirst();
        String token = myProfile.getExt4();
        String hospital = "w34qjptO0cYSJdAwScFQ";
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("hospital", hospital);

        ArrayList<String> userIdList = new ArrayList<>();
        for (User user : list) {
            userIdList.add(user.getUserId());
        }

        if (list.size() == 1) { // 1:1 채팅인 경우
            if (!name.equals("")) { // 채팅방 이름을 입력했을 때
                data.put("title", name);
            } else { // 채팅방 이름 입력 안했을 때 = "" 빈 내용으로 입력
                data.put("title", list.get(0).getAppName());
            }
            data.put("roomImgUrl", list.get(0).getAppImagePath());
        } else {
            if (!name.equals("")) { // 채팅방 이름을 입력했을 때
                data.put("title", name);
            } else { // 채팅방 이름 입력 안했을 때, 단톡방에서는 무조건 채팅방 이름 입력 하도록
                return false;
            }
            data.put("roomImgUrl", list.get(0).getAppImagePath());
        }

        ChatRoom.createChatRoom(realm, data, userIdList);
        return true;
    }
}
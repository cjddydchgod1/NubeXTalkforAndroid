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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import co.moonmonkeylabs.realmsearchview.RealmSearchView;
import io.realm.Realm;
import io.realm.RealmList;
import x.com.nubextalk.Manager.FireBase.FirebaseFunctionsManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.Module.Adapter.ChatAddMemberAdapter;
import x.com.nubextalk.Module.Adapter.ChatAddSearchAdapter;

public class AddChatMemberActivity extends AppCompatActivity implements
        ChatAddSearchAdapter.OnItemSelectedListner, View.OnClickListener {
    private Realm realm;
    private RecyclerView realmMemberSearchView;
    private RecyclerView selectedMemberView;
    private ChatAddSearchAdapter realmSearchAdapter;
    private ChatAddMemberAdapter selectedMemberAdapter;
    private ArrayList<User> userList = new ArrayList<User>();
    private Button chatAddConfirmButton;
    private Button chatAddCancelButton;

    private String chatRoomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat_member);

        Intent intent = getIntent();
        chatRoomId = intent.getExtras().getString("rid");

        realm = Realm.getInstance(UtilityManager.getRealmConfig());
        realmMemberSearchView = findViewById(R.id.add_chat_member_search_view);
        selectedMemberView = findViewById(R.id.add_chat_member_added_view);

        chatAddConfirmButton = findViewById(R.id.add_chat_confirm_btn);
        chatAddCancelButton = findViewById(R.id.add_chat_cancel_btn);
        chatAddConfirmButton.setOnClickListener(this);
        chatAddCancelButton.setOnClickListener(this);

        realmSearchAdapter = new ChatAddSearchAdapter(this, realm);
        realmSearchAdapter.setItemSelectedListener(this);
        realmMemberSearchView.setAdapter(realmSearchAdapter);

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
        String userName = user.getAppName();
        selectedMemberAdapter.addItem(user);
        selectedMemberAdapter.notifyDataSetChanged();
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
}
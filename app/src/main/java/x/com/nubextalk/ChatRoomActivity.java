package x.com.nubextalk;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aquery.AQuery;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.UploadTask;
import com.joanzapata.iconify.widget.IconButton;

import org.json.JSONArray;
import org.jsoup.select.Evaluator;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import x.com.nubextalk.Manager.DateManager;
import x.com.nubextalk.Manager.FireBase.FirebaseFunctionsManager;
import x.com.nubextalk.Manager.FireBase.FirebaseStorageManager;
import x.com.nubextalk.Manager.KeyboardManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.ChatRoomMember;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.Module.Adapter.ChatAdapter;

//채팅방 액티비티
public class ChatRoomActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private Realm realm;
    private RealmChangeListener realmChangeListener;
    private AQuery aq;
    private InputMethodManager imm;
    private FirebaseFirestore fs;
    private RealmResults<ChatContent> mChat;

    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private EditText mEditChat;
    private IconButton mSendButton;
    private String mRoomId;
    private String mUid;
    private String mHid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        realm = Realm.getInstance(UtilityManager.getRealmConfig());
        aq = new AQuery(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        fs = FirebaseFirestore.getInstance();

        //
        mUid = Config.getMyUID(realm);
        mHid = "w34qjptO0cYSJdAwScFQ";

        // rid 를 사용하여 채팅 내용과 채팅방 이름을 불러옴
        Intent intent = getIntent();
        mRoomId = intent.getExtras().getString("rid");
        ChatRoom roomInfo = realm.where(ChatRoom.class).equalTo("rid", mRoomId).findFirst();
        String roomTitle = roomInfo.getRoomName();

        // 채팅방 툴바 설정
        Toolbar toolbar_chat_room = findViewById(R.id.toolbar_chat_room);
        setSupportActionBar(toolbar_chat_room);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        TextView title = (TextView) findViewById(R.id.toolbar_chat_room_title);
        title.setText(roomTitle);

        // rid 참조하여 채팅내용 불러옴
        mChat = realm.where(ChatContent.class).equalTo("rid", mRoomId).findAll();
        setChatContentRead(mChat);

        // 하단 미디어 버튼, 에디트텍스트 , 전송 버튼을 아이디로 불러옴
        mEditChat = (EditText) findViewById(R.id.edit_chat);
        mSendButton = (IconButton) findViewById(R.id.send_button);

        // 버튼 아이콘 설정
        mSendButton.setText("{far-paper-plane 30dp #747475}");
//        mMediaButton.setText("{far-image 35dp #747475}");

        // on click listener 설정
        mSendButton.setOnClickListener(this);

        // 리사이클러 뷰와 어댑터를 연결 채팅을 불러 올수 있음
        mRecyclerView = (RecyclerView) findViewById(R.id.chat_room_content);
        mAdapter = new ChatAdapter(this, mChat);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);

        // Drawer navigation 설정
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        setNavigationView(roomInfo);
        View header = mNavigationView.getHeaderView(0);
        TextView drawerTitle = (TextView) header.findViewById(R.id.drawer_title);
        drawerTitle.setText(roomTitle);


        realmChangeListener = new RealmChangeListener() {
            @Override
            public void onChange(Object o) {
                mAdapter.notifyDataSetChanged();
                if (mAdapter.getItemCount() > 0)
                    mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
            }
        };
        realm.addChangeListener(realmChangeListener);


        final KeyboardManager km = new KeyboardManager(this);
        addContentView(km, new FrameLayout.LayoutParams(-1, -1));

        km.setOnShownKeyboard(new KeyboardManager.OnShownKeyboardListener() {
            @Override
            public void onShowSoftKeyboard() {
//                int keyboardHeight = km.getKeyboardHeight();
//                Log.d("keyBoard",Integer.toString(keyboardHeight));
//
//                //키보드 등장할 때
//                mRecyclerView.scrollBy(0,987);
                mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);

            }
        });
        km.setOnHiddenKeyboard(new KeyboardManager.OnHiddenKeyboardListener() {
            @Override
            public void onHiddenSoftKeyboard() {
//                mRecyclerView.scrollBy(0,-987);
                mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
            }
        });

//        mDrawerLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
//             public void onGlobalLayout(){
//                   int heightDiff = mDrawerLayout.getRootView().getHeight()- mDrawerLayout.getHeight();
//                   Log.d("dadsasdsdasd",Integer.toString(heightDiff));
//                }
//        });

    }

    private void setChatContentRead(RealmResults<ChatContent> mChat) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (ChatContent chatContent : mChat) {
                    if (!chatContent.getIsRead()) {
                        chatContent.setIsRead(true);
                    }
                }
                ;
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(10);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override // Button onClick Listener
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_button:
                sendChat();
                break;
        }
    }

    @Override // Option Item Selected Listener
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                imm.hideSoftInputFromWindow(mEditChat.getWindowToken(), 0);
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override // Navigation Item Selected Listener
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Menu menu = mNavigationView.getMenu();
        int id = menuItem.getItemId();
        SwitchCompat fixTopSwitch = (SwitchCompat) MenuItemCompat.getActionView(menu.findItem(R.id.nav_setting_fix_top)).findViewById(R.id.drawer_switch);
        SwitchCompat alarmSwitch = (SwitchCompat) MenuItemCompat.getActionView(menu.findItem(R.id.nav_setting_alarm)).findViewById(R.id.drawer_switch);

        switch (id) {
            case R.id.nav_camera:
                openCamera();
                break;

            case R.id.nav_gallery:
                openAlbum();
                break;

            case R.id.nav_pacs:
                openPacs();
                break;

            case R.id.nav_setting_fix_top:
                fixTopSwitch.toggle();
                break;

            case R.id.nav_setting_alarm:
                alarmSwitch.toggle();
                break;

            case R.id.nav_exit:
                mDrawerLayout.closeDrawers();
                exitRoom();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri file = data.getData();
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("(yyyy-MM-dd'T'hh:mm:ss)");

            UploadTask uploadTask = FirebaseStorageManager.uploadFile(file, "hospital/" + mHid + "/chatroom/" + mRoomId + "/" + mUid + simpleDateFormat.format(date));

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("(yyyy-MM-dd'T'hh:mm:ss)");
            UploadTask uploadTask = FirebaseStorageManager.uploadFile(image, "hospital/" + mHid + "/chatroom/" + mRoomId + "/" + mUid + simpleDateFormat.format(date));
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });

        }
    }

    private void setNavigationView(ChatRoom roomInfo) {
        Menu menu = mNavigationView.getMenu();
        MenuItem item = menu.findItem(R.id.menu_chat_member);
        SubMenu subMenu = item.getSubMenu();

        RealmResults<ChatRoomMember> chatRoomMembers = realm.where(ChatRoomMember.class).equalTo("rid", mRoomId).findAll();

        int menuId = 2131313;
        for (ChatRoomMember member : chatRoomMembers) {
            String userName = realm.where(User.class).equalTo("userId", member.getUid()).findFirst().getAppName();
            subMenu.add(0, menuId++, 0, userName);
        }
        SwitchCompat fixTopSwitch = (SwitchCompat) MenuItemCompat.getActionView(menu.findItem(R.id.nav_setting_fix_top)).findViewById(R.id.drawer_switch);
        SwitchCompat alarmSwitch = (SwitchCompat) MenuItemCompat.getActionView(menu.findItem(R.id.nav_setting_alarm)).findViewById(R.id.drawer_switch);

        if (roomInfo.getSettingFixTop()) {
            fixTopSwitch.setChecked(true);
        } else {
            fixTopSwitch.setChecked(false);
        }
        if (roomInfo.getSettingAlarm()) {
            alarmSwitch.setChecked(true);
        } else {
            alarmSwitch.setChecked(false);
        }

        fixTopSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            roomInfo.setSettingFixTop(true);
                            Toast.makeText(getApplicationContext(), "상단고정이 설정 되었습니다.", Toast.LENGTH_SHORT).show();
                            realm.copyToRealmOrUpdate(roomInfo);
                        }
                    });
                } else {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            roomInfo.setSettingFixTop(false);
                            Toast.makeText(getApplicationContext(), "상단고정이 해제 되었습니다.", Toast.LENGTH_SHORT).show();
                            realm.copyToRealmOrUpdate(roomInfo);
                        }
                    });
                }
            }
        });
        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            roomInfo.setSettingAlarm(true);
                            Toast.makeText(getApplicationContext(), "알람기능이 설정 되었습니다.", Toast.LENGTH_SHORT).show();
                            realm.copyToRealmOrUpdate(roomInfo);
                        }
                    });
                } else {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            roomInfo.setSettingAlarm(false);
                            Toast.makeText(getApplicationContext(), "알람기능이 해제 되었습니다.", Toast.LENGTH_SHORT).show();
                            realm.copyToRealmOrUpdate(roomInfo);
                        }
                    });
                }
            }
        });

    }

    private void openAlbum() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            if (intent.resolveActivity(this.getPackageManager()) != null) {
                startActivityForResult(intent, 1);
            }
        }
    }

    private void openCamera() {
        Log.d("camera", "start");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            Log.d("camera", "permission error");
        } else {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 2);
            Log.d("camera", "goto intent");
        }
    }

    private void openPacs() {
    }

    private void sendChat() {
        String content;
        if ((content = String.valueOf(mEditChat.getText())).equals("")) {
            aq.toast("메세지를 입력하세요");
        } else {

            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);


//          채팅목록 최신순 정렬을 위해 ChatRoom updatedDate 갱신
            ChatRoom roomInfo = realm.where(ChatRoom.class).equalTo("rid", mRoomId).findFirst();
            Date roomUpdateDate = roomInfo.getUpdatedDate();

            //서버에 채팅방 업데이트 시간 업뎃
//            fs.collection("hospital").document(mHid)
//                    .collection("chatRoom").document(mRoomId)
//                    .update("updatedDate", simpleDateFormat.format(date));

            Map<String, Object> chat = new HashMap<>();
            String cid = mUid.concat(String.valueOf(date.getTime()));
            chat.put("cid", cid);
            chat.put("uid", mUid);
            chat.put("rid", mRoomId);
            chat.put("content", content);
            chat.put("sendDate", simpleDateFormat.format(date));
            chat.put("type", "0");
            if (DateManager.isSameDay(date, roomUpdateDate)) {
                chat.put("isFirst", "false");
            } else {
                chat.put("isFirst", "true");
            }

            //채팅방이 realm에만 생성되있는 경우, firestore 서버 에도 채팅방 생성한 다음 채팅메세지 서버에 추가
            if(realm.where(ChatContent.class).equalTo("rid",mRoomId).findAll().isEmpty()){
                Log.d("CHATROOM", "채팅방 서버에 생성한다잉 ");
                Map<String, Object> chatRoomData = new HashMap<>();
                RealmResults<ChatRoomMember> chatRoomMember = ChatRoom.getChatRoomUsers(realm, mRoomId);
                JSONArray chatRoomMemberJsonArray = new JSONArray();
                for( ChatRoomMember member : chatRoomMember ) {
                    chatRoomMemberJsonArray.put(member.getUid());
                }
                chatRoomData.put("hospital", mHid);
                chatRoomData.put("chatRoomId", mRoomId);
                chatRoomData.put("members", chatRoomMemberJsonArray);
                chatRoomData.put("title", roomInfo.getRoomName());
                chatRoomData.put("roomImgUrl", roomInfo.getRoomImg());
                chatRoomData.put("notificationId", roomInfo.getNotificationId());
                FirebaseFunctionsManager.createChatRoom(chatRoomData).addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                    @Override
                    public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                        Log.d("CHATROOM", "채팅방 생성 완료!");
                        fs.collection("hospital").document(mHid)
                            .collection("chatRoom").document(mRoomId)
                            .collection("chatContent").document(cid)
                            .set(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("CHATROOM", "채팅 생성 완료!");
                                }}
                            );
                    }
                });
            }

            ChatContent.createChat(realm, chat);

            fs.collection("hospital").document(mHid)
                    .collection("chatRoom").document(mRoomId)
                    .collection("chatContent").document(cid)
                    .set(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("CHATROOM", "채팅 생성 완료!");
                }
            });

            //서버에 채팅 추가
//            fs.collection("hospital").document(mHid)
//                    .collection("chatRoom").document(mRoomId)
//                    .collection("chatContent").add(chat)
//                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                        @Override
//                        public void onSuccess(DocumentReference documentReference) {
//                            fs.collection("hospital").document(mHid)
//                                    .collection("chatRoom").document(mRoomId)
//                                    .collection("chatContent").document(documentReference.getId())
//                                    .update("cid", documentReference.getId());
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                        }
//                    });
            mEditChat.setText("");
        }
    }

    private void exitRoom() {
        ChatRoom roomInfo = realm.where(ChatRoom.class).equalTo("rid", mRoomId).findFirst();
        RealmResults<ChatContent> chatInfo = realm.where(ChatContent.class)
                .equalTo("rid", mRoomId)
                .findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                roomInfo.deleteFromRealm();
                realm.copyToRealmOrUpdate(roomInfo);
                chatInfo.deleteAllFromRealm();
                realm.copyToRealmOrUpdate(chatInfo);
            }
        });
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
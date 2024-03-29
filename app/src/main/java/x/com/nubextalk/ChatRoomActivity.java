/*
 * Created By JONGHO, LEE, Researcher of Institute of Biomedical Engineering, TechHeim Co.,Ltd.
 * on  2021.
 * This software is not stable health-care system and not certificated any health-care authority(like CE, FDA)
 * Prohibit redistribution, modification, commercial use.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aquery.AQuery;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.UploadTask;
import com.joanzapata.iconify.widget.IconButton;

import org.json.JSONArray;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.grpc.okhttp.internal.framed.Header;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
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
import x.com.nubextalk.Module.CodeResources;
import x.com.nubextalk.Module.Fragment.PACSReferenceFragment;
import x.com.nubextalk.Module.Fragment.RoomNameModificationDialogFragment;

import static android.os.Environment.DIRECTORY_DCIM;
import static x.com.nubextalk.Module.CodeResources.EMPTY;
import static x.com.nubextalk.Module.CodeResources.EMPTY_IMAGE;
import static x.com.nubextalk.Module.CodeResources.HOSPITAL_ID;
import static x.com.nubextalk.Module.CodeResources.ICON_SEND_CHAT;
import static x.com.nubextalk.Module.CodeResources.MSG_ALARM_OFF;
import static x.com.nubextalk.Module.CodeResources.MSG_ALARM_ON;
import static x.com.nubextalk.Module.CodeResources.MSG_EMPTY_CONTENT;
import static x.com.nubextalk.Module.CodeResources.MSG_FIX_TOP_OFF;
import static x.com.nubextalk.Module.CodeResources.MSG_FIX_TOP_ON;
import static x.com.nubextalk.Module.CodeResources.PATH_IMAGE;
import static x.com.nubextalk.Module.CodeResources.PATH_STORAGE1;
import static x.com.nubextalk.Module.CodeResources.PATH_STORAGE3;

//채팅방 액티비티
public class ChatRoomActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Realm mRealm;
    private AQuery mAquery;
    private InputMethodManager mInputMethodManager;
    private KeyboardManager mKeyboardManager;

    private String mHid;
    private String mRid;
    private String mUid;

    private ChatAdapter mAdapter;
    private RealmResults<ChatContent> mChatContents;
    private ChatRoom mChatRoom;
    private int mChatContentsIndex;
    private File mFile;

    private RecyclerView mRecyclerView;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private ActionBar mActionbar;
    private TextView mToolbarTitle;
    private View mHeader;
    private TextView mDrawerTitle;


    private NavigationView mNavigationView;
    private EditText mEditChat;
    private IconButton mSendButton;

    private PACSReferenceFragment mPacsReferenceFrag;
    private FragmentManager mFragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Intent intent = getIntent();

        mRealm = Realm.getInstance(UtilityManager.getRealmConfig());
        mAquery = new AQuery(this);
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mKeyboardManager = new KeyboardManager(this);

        //각 아이디 가져오기
        mHid = HOSPITAL_ID;
        mRid = intent.getExtras().getString("rid");
        mUid = Config.getMyUID(mRealm);

        // rid 를 사용하여 채팅 내용과 채팅방 이름을 불러옴
        mChatRoom = mRealm.where(ChatRoom.class).equalTo("rid", mRid).findFirst();
        String chatRoomTitle = mChatRoom.getRoomName();

        // 채팅방 툴바 설정
        mToolbar = findViewById(R.id.toolbar_chat_room);
        setSupportActionBar(mToolbar);

        mActionbar = getSupportActionBar();
        mActionbar.setDisplayShowTitleEnabled(false);
        mActionbar.setDisplayHomeAsUpEnabled(true);
        mActionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mToolbarTitle = (TextView) findViewById(R.id.toolbar_chat_room_title);
        mToolbarTitle.setText(chatRoomTitle);

        // rid 참조하여 채팅내용 불러옴
        mChatContents = mRealm.where(ChatContent.class).equalTo("rid", mRid).findAll();
        mChatContents = mChatContents.sort("sendDate", Sort.ASCENDING);
        mChatContentsIndex = setChatContentRead(mChatContents, 0);

        // 하단 미디어 버튼, 에디트텍스트 , 전송 버튼을 아이디로 불러옴
        mEditChat = (EditText) findViewById(R.id.edit_chat);
        mSendButton = (IconButton) findViewById(R.id.send_button);

        // 버튼 아이콘 설정
        mSendButton.setText(ICON_SEND_CHAT);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendChat();
            }
        });

        //tablet fragment 관리
        if (UtilityManager.isTablet(this)) {
            mPacsReferenceFrag = new PACSReferenceFragment();
            mFragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.chat_room_pacs_layout, mPacsReferenceFrag).commit();
        }

        // 리사이클러 뷰와 어댑터를 연결 채팅을 불러 올수 있음
        mRecyclerView = (RecyclerView) findViewById(R.id.chat_room_content);
        mAdapter = new ChatAdapter(this, mChatContents, mFragmentManager);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);

        // Drawer navigation 설정
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        mHeader = mNavigationView.getHeaderView(0);
        mDrawerTitle = (TextView) mHeader.findViewById(R.id.drawer_title);
        mDrawerTitle.setText(chatRoomTitle);

        // pacs 데이터를 인텐트로 받은 상태로 채팅방 입장시
        String studyId = getIntent().getStringExtra("studyId");
        String description = getIntent().getStringExtra("description");

        if (UtilityManager.checkString(studyId)) {
            sendPacs(studyId, description);
        }

        RealmChangeListener<RealmResults<ChatContent>> realmChangeListener = new RealmChangeListener<RealmResults<ChatContent>>() {
            @Override
            public void onChange(RealmResults<ChatContent> chatContents) {
                mAdapter.update();
                if (mAdapter.getItemCount() > 0)
                    mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
            }
        };
        mChatContents.addChangeListener(realmChangeListener);

        addContentView(mKeyboardManager, new FrameLayout.LayoutParams(-1, -1));
        mKeyboardManager.setOnShownKeyboard(new KeyboardManager.OnShownKeyboardListener() {
            @Override
            public void onShowSoftKeyboard() {
                mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
            }
        });
        mKeyboardManager.setOnHiddenKeyboard(new KeyboardManager.OnHiddenKeyboardListener() {
            @Override
            public void onHiddenSoftKeyboard() {
                mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        mRid = intent.getExtras().getString("rid");

        mChatRoom = mRealm.where(ChatRoom.class).equalTo("rid", mRid).findFirst();
        String chatRoomTitle = mChatRoom.getRoomName();
        mToolbar = findViewById(R.id.toolbar_chat_room);
        setSupportActionBar(mToolbar);

        mActionbar = getSupportActionBar();
        mActionbar.setDisplayShowTitleEnabled(false);
        mActionbar.setDisplayHomeAsUpEnabled(true);
        mActionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mToolbarTitle = (TextView) findViewById(R.id.toolbar_chat_room_title);
        mToolbarTitle.setText(chatRoomTitle);
        mChatContents = mRealm.where(ChatContent.class).equalTo("rid", mRid).findAll();
        mChatContents = mChatContents.sort("sendDate", Sort.ASCENDING);
        mChatContentsIndex = setChatContentRead(mChatContents, 0);
        mAdapter = new ChatAdapter(this, mChatContents, mFragmentManager);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
        mHeader = mNavigationView.getHeaderView(0);
        mDrawerTitle = (TextView) mHeader.findViewById(R.id.drawer_title);
        mDrawerTitle.setText(chatRoomTitle);
    }

    @Override // Back pressed
    public void onBackPressed() {
        super.onBackPressed();
        setChatContentRead(mChatContents, mChatContentsIndex);
//         if (mRealm != null) {
//            mRealm.removeAllChangeListeners();
//            mRealm.close();
//            mRealm = null;
//        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("requestChatList", RESULT_OK);

        startActivity(intent);
    }

    @Override // Destroy
    protected void onDestroy() {
        super.onDestroy();
        setChatContentRead(mChatContents, mChatContentsIndex);
        if (mRealm != null) {
            mRealm.removeAllChangeListeners();
            mRealm.close();
            mRealm = null;
        }
    }

    @Override // Option Item Selected Listener
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                setNavigationView();
                mInputMethodManager.hideSoftInputFromWindow(mEditChat.getWindowToken(), 0);
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override // Navigation Item Selected Listener
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Menu menu = mNavigationView.getMenu();

        switch (menuItem.getItemId()) {
            case R.id.nav_camera:
                mDrawerLayout.closeDrawers();
                openCamera();
                break;

            case R.id.nav_gallery:
                mDrawerLayout.closeDrawers();
                openAlbum();
                break;

            case R.id.nav_pacs:
                mDrawerLayout.closeDrawers();
                openPacs();
                break;

            case R.id.nav_setting_fix_top:
                SwitchCompat fixTopSwitch = (SwitchCompat) MenuItemCompat
                        .getActionView(menu.findItem(R.id.nav_setting_fix_top))
                        .findViewById(R.id.drawer_switch);
                fixTopSwitch.toggle();
                break;

            case R.id.nav_setting_alarm:
                SwitchCompat alarmSwitch = (SwitchCompat) MenuItemCompat
                        .getActionView(menu.findItem(R.id.nav_setting_alarm))
                        .findViewById(R.id.drawer_switch);
                alarmSwitch.toggle();
                break;

            case R.id.nav_rename_chat_room:
                mDrawerLayout.closeDrawers();
                renameChatRoom();
                break;


            case R.id.nav_add_member:
                mDrawerLayout.closeDrawers();
                addMember();
                break;

            case R.id.nav_exit:
                exitRoom();
                break;
        }
        return true;
    }

    @Override // Send album or camera image
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri file = data.getData();
                Date date = new Date();

                //realm 에 사진 채팅 추가
                ChatRoom chatRoom = mRealm.where(ChatRoom.class).equalTo("rid", mRid).findFirst();

                Map<String, Object> chat = new HashMap<>();
                String cid = mUid.concat(String.valueOf(date.getTime())); //cid는 자신의 userId + 시간 으로 설정
                chat.put("hid", mHid);
                chat.put("rid", mRid);
                chat.put("uid", mUid);
                chat.put("cid", cid);
                chat.put("content", EMPTY_IMAGE);
                chat.put("type", "1");

                //채팅방이 realm에만 생성되있는 경우, firestore 서버 에도 채팅방 생성한 다음 채팅메세지 서버에 추가
                if (mRealm.where(ChatContent.class).equalTo("rid", mRid).findAll().isEmpty()) {
                    //realm 채팅 생성
                    ChatContent.createChat(mRealm, chat);
                    Map<String, Object> chatRoomData = new HashMap<>();
                    RealmResults<ChatRoomMember> chatRoomMember = ChatRoom.getChatRoomUsers(mRealm, mRid);
                    JSONArray chatRoomMemberJsonArray = new JSONArray();
                    for (ChatRoomMember member : chatRoomMember) {
                        chatRoomMemberJsonArray.put(member.getUid());
                    }
                    chatRoomData.put("hid", mHid);
                    chatRoomData.put("rid", mRid);
                    chatRoomData.put("uid", mUid);
                    chatRoomData.put("title", chatRoom.getRoomName());
                    chatRoomData.put("members", chatRoomMemberJsonArray);
                    chatRoomData.put("roomImgUrl", chatRoom.getRoomImg());
                    chatRoomData.put("notificationId", chatRoom.getNotificationId());
                    FirebaseFunctionsManager.createChatRoom(chatRoomData).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                        @Override
                        public void onSuccess(HttpsCallableResult httpsCallableResult) {
                            UploadTask uploadTask = FirebaseStorageManager.uploadFile(file, PATH_STORAGE1 + mHid + PATH_STORAGE3 + mRid + "/" + cid + "_" + mUid);
                        }
                    });
                } else {
                    //realm 채팅 생성
                    ChatContent.createChat(mRealm, chat);
                    UploadTask uploadTask = FirebaseStorageManager.uploadFile(file, PATH_STORAGE1 + mHid + PATH_STORAGE3 + mRid + "/" + cid + "_" + mUid);
                }
            } else if (requestCode == 2) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                Bitmap file = BitmapFactory.decodeFile(mFile.getAbsolutePath(), options);
                Date date = new Date();

                //realm 에 사진 채팅 추가
                ChatRoom chatRoom = mRealm.where(ChatRoom.class).equalTo("rid", mRid).findFirst();

                Map<String, Object> chat = new HashMap<>();
                String cid = mUid.concat(String.valueOf(date.getTime())); //cid는 자신의 userId + 시간 으로 설정
                chat.put("hid", mHid);
                chat.put("rid", mRid);
                chat.put("uid", mUid);
                chat.put("cid", cid);
                chat.put("content", EMPTY_IMAGE);
                chat.put("type", "1");

                if (mRealm.where(ChatContent.class).equalTo("rid", mRid).findAll().isEmpty()) {
                    //realm 채팅 생성
                    ChatContent.createChat(mRealm, chat);
                    Map<String, Object> chatRoomData = new HashMap<>();
                    RealmResults<ChatRoomMember> chatRoomMember = ChatRoom.getChatRoomUsers(mRealm, mRid);
                    JSONArray chatRoomMemberJsonArray = new JSONArray();
                    for (ChatRoomMember member : chatRoomMember) {
                        chatRoomMemberJsonArray.put(member.getUid());
                    }
                    chatRoomData.put("hid", mHid);
                    chatRoomData.put("rid", mRid);
                    chatRoomData.put("uid", mUid);
                    chatRoomData.put("title", chatRoom.getRoomName());
                    chatRoomData.put("members", chatRoomMemberJsonArray);
                    chatRoomData.put("roomImgUrl", chatRoom.getRoomImg());
                    chatRoomData.put("notificationId", chatRoom.getNotificationId());
                    FirebaseFunctionsManager.createChatRoom(chatRoomData).addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                        @Override
                        public void onSuccess(HttpsCallableResult httpsCallableResult) {
                            UploadTask uploadTask = FirebaseStorageManager.uploadFile(file, PATH_STORAGE1 + mHid + PATH_STORAGE3 + mRid + "/" + cid + "_" + mUid);
                        }
                    });
                } else {
                    //realm 채팅 생성
                    ChatContent.createChat(mRealm, chat);
                    UploadTask uploadTask = FirebaseStorageManager.uploadFile(file, PATH_STORAGE1 + mHid + PATH_STORAGE3 + mRid + "/" + cid + "_" + mUid);
                }
            }
        }
    }

    // Drawer Navigation setting
    private void setNavigationView() {
        Menu menu = mNavigationView.getMenu();
        MenuItem item = menu.findItem(R.id.menu_chat_member);
        SubMenu subMenu = item.getSubMenu();


        ChatRoom chatRoom = mRealm.where(ChatRoom.class).equalTo("rid", mRid).findFirst();
        RealmResults<ChatRoomMember> chatRoomMembers = mRealm.where(ChatRoomMember.class).equalTo("rid", mRid).findAll();
        RealmResults<ChatRoomMember> users = ChatRoom.getChatRoomUsers(mRealm, mRid);


        item.setTitle("채팅방 멤버 " + users.size());

        subMenu.clear();
        int menuId = 0;
        for (ChatRoomMember member : chatRoomMembers) {
            String userName = mRealm.where(User.class).equalTo("uid", member.getUid()).findFirst().getAppName();
            subMenu.add(0, menuId++, 0, userName);
        }
        SwitchCompat fixTopSwitch = (SwitchCompat) MenuItemCompat.getActionView(menu.findItem(R.id.nav_setting_fix_top)).findViewById(R.id.drawer_switch);
        SwitchCompat alarmSwitch = (SwitchCompat) MenuItemCompat.getActionView(menu.findItem(R.id.nav_setting_alarm)).findViewById(R.id.drawer_switch);

        if (chatRoom.getSettingFixTop()) {
            fixTopSwitch.setChecked(true);
        } else {
            fixTopSwitch.setChecked(false);
        }
        if (chatRoom.getSettingAlarm()) {
            alarmSwitch.setChecked(true);
        } else {
            alarmSwitch.setChecked(false);
        }

        fixTopSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            chatRoom.setSettingFixTop(true);
                            mAquery.toast(MSG_FIX_TOP_ON);
                            realm.copyToRealmOrUpdate(chatRoom);
                        }
                    });
                } else {
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            chatRoom.setSettingFixTop(false);
                            mAquery.toast(MSG_FIX_TOP_OFF);
                            realm.copyToRealmOrUpdate(chatRoom);
                        }
                    });
                }
            }
        });
        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            chatRoom.setSettingAlarm(true);
                            mAquery.toast(MSG_ALARM_ON);
                            realm.copyToRealmOrUpdate(chatRoom);
                        }
                    });
                } else {
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            chatRoom.setSettingAlarm(false);
                            mAquery.toast(MSG_ALARM_OFF);
                            realm.copyToRealmOrUpdate(chatRoom);
                        }
                    });
                }
            }
        });

    }

    // Open Album -> onActivityResult()
    private void openAlbum() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            openAlbum();
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(PATH_IMAGE);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            if (intent.resolveActivity(this.getPackageManager()) != null) {
                startActivityForResult(intent, 1);
            }
        }
    }

    // Open Camera -> onActivityResult()
    private void openCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            Date date = new Date();
            mFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM) + "/camera", date.toString() + ".jpg");
            Uri uri = FileProvider.getUriForFile(getBaseContext(), "nubextalk.fileprovider", mFile);
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, 2);
        }
    }

    // Open Pacs  -> ImageViewActivity
    private void openPacs() {
        Intent intent = new Intent(getApplicationContext(), ImageViewActivity.class);
        intent.putExtra("rid", mRid);
        startActivity(intent);
    }

    // Invite new member -> ChatAddActivity
    private void addMember() {
        Intent intent = new Intent(getApplicationContext(), ChatAddActivity.class);
        intent.putExtra("rid", mRid);
        startActivity(intent);
    }

    // Send new chat
    private void sendChat() {
        String content;
        if ((content = String.valueOf(mEditChat.getText())).equals("")) {
            mAquery.toast(MSG_EMPTY_CONTENT);
        } else {
            Date date = new Date();
            ChatRoom chatRoom = mRealm.where(ChatRoom.class).equalTo("rid", mRid).findFirst();

            Map<String, Object> chat = new HashMap<>();
            String cid = mUid.concat(String.valueOf(date.getTime())); //cid는 자신의 userId + 시간 으로 설정
            chat.put("hid", mHid);
            chat.put("rid", mRid);
            chat.put("uid", mUid);
            chat.put("cid", cid);
            chat.put("content", content);
            chat.put("type", "0");

            //채팅방이 realm에만 생성되있는 경우, firestore 서버 에도 채팅방 생성한 다음 채팅메세지 서버에 추가
            if (mRealm.where(ChatContent.class).equalTo("rid", mRid).findAll().isEmpty()) {
                //realm 채팅 생성
                ChatContent.createChat(mRealm, chat);
                Map<String, Object> chatRoomData = new HashMap<>();
                RealmResults<ChatRoomMember> chatRoomMember = ChatRoom.getChatRoomUsers(mRealm, mRid);
                JSONArray chatRoomMemberJsonArray = new JSONArray();
                for (ChatRoomMember member : chatRoomMember) {
                    chatRoomMemberJsonArray.put(member.getUid());
                }
                chatRoomData.put("hid", mHid);
                chatRoomData.put("rid", mRid);
                chatRoomData.put("uid", mUid);
                chatRoomData.put("title", chatRoom.getRoomName());
                chatRoomData.put("members", chatRoomMemberJsonArray);
                chatRoomData.put("roomImgUrl", chatRoom.getRoomImg());
                chatRoomData.put("notificationId", chatRoom.getNotificationId());
                FirebaseFunctionsManager.createChatRoom(chatRoomData)
                        .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                            @Override
                            public void onSuccess(HttpsCallableResult httpsCallableResult) {
                                FirebaseFunctionsManager.createChat(chat);
                            }
                        });
            } else {
                //realm 채팅 생성
                ChatContent.createChat(mRealm, chat);
                FirebaseFunctionsManager.createChat(chat);
            }
            mEditChat.setText(EMPTY);
        }
    }

    // Send new pacs
    private void sendPacs(String studyId, String description) {
        Date date = new Date();
        ChatRoom chatRoom = mRealm.where(ChatRoom.class).equalTo("rid", mRid).findFirst();

        Map<String, Object> chat = new HashMap<>();
        String cid = mUid.concat(String.valueOf(date.getTime())); //cid는 자신의 userId + 시간 으로 설정
        chat.put("hid", mHid);
        chat.put("rid", mRid);
        chat.put("uid", mUid);
        chat.put("cid", cid);
        chat.put("ext1", studyId);
        chat.put("content", description);
        chat.put("type", "2");

        //채팅방이 realm에만 생성되있는 경우, firestore 서버 에도 채팅방 생성한 다음 채팅메세지 서버에 추가
        if (mRealm.where(ChatContent.class).equalTo("rid", mRid).findAll().isEmpty()) {
            //realm 채팅 생성
            ChatContent.createChat(mRealm, chat);
            Map<String, Object> chatRoomData = new HashMap<>();
            RealmResults<ChatRoomMember> chatRoomMember = ChatRoom.getChatRoomUsers(mRealm, mRid);
            JSONArray chatRoomMemberJsonArray = new JSONArray();
            for (ChatRoomMember member : chatRoomMember) {
                chatRoomMemberJsonArray.put(member.getUid());
            }
            chatRoomData.put("hid", mHid);
            chatRoomData.put("uid", mUid);
            chatRoomData.put("rid", mRid);
            chatRoomData.put("title", chatRoom.getRoomName());
            chatRoomData.put("members", chatRoomMemberJsonArray);
            chatRoomData.put("roomImgUrl", chatRoom.getRoomImg());
            chatRoomData.put("notificationId", chatRoom.getNotificationId());
            FirebaseFunctionsManager.createChatRoom(chatRoomData)
                    .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                        @Override
                        public void onSuccess(HttpsCallableResult httpsCallableResult) {
                            FirebaseFunctionsManager.createChat(chat);
                        }
                    });
        } else {
            //realm 채팅 생성
            ChatContent.createChat(mRealm, chat);
            FirebaseFunctionsManager.createChat(chat);
        }

    }

    public void sendPacs(String studyId, String description, Realm realm) {
        Date date = new Date();
        ChatRoom chatRoom = realm.where(ChatRoom.class).equalTo("rid", mRid).findFirst();

        Map<String, Object> chat = new HashMap<>();
        String cid = mUid.concat(String.valueOf(date.getTime())); //cid는 자신의 userId + 시간 으로 설정
        chat.put("hid", mHid);
        chat.put("rid", mRid);
        chat.put("uid", mUid);
        chat.put("cid", cid);
        chat.put("content", description);
        chat.put("type", "2");
        chat.put("ext1", studyId);

        //채팅방이 realm에만 생성되있는 경우, firestore 서버 에도 채팅방 생성한 다음 채팅메세지 서버에 추가
        if (realm.where(ChatContent.class).equalTo("rid", mRid).findAll().isEmpty()) {
            //realm 채팅 생성
            ChatContent.createChat(realm, chat);
            Map<String, Object> chatRoomData = new HashMap<>();
            RealmResults<ChatRoomMember> chatRoomMember = ChatRoom.getChatRoomUsers(realm, mRid);
            JSONArray chatRoomMemberJsonArray = new JSONArray();
            for (ChatRoomMember member : chatRoomMember) {
                chatRoomMemberJsonArray.put(member.getUid());
            }
            chatRoomData.put("hid", mHid);
            chatRoomData.put("rid", mRid);
            chatRoomData.put("uid", mUid);
            chatRoomData.put("title", chatRoom.getRoomName());
            chatRoomData.put("members", chatRoomMemberJsonArray);
            chatRoomData.put("roomImgUrl", chatRoom.getRoomImg());
            chatRoomData.put("notificationId", chatRoom.getNotificationId());
            FirebaseFunctionsManager.createChatRoom(chatRoomData)
                    .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                        @Override
                        public void onSuccess(HttpsCallableResult httpsCallableResult) {
                            FirebaseFunctionsManager.createChat(chat);
                        }
                    });
        } else {
            //realm 채팅 생성
            ChatContent.createChat(realm, chat);
            FirebaseFunctionsManager.createChat(chat);
        }

    }

    // Exit chat room
    private void exitRoom() {
        ChatRoom.deleteChatRoom(mRealm, mRid);
        onBackPressed();
    }

    // Read Chat
    private int setChatContentRead(RealmResults<ChatContent> Chats, int start) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (int i = start; i < Chats.size(); i++) {
                    ChatContent chatContent = Chats.get(i);
                    if (!chatContent.getIsRead()) {
                        chatContent.setIsRead(true);
                        realm.copyToRealmOrUpdate(chatContent);
                    }
                }
            }
        });
        return Chats.size();
    }

    // Rename ChatRoom
    private void renameChatRoom() {
        RoomNameModificationDialogFragment fragment = new RoomNameModificationDialogFragment(mRealm, mRid);
        fragment.setNameModifiedListener(new RoomNameModificationDialogFragment.NameModifiedListener() {
            @Override
            public void onChange(String name) {
                mToolbarTitle.setText(name);
                mDrawerTitle.setText(name);
                mAquery.toast("채팅방 이름이 변경 되었습니다.");
            }
        });
        fragment.show(getSupportFragmentManager(), "Change RoomName");
    }
}

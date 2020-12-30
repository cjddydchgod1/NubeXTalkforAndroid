package x.com.nubextalk;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aquery.AQuery;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.joanzapata.iconify.widget.IconButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Module.Adapter.ChatAdapter;

//채팅방 액티비티
public class ChatRoomActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{
    private static Realm realm;
    private static AQuery aq;
    private static InputMethodManager imm;

    private static RealmResults<ChatContent> mChat;
    private static RecyclerView mRecyclerView;
    private static ChatAdapter mAdapter;
    private static DrawerLayout mDrawerLayout;
    private static NavigationView mNavigationView;

    private static EditText mEditChat;
    private static IconButton mSendButton;
//    private static IconButton mMediaButton;
//    private static LinearLayout mMediaMenu;

    private static String mRoomId;
    private static Date mRoomUpdateDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        realm = Realm.getInstance(UtilityManager.getRealmConfig());
        aq = new AQuery(this);
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);


        // rid 를 사용하여 채팅 내용과 채팅방 이름을 불러옴
        Intent intent = getIntent();
        mRoomId = intent.getExtras().getString("rid");
        ChatRoom roomInfo = realm.where(ChatRoom.class).equalTo("rid",mRoomId).findFirst();
        String roomTitle = roomInfo.getRoomName();
        mRoomUpdateDate = roomInfo.getUpdatedDate();

        // 채팅방 툴바 설정
        Toolbar toolbar_chat_room = findViewById(R.id.toolbar_chat_room);
        setSupportActionBar(toolbar_chat_room);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        TextView title = (TextView)findViewById(R.id.toolbar_chat_room_title);
        title.setText(roomTitle);

        // rid 참조하여 채팅내용 불러옴
        mChat = realm.where(ChatContent.class).equalTo("rid", mRoomId).findAll();
        setChatContentRead(mChat);

        // 하단 미디어 버튼, 에디트텍스트 , 전송 버튼을 아이디로 불러옴
        mEditChat = (EditText)findViewById(R.id.edit_chat);
        mSendButton = (IconButton)findViewById(R.id.send_button);
//        mMediaButton = (IconButton)findViewById(R.id.media_button);

        // 버튼 아이콘 설정
        mSendButton.setText("{far-paper-plane 30dp #747475}");
//        mMediaButton.setText("{far-image 35dp #747475}");

        // on click listener 설정
        mSendButton.setOnClickListener(this);
//        mMediaButton.setOnClickListener(this);

        // 리사이클러 뷰와 어댑터를 연결 채팅을 불러 올수 있음
        mRecyclerView = (RecyclerView)findViewById(R.id.chat_room_content);
        mAdapter = new ChatAdapter(this, mChat);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);

        // Drawer navigation 설정
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        setNavigationView(roomInfo);
        View header = mNavigationView.getHeaderView(0);
        TextView drawerTitle = (TextView) header.findViewById(R.id.drawer_title);
        drawerTitle.setText(roomTitle);
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
    public void onBackPressed(){
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
        switch (v.getId()){
            case R.id.send_button:
                sendChat(v);
                break;

//            case R.id.chat_room_content:
//                imm.hideSoftInputFromWindow(mEditChat.getWindowToken(), 0);
//                break;

//            case R.id.media_button:
//                openMediaMenu(v);
//                break;
//            case R.id.gallery_button:
//                openAlbum();
//                ((ViewManager)mMediaMenu.getParent()).removeView(mMediaMenu);
//                break;
//            case R.id.camera_button:
//                openCamera();
//                ((ViewManager)mMediaMenu.getParent()).removeView(mMediaMenu);
//                break;
//            case R.id.pacs_button:
//                openPacs();
//                ((ViewManager)mMediaMenu.getParent()).removeView(mMediaMenu);
//                break;
//            case R.id.media_menu_cancel:
//                ((ViewManager)mMediaMenu.getParent()).removeView(mMediaMenu);
//                break;
        }
    }
    @Override // Option Item Selected Listener
    public boolean onOptionsItemSelected(MenuItem item){
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
            case R.id.nav_camera :
                openCamera();
                break;

            case R.id.nav_gallery :
                openAlbum();
                break;

            case R.id.nav_pacs :
                openPacs();
                break;

            case  R.id.nav_setting_fix_top :
                fixTopSwitch.toggle();
                break;

            case R.id.nav_setting_alarm :
                alarmSwitch.toggle();
                break;

            case R.id.nav_exit :
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
            try {
                Uri uri = data.getData();
                realm.executeTransaction(new Realm.Transaction() {
//                    @Override
//                    public void execute(Realm realm) {
//                            Date date = new Date();
//                            ChatContent chat = new ChatContent();
//                            chat.setCid();
//                            chat.setUid("1234");
//                            chat.setRid(mRoomId);
//                            chat.setType(1);
//                            chat.setContent(uri.toString());
//                            chat.setSendDate(date);
//                            realm.copyToRealmOrUpdate(chat);
//
//                            mChat = realm.where(ChatContent.class).equalTo("rid", mRoomId).findAll();
//                            mAdapter.update(mChat);
//
//                            mRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
//
//                    }
                    @Override
                    public void execute(Realm realm) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                            Date date = new Date();

                            // 채팅목록 최신순 정렬을 위해 ChatRoom updatedDate 갱신
                            ChatRoom roomInfo = realm.where(ChatRoom.class).equalTo("rid",mRoomId).findFirst();
                            mRoomUpdateDate = roomInfo.getUpdatedDate();
                            roomInfo.setUpdatedDate(date);
                            realm.copyToRealmOrUpdate(roomInfo);

                            String sDay1 = sdf.format(mRoomUpdateDate);
                            String sDay2 = sdf.format(date);

                            Date day1 = null;
                            Date day2 = null;
                            try {
                                day1 = sdf.parse(sDay1);
                                day2 = sdf.parse(sDay2);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            ChatContent chat = new ChatContent();
                            chat.setCid(); // Content ID 자동으로 유니크한 값 설정
                            chat.setUid("1234"); // UID 보내는 사람
                            chat.setRid(mRoomId); // RID 채팅방 아이디
                            chat.setType(1);
                            chat.setContent(uri.toString());
                            chat.setIsRead(true);
                            chat.setSendDate(date);
                            if(day2.equals(day1)){
                                chat.setFirst(false);
                            }
                            Log.d("day1",sDay1);
                            Log.d("day2",sDay2);
                            realm.copyToRealmOrUpdate(chat);
                            mRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
                        }

                });
            } catch (Exception e) {
                    e.printStackTrace();
            }
        }
        else if(requestCode == 2 && resultCode == RESULT_OK){
            try {
                Uri uri = data.getData();
                realm.executeTransaction(new Realm.Transaction() {
//                    @Override
//                    public void execute(Realm realm) {
//                            Date date = new Date();
//                            ChatContent chat = new ChatContent();
//                            chat.setCid();
//                            chat.setUid("1234");
//                            chat.setRid(mRoomId);
//                            chat.setType(1);
//                            chat.setContent(uri.toString());
//                            chat.setSendDate(date);
//                            realm.copyToRealmOrUpdate(chat);
//                            mAdapter.notifyDataSetChanged();
//                    }
                    @Override
                    public void execute(Realm realm) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                            Date date = new Date();

                            // 채팅목록 최신순 정렬을 위해 ChatRoom updatedDate 갱신
                            ChatRoom roomInfo = realm.where(ChatRoom.class).equalTo("rid",mRoomId).findFirst();
                            mRoomUpdateDate = roomInfo.getUpdatedDate();
                            roomInfo.setUpdatedDate(date);
                            realm.copyToRealmOrUpdate(roomInfo);

                            String sDay1 = sdf.format(mRoomUpdateDate);
                            String sDay2 = sdf.format(date);

                            Date day1 = null;
                            Date day2 = null;
                            try {
                                day1 = sdf.parse(sDay1);
                                day2 = sdf.parse(sDay2);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            ChatContent chat = new ChatContent();
                            chat.setCid(); // Content ID 자동으로 유니크한 값 설정
                            chat.setUid("1234"); // UID 보내는 사람
                            chat.setRid(mRoomId); // RID 채팅방 아이디
                            chat.setType(1);
                            chat.setContent(uri.toString());
                            chat.setIsRead(true);
                            chat.setSendDate(date);
                            realm.copyToRealmOrUpdate(chat);
                            mRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
                        }
                });
            } catch (Exception e) {
                    e.printStackTrace();
            }

        }
    }

    private void setNavigationView(ChatRoom roomInfo){
         Menu menu = mNavigationView.getMenu();
         SwitchCompat fixTopSwitch = (SwitchCompat) MenuItemCompat.getActionView(menu.findItem(R.id.nav_setting_fix_top)).findViewById(R.id.drawer_switch);
         SwitchCompat alarmSwitch = (SwitchCompat) MenuItemCompat.getActionView(menu.findItem(R.id.nav_setting_alarm)).findViewById(R.id.drawer_switch);

         if(roomInfo.getSettingFixTop()){
             fixTopSwitch.setChecked(true);
         }
         else{
             fixTopSwitch.setChecked(false);
         }
         if(roomInfo.getSettingAlarm()){
             alarmSwitch.setChecked(true);
         }
         else {
             alarmSwitch.setChecked(false);
         }

         fixTopSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    roomInfo.setSettingFixTop(true);
                                    Toast.makeText(getApplicationContext(),"상단고정이 설정 되었습니다.",Toast.LENGTH_SHORT).show();
                                    realm.copyToRealmOrUpdate(roomInfo);
                                }
                            });
                        }
                        else{
                            Log.e("fixTOp","please");
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    roomInfo.setSettingFixTop(false);
                                    Toast.makeText(getApplicationContext(),"상단고정이 해제 되었습니다.",Toast.LENGTH_SHORT).show();
                                    realm.copyToRealmOrUpdate(roomInfo);
                                }
                            });
                        }
                    }
         });
         alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 if(isChecked){
                     realm.executeTransaction(new Realm.Transaction() {
                         @Override
                         public void execute(Realm realm) {
                             roomInfo.setSettingAlarm(true);
                             Toast.makeText(getApplicationContext(),"알람기능이 설정 되었습니다.",Toast.LENGTH_SHORT).show();
                             realm.copyToRealmOrUpdate(roomInfo);
                         }
                     });
                 }
                 else{
                     realm.executeTransaction(new Realm.Transaction() {
                         @Override
                         public void execute(Realm realm) {
                             roomInfo.setSettingAlarm(false);
                             Toast.makeText(getApplicationContext(),"알람기능이 해제 되었습니다.",Toast.LENGTH_SHORT).show();
                             realm.copyToRealmOrUpdate(roomInfo);
                         }
                     });
                 }
             }
         });

    }

//    private void openMediaMenu(View view) {
//        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mMediaMenu = (LinearLayout)inflater.inflate(R.layout.activity_chat_room_media, null);
//        mMediaMenu.setBackgroundColor(Color.parseColor("#99000000"));
//        addContentView(mMediaMenu, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT ));
//
//        IconButton GalleryButton = findViewById(R.id.gallery_button);
//        IconButton CameraButton = findViewById(R.id.camera_button);
//        IconButton PacsButton = findViewById(R.id.pacs_button);
//        TextView Cancel = findViewById(R.id.media_menu_cancel);
//
//        GalleryButton.setOnClickListener(this);
//        CameraButton.setOnClickListener(this);
//        PacsButton.setOnClickListener(this);
//        Cancel.setOnClickListener(this);
//
//        GalleryButton.setText("{fas-images 70dp #FFFFFF}");
//        CameraButton.setText("{fas-camera-retro 70dp #FFFFFF}");
//        PacsButton.setText("{fas-puzzle-piece 70dp #FFFFFF}");
//    }

    private void openAlbum() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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

    private void openCamera(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, 2);
            }
        }
    }

    private void openPacs(){
    }

    private void sendChat(View view) {
        realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        String content;
                        if( (content = String.valueOf(mEditChat.getText())).equals("")){
                            aq.toast("메세지를 입력하세요");
                        }
                        else {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                            Date date = new Date();

                            // 채팅목록 최신순 정렬을 위해 ChatRoom updatedDate 갱신
                            ChatRoom roomInfo = realm.where(ChatRoom.class).equalTo("rid",mRoomId).findFirst();
                            mRoomUpdateDate = roomInfo.getUpdatedDate();
                            roomInfo.setUpdatedDate(date);
                            realm.copyToRealmOrUpdate(roomInfo);

                            String sDay1 = sdf.format(mRoomUpdateDate);
                            String sDay2 = sdf.format(date);

                            Date day1 = null;
                            Date day2 = null;
                            try {
                                day1 = sdf.parse(sDay1);
                                day2 = sdf.parse(sDay2);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            ChatContent chat = new ChatContent();
                            chat.setCid(); // Content ID 자동으로 유니크한 값 설정
                            chat.setUid("1234"); // UID 보내는 사람
                            chat.setRid(mRoomId); // RID 채팅방 아이디
                            chat.setType(0);
                            chat.setContent(content);
                            chat.setIsRead(true);
                            chat.setSendDate(date);
                            if(day2.equals(day1)){
                                chat.setFirst(false);
                            }
                            realm.copyToRealmOrUpdate(chat);

                        }
                        mRecyclerView.scrollToPosition(mAdapter.getItemCount()-1);
                        mEditChat.setText("");
                    }
        });

    }

    private void exitRoom(){
        ChatRoom roomInfo = realm.where(ChatRoom.class).equalTo("rid",mRoomId).findFirst();
        RealmResults<ChatContent> chatInfo = realm.where(ChatContent.class)
                        .equalTo("rid",mRoomId)
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
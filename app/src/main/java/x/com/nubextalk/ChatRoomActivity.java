package x.com.nubextalk;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aquery.AQuery;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.joanzapata.iconify.widget.IconButton;

import java.net.URI;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Module.Adapter.ChatAdapter;

//채팅방 액티비티
public class ChatRoomActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{
    private static AQuery aq;
    private static Realm realm;

    private static RealmResults<ChatContent> mChat;
    private static RecyclerView mRecyclerView;
    private static RecyclerView.Adapter mAdapter;
    private static DrawerLayout mDrawerLayout;

    private static EditText mEditChat;
    private static IconButton mSendButton;
    private static IconButton mMediaButton;
    private static LinearLayout mMediaMenu;
    private static String mRoomId;
    private static String mRoomTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        realm = Realm.getInstance(UtilityManager.getRealmConfig());
        aq = new AQuery(this);

        // rid 를 사용하여 채팅 내용과 채팅방 이름을 불러옴
        Intent intent = getIntent();
        mRoomId = intent.getExtras().getString("rid");
        ChatRoom RoomInfo = realm.where(ChatRoom.class).equalTo("rid",mRoomId).findFirst();
        mRoomTitle = RoomInfo.getRoomName();

        // 채팅방 툴바 설정
        Toolbar toolbar_chat_room = findViewById(R.id.toolbar_chat_room);
        setSupportActionBar(toolbar_chat_room);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        TextView title = (TextView)findViewById(R.id.toolbar_chat_room_title);
        title.setText(mRoomTitle);

        // rid 참조하여 채팅내용 불러옴
        mChat = realm.where(ChatContent.class).equalTo("rid", mRoomId).findAll();

        // 하단 미디어 버튼, 에디트텍스트 , 전송 버튼을 아이디로 불러옴
        mEditChat = (EditText)findViewById(R.id.edit_chat);
        mSendButton = (IconButton)findViewById(R.id.send_button);
        mMediaButton = (IconButton)findViewById(R.id.media_button);

        // 버튼 아이콘 설정
        mSendButton.setText("{far-paper-plane 35dp #FFFFFF}");
        mMediaButton.setText("{far-image 35dp #FFFFFF}");

        // on click listener 설정
        mSendButton.setOnClickListener(this);
        mMediaButton.setOnClickListener(this);

        // 리사이클러 뷰와 어댑터를 연결 채팅을 불러 올수 있음
        mRecyclerView = (RecyclerView)findViewById(R.id.chat_room_content);
        mAdapter = new ChatAdapter(this, mChat);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Drawer navigation 설정
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView drawerTitle = (TextView) header.findViewById(R.id.drawer_title);
        drawerTitle.setText(mRoomTitle);

        //데이터가 없을 경우 테스트 데이터 넣기 -> 요고는 이제 필요 없어서 지웠음. 이거 지금도 있으면 새로운 채팅방 생성해서 해당 채팅방 들어가면
        // ChatContent.init(this, realm) 이 코드 때문에 다른 채팅방 메세지도 초기화 됨.
//        if(mChat.size() == 0){
//            ChatContent.init(this, realm);
//            mChat = ChatContent.getAll(realm);
//        }
    }

    // 이상하게 onDestroy() 에서는 setResult(10) 작동이 안되서 onBackPressed() 함수에서 씀.
    // 채팅방에서 뒤로갈때(= ChatRoomAcitivity 종료) setResult(10) 를 보내줘서 MainAcitivity 에서 채팅목록 동기화.
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
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_button:
                sendChat(v);
                break;
            case R.id.media_button:
                showMediaMenu(v);
                break;
            case R.id.gallery_button:
                goToAlbum();
                ((ViewManager)mMediaMenu.getParent()).removeView(mMediaMenu);
                break;
            case R.id.camera_button:
                ((ViewManager)mMediaMenu.getParent()).removeView(mMediaMenu);
                break;
            case R.id.pacs_button:
                ((ViewManager)mMediaMenu.getParent()).removeView(mMediaMenu);
                break;
            case R.id.media_menu_cancel:
                ((ViewManager)mMediaMenu.getParent()).removeView(mMediaMenu);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();
        int id = menuItem.getItemId();
        ChatRoom RoomInfo = realm.where(ChatRoom.class).equalTo("rid",mRoomId).findFirst();
        switch (id) {
            case  R.id.setting_fix_top :
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if(RoomInfo.getSettingFixTop()){
                            RoomInfo.setSettingFixTop(false);
                            Toast.makeText(getApplicationContext(),"상단고정이 해제 되었습니다.",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            RoomInfo.setSettingFixTop(true);
                            Toast.makeText(getApplicationContext(),"상단고정이 설정 되었습니다.",Toast.LENGTH_SHORT).show();
                        }
                        realm.copyToRealmOrUpdate(RoomInfo);
                    }
                });
                break;
            case R.id.setting_alarm :
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if(RoomInfo.getSettingAlarm()){
                            RoomInfo.setSettingAlarm(false);

                            Toast.makeText(getApplicationContext(),"알람이 해제 되었습니다.",Toast.LENGTH_SHORT).show();

                        }
                        else{
                            RoomInfo.setSettingAlarm(true);
                            Toast.makeText(getApplicationContext(),"알람이 설정 되었습니다.",Toast.LENGTH_SHORT).show();
                        }
                        realm.copyToRealmOrUpdate(RoomInfo);
                    }
                });
                break;
            case R.id.exit :
                RealmResults<ChatContent> chatInfo = realm.where(ChatContent.class)
                        .equalTo("rid",mRoomId)
                        .findAll();
                realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RoomInfo.deleteFromRealm();
                            realm.copyToRealmOrUpdate(RoomInfo);
                            chatInfo.deleteAllFromRealm();
                            realm.copyToRealmOrUpdate(chatInfo);

                        }
                    });
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    public void showMediaMenu(View view) {
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMediaMenu = (LinearLayout)inflater.inflate(R.layout.view_media_menu, null);
        addContentView(mMediaMenu, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT ));
        IconButton GalleryButton = findViewById(R.id.gallery_button);
        IconButton CameraButton = findViewById(R.id.camera_button);
        IconButton PacsButton = findViewById(R.id.pacs_button);
        TextView Cancel = findViewById(R.id.media_menu_cancel);
        GalleryButton.setOnClickListener(this);
        CameraButton.setOnClickListener(this);
        PacsButton.setOnClickListener(this);
        Cancel.setOnClickListener(this);

        GalleryButton.setText("{fas-images 90dp #FFFFFF}");
        CameraButton.setText("{fas-camera-retro 90dp #FFFFFF}");
        PacsButton.setText("{fas-puzzle-piece 90dp #FFFFFF}");
    }
    // 채팅 메세지를 Chat 클래스를 활용하여 인스턴스를 만들어 리스트에 추가 해줌
    public void sendChat(View view) {
        realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        String content;
                        if( (content = String.valueOf(mEditChat.getText())).equals("")){
                            aq.toast("메세지를 입력하세요");
                        }
                        else {
                            Date date = new Date();

                            ChatContent chat = new ChatContent();
                            chat.setCid(); // Content ID 자동으로 유니크한 값 설정
                            chat.setUid("1234"); // UID 보내는 사람
                            chat.setRid(mRoomId); // RID 채팅방 아이디
                            chat.setType(0);
                            chat.setContent(content);
                            chat.setSendDate(date);
                            realm.copyToRealmOrUpdate(chat);

                            // 채팅목록 최신순 정렬을 위해 ChatRoom updatedDate 갱신
                            ChatRoom chatRoom = realm.where(ChatRoom.class)
                                    .equalTo("rid",mRoomId).findFirst();
                            chatRoom.setUpdatedDate(date);
                            realm.copyToRealmOrUpdate(chatRoom);

                            mAdapter.notifyDataSetChanged();
                        }
                        mEditChat.setText("");
                    }
        });
    }

    private void goToAlbum() {
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri uri = data.getData();
                    realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                            Date date = new Date();

                            ChatContent chat = new ChatContent();
                            chat.setCid();
                            chat.setUid("1234");
                            chat.setRid(mRoomId);
                            chat.setType(1);
                            chat.setContent(uri.toString());
                            chat.setSendDate(date);
                            realm.copyToRealmOrUpdate(chat);
                            mAdapter.notifyDataSetChanged();
                    }
            });
//                    getPicture(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void getPicture(Uri uri) {
        int index = 0;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            Log.d("cursor", "null ");
        }
        else if (cursor.moveToFirst()) {
            index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            String imgPath = cursor.getString(index);
            realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                            Date date = new Date();

                            ChatContent chat = new ChatContent();
                            chat.setCid();
                            chat.setUid("1234");
                            chat.setRid(mRoomId);
                            chat.setType(1);
                            chat.setContent(imgPath);
                            chat.setSendDate(date);
                            realm.copyToRealmOrUpdate(chat);
                            mAdapter.notifyDataSetChanged();
                    }
            });
            Log.d("realPathFromURI", "realPathFromURI: " + imgPath);
            cursor.close();
        } else {
            Log.e("null", "커서가 비어있습니다.");
        }
    }
}
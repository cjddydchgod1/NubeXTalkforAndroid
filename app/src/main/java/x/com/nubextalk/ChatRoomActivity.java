package x.com.nubextalk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.aquery.AQuery;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.widget.IconButton;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Module.Adapter.ChatAdapter;

//채팅방 액티비티
public class ChatRoomActivity extends AppCompatActivity implements View.OnClickListener {
    private static RealmResults<ChatContent> mChat;
    private static RecyclerView mRecyclerView;
    private static RecyclerView.Adapter mAdapter;
    private static EditText mEditChat;
    private static IconButton mSendButton;
    private static IconButton mMediaButton;

    private static AQuery aq;
    private Realm realm;

    private String roomId;
    private String roomTitle;




     @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        realm = Realm.getDefaultInstance();
        aq = new AQuery(this);



        // rid 를 사용하여 채팅 내용과 채팅방 이름을 불러옴
        Intent intent = getIntent();
        roomId = intent.getExtras().getString("rid");
        roomTitle = realm.where(ChatRoom.class).equalTo("rid",roomId).findFirst().getRoomName();


        // 채팅방 툴바 설정
        Toolbar toolbar_chat_room = findViewById(R.id.toolbar_chat_room);
        setSupportActionBar(toolbar_chat_room);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        TextView title = (TextView)findViewById(R.id.toolbar_chat_room_title);
        title.setText(roomTitle);

        // rid 참조하여 채팅내용 불러옴
        mChat = realm.where(ChatContent.class).equalTo("rid", roomId).findAll();

        // 하단 미디어 버튼, 에디트텍스트 , 전송 버튼을 아이디로 불러옴
        mEditChat = (EditText)findViewById(R.id.editChat);
        mSendButton = (IconButton)findViewById(R.id.sendButton);
        mMediaButton = (IconButton)findViewById(R.id.mediaButton);

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


        //데이터가 없을 경우 테스트 데이터 넣기
        if(mChat.size() == 0){
            ChatContent.init(this, realm);
            mChat = ChatContent.getAll(realm);
        }


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
                            chat.setRid(roomId); // RID 채팅방 아이디
                            chat.setType(0);
                            chat.setContent(content);
                            chat.setSendDate(date);
                            realm.copyToRealmOrUpdate(chat);
                            mAdapter.notifyDataSetChanged();
                        }
                        mEditChat.setText("");
                    }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sendButton:
                sendChat(v);
                break;
            case R.id.mediaButton:
                break;
        }
    }
    // 툴바 메뉴 옵션 설정 함수
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }
    // 툴바의 뒤로가기
    @Override
    public boolean onOptionsItemSelected(MenuItem item ){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
package x.com.nubextalk;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
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
import java.util.LinkedList;

//채팅방 액티비티
public class ChatRoomActivity extends AppCompatActivity {
    private final static LinkedList<Chat> mChat = new LinkedList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private static EditText mEditChat;
    private static IconButton mSendButton;
    private static IconButton mMediaButton;

    static AQuery aq;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        // 채팅방 툴바 설정
        Toolbar toolbar_chat_room = findViewById(R.id.toolbar_chat_room);
        setSupportActionBar(toolbar_chat_room);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        TextView title = (TextView)findViewById(R.id.toolbar_chat_room_title);
        title.setText("최재영");


        //Aquery 인스턴스 생성
        aq = new AQuery(this);

        // 하단 미디어 버튼, 에디트텍스트 , 전송 버튼을 아이디로 불러옴
        mEditChat = (EditText)findViewById(R.id.editChat);
        mSendButton = (IconButton)findViewById(R.id.sendButton);
        mMediaButton = (IconButton)findViewById(R.id.mediaButton);

        // 버튼 아이콘 설정
        mSendButton.setText("{far-paper-plane 35dp #FFFFFF}");
        mMediaButton.setText("{far-image 35dp #FFFFFF}");



        // 리사이클러 뷰와 어댑터를 연결 채팅을 불러 올수 있음
        mRecyclerView = (RecyclerView)findViewById(R.id.chat_room_content);
        mAdapter = new ChatAdapter(this, mChat);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        //테스트용 Chat 인스턴스 생성, 채팅방 실행시 데이터를 불러오는 것으로 변경 예정
        mChat.add(new Chat(4321,R.drawable.common_google_signin_btn_icon_dark, "최재영", "주섭아 밥먹었엉???"));


    }
    // 툴바 메뉴 옵션 설정 함수
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }
    // 채팅 메세지를 Chat 클래스를 활용하여 인스턴스를 만들어 리스트에 추가 해줌
    public void sendChat(View view) {
        String chat;
        // 아무 내용도 없을 시 메세지를 입력하라고 띄워줌
        if( (chat= String.valueOf(mEditChat.getText())).equals("")){
            aq.toast("메세지를 입력하세요");
        }
        else{
            mChat.add(new Chat(1234,R.drawable.common_google_signin_btn_icon_dark, "장주섭", chat));
            mAdapter.notifyDataSetChanged();
            mEditChat.setText("");
        }


    }
}
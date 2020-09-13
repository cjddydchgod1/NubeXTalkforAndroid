package x.com.nubextalk;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedList;

public class ChatListActivity extends AppCompatActivity implements View.OnClickListener {

    public class ChatList {
        private int profileUrl;
        private int statusUrl;
        private String strProfileUrl;
        private String strStatusUrl;
        private String name;
        private String msg;
        private String time;
        private String remain;

        public ChatList(int url, String name, String msg, String time, String remain, int statusUrl) {
            this.profileUrl = url;
            this.name = name;
            this.msg = msg;
            this.time = time;
            this.remain = remain;
            this.statusUrl = statusUrl;
        }

        public ChatList(String strUrl, String name, String msg, String time, String remain, String strProfileUrl) {
            this.strProfileUrl = strUrl;
            this.name = name;
            this.msg = msg;
            this.time = time;
            this.remain = remain;
            this.strProfileUrl = strProfileUrl;
        }

        public String getName() {
            return name;
        }

        public String getMsg() {
            return msg;
        }

        public String getTime() {
            return time;
        }

        public String getRemain() {
            return remain;
        }


        public int getProfileUrl() {
            return profileUrl;
        }

        public String getStrStatusUrl() {
            return strStatusUrl;
        }

        public String getStrProfileUrl() {
            return strProfileUrl;
        }

        public int getStatusUrl() {
            return statusUrl;
        }
    }

    private final LinkedList<ChatList> mChatList = new LinkedList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private FloatingActionButton fab_main, fab_sub1, fab_sub2;
    private boolean isFabOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        Toolbar toolbar = findViewById(R.id.chat_list_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        mChatList.addLast(new ChatList(R.drawable.exmaple_profile, "최재영", "잘지내니???", "11:23", "1", R.drawable.oval_status_off));
        mChatList.addLast(new ChatList(R.drawable.example_profile2, "장주섭", "전화 좀 받아봐...", "15:23", "2", R.drawable.oval_status_on));
        mChatList.addLast(new ChatList(R.drawable.example_profile3, "이정규", "내가 미안하다고!", "04:21", "1", R.drawable.oval_status_off));
        mChatList.addLast(new ChatList(R.drawable.exmaple_profile4, "박현준", "잠깐 나와봐. 할 말 있어", "16:53", "1", R.drawable.oval_status_off));



        mRecyclerView = findViewById(R.id.chat_list_view);
        mAdapter = new ChatListAdapter(this, mChatList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fab_main = findViewById(R.id.chat_fab_main);
        fab_sub1 = findViewById(R.id.chat_fab_sub1);
        fab_sub2 = findViewById(R.id.chat_fab_sub2);
        fab_main.setOnClickListener(this);
        fab_sub1.setOnClickListener(this);
        fab_sub2.setOnClickListener(this);


        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_fab_main:
                toggleFab();
                break;

            case R.id.chat_fab_sub1:
                toggleFab();
                Toast.makeText(this, "New chat!", Toast.LENGTH_SHORT).show();
                break;

            case R.id.chat_fab_sub2:
                toggleFab();
                Toast.makeText(this, "Setting!", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    private void toggleFab() {
        if (isFabOpen) {
            fab_main.setImageResource(R.drawable.ic_floating_btn_24);
            fab_sub1.hide();
            fab_sub2.hide();
            isFabOpen = false;
        } else {
            fab_main.setImageResource(R.drawable.ic_baseline_close_24);
            fab_sub1.show();
            fab_sub2.show();
            isFabOpen = true;

        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_search:
//                return true;
//
//            default:
//                // If we got here, the user's action was not recognized.
//                // Invoke the superclass to handle it.
//                return super.onOptionsItemSelected(item);
//
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_list_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) searchItem.getActionView();

        // Configure the search info and add any event listeners...

        return super.onCreateOptionsMenu(menu);
    }


}
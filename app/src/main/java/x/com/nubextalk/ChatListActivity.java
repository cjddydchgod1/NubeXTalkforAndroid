package x.com.nubextalk;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

import io.realm.Realm;
import io.realm.RealmResults;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Module.Adapter.ChatListAdapter;

public class ChatListActivity extends AppCompatActivity implements View.OnClickListener {
    SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final LinkedList<ChatList> mChatList = new LinkedList<>();
    private RecyclerView mRecyclerView;
    private FloatingActionButton fab_main, fab_sub1, fab_sub2;
    private boolean isFabOpen = false;
    private final int On = 1;
    private final int Off = 0;

    private Realm realm;
    private ChatListAdapter mAdapter;

    private RealmResults<ChatRoom> chatRoomResults;
    private RealmResults<ChatContent> chatContentResults;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        Toolbar toolbar = findViewById(R.id.chat_list_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        realm = Realm.getInstance(UtilityManager.getRealmConfig());
        mRecyclerView = findViewById(R.id.chat_list_view);
        chatRoomResults = ChatRoom.getAll(realm);
        chatContentResults = ChatContent.getAll(realm);

        if (chatContentResults.size() == 0) {
            ChatContent.init(this, realm);
        }

        if (chatRoomResults.size() == 0) {
            ChatRoom.init(this, realm);
            chatRoomResults = ChatRoom.getAll(realm);
        }

        mAdapter = new ChatListAdapter(this, chatRoomResults);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_list_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        // Configure the search info and add any event listeners...

        return super.onCreateOptionsMenu(menu);
    }


}
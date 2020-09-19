package x.com.nubextalk;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

public class ChatListActivity extends AppCompatActivity implements View.OnClickListener {
    SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final LinkedList<ChatList> mChatList = new LinkedList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private FloatingActionButton fab_main, fab_sub1, fab_sub2;
    private boolean isFabOpen = false;
    private final int On = 1;
    private final int Off = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        Toolbar toolbar = findViewById(R.id.chat_list_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        try {
            mChatList.addLast(new ChatList(R.drawable.exmaple_profile, "최재영", "잘지내니???", dataFormat.parse("2020-09-13 13:24:22"), "1", On));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            mChatList.addLast(new ChatList(R.drawable.example_profile2, "장주섭", "전화 좀 받아봐...", dataFormat.parse("2020-09-02 01:21:33"), "2", Off));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            mChatList.addLast(new ChatList(R.drawable.example_profile3, "이정규", "내가 미안하다고!", dataFormat.parse("2020-09-15 14:20:10"), "1", Off));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            mChatList.addLast(new ChatList(R.drawable.exmaple_profile4, "박현준", "잠깐 나와봐. 할 말 있어", dataFormat.parse("2020-09-17 12:30:45"), "1", On));
        } catch (ParseException e) {
            e.printStackTrace();
        }

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
package x.com.nubextalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.LinkedList;

import x.com.nubextalk.item.Profile;

public class MainFragment extends Fragment implements FriendListAdapter.OnItemClickListenerInterface {
    private RecyclerView mRecyclerView;
    private final LinkedList<Profile> mFriendList = new LinkedList<>();
    private RecyclerView.Adapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.content_friendlist, container, false);
        mRecyclerView = rootview.findViewById(R.id.friendRecycleview);
        mAdapter = new FriendListAdapter(getActivity(), mFriendList, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        Log.e("Fragment", "MainFragment");
        return rootview;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareData();
    }

    private void prepareData() {
        mFriendList.addLast(new Profile(0, 0, "치과"));
        mFriendList.addLast(new Profile(R.drawable.cat1, R.drawable.baseline_fiber_manual_record_black_24dp, "친구1"));
        mFriendList.addLast(new Profile(R.drawable.cat2, R.drawable.baseline_fiber_manual_record_black_24dp, "친구2"));
        mFriendList.addLast(new Profile(0, 0, "안과"));
        mFriendList.addLast(new Profile(R.drawable.cat3, R.drawable.baseline_fiber_manual_record_black_24dp, "친구3"));
        mFriendList.addLast(new Profile(R.drawable.cat4, R.drawable.baseline_fiber_manual_record_black_24dp, "친구4"));
        mFriendList.addLast(new Profile(R.drawable.cat5, R.drawable.baseline_fiber_manual_record_black_24dp, "친구5"));
    }

    @Override
    public void onItemClick(View v, int pos) {
        Log.e("aa", "aa");
        FriendListAdapter.FriendViewHolder viewHolder = (FriendListAdapter.FriendViewHolder) mRecyclerView.findViewHolderForAdapterPosition(pos);
        Intent intent = new Intent(getActivity(), TestActivity.class);
        intent.putExtra("name", viewHolder.profileName.getText().toString());
        startActivity(intent);
        Toast.makeText(getActivity(), viewHolder.profileName.getText().toString(), Toast.LENGTH_SHORT).show();
    }
}
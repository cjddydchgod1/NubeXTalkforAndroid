package x.com.nubextalk;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;

import x.com.nubextalk.item.Profile;

public class MainFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private final LinkedList<Profile> mFriendList = new LinkedList<>();
    private RecyclerView.Adapter mAdapter;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.content_friendlist, container, false);
        mRecyclerView = rootview.findViewById(R.id.friendRecycleview);

        mAdapter = new FriendListAdapter(getActivity(), mFriendList);
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
        mFriendList.addLast(new Profile(0, 0, "친구 프로필"));
        mFriendList.addLast(new Profile(R.drawable.cat1, R.drawable.baseline_fiber_manual_record_black_24dp, "친구1"));
        mFriendList.addLast(new Profile(R.drawable.cat2, R.drawable.baseline_fiber_manual_record_black_24dp, "친구2"));
        mFriendList.addLast(new Profile(R.drawable.cat3, R.drawable.baseline_fiber_manual_record_black_24dp, "친구3"));
        mFriendList.addLast(new Profile(R.drawable.cat4, R.drawable.baseline_fiber_manual_record_black_24dp, "친구4"));
        mFriendList.addLast(new Profile(R.drawable.cat5, R.drawable.baseline_fiber_manual_record_black_24dp, "친구5"));
    }
}
package x.com.nubextalk;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import io.realm.Realm;
import io.realm.RealmResults;
import x.com.nubextalk.Manager.AnimManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.Module.Adapter.FriendListAdapter;

public class FriendListFragment extends Fragment implements FriendListAdapter.onItemSelectedListener {
    private Realm realm;

    private LinearLayout mBottomWrapper;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RealmResults<User> mResults;

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup) inflater.inflate(R.layout.fragment_friend_list, container, false);


        realm           = Realm.getDefaultInstance();
        mRecyclerView   = rootview.findViewById(R.id.friendRecycleview);
        mBottomWrapper  = rootview.findViewById(R.id.bottomWrapper);

        mResults = User.getAll(realm);
        if(mResults.size() == 0){
            User.init(getActivity(), realm);
            mResults = User.getAll(realm);
        }

        mAdapter = new FriendListAdapter(getActivity() ,mResults);
        ((FriendListAdapter) mAdapter).setOnItemSelectedListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_fall_down));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scheduleLayoutAnimation();
        Log.e("Fragment", "MainFragment");
        return rootview;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSelected(User address) {
        initBottomsheet(address);
        new AnimManager(
                AnimManager.make(mBottomWrapper, AnimManager.SHORT).translationY(3000).translationY(0).setInterpolator(new DecelerateInterpolator())
        ).start(AnimManager.TOGETHER);
    }

    protected void initBottomsheet(User address) {
        // 내 프로필과 친구 프로필에서 이미지 수정버튼 유무
        if(address.getUid().equals("1")){
            mBottomWrapper.findViewById(R.id.modifyImage).setVisibility(View.VISIBLE);
        } else {
            mBottomWrapper.findViewById(R.id.modifyImage).setVisibility(View.INVISIBLE);
        }
        TextView profileName = mBottomWrapper.findViewById(R.id.profileName);
        ImageView profileImage = mBottomWrapper.findViewById(R.id.profileImage);
        // 이미지인데 Glide를 쓰면 안된다. String으로 바꿔야 하지 않을까?
        ImageView profileStatus = mBottomWrapper.findViewById(R.id.profileStatus);
        EditText modifyName = mBottomWrapper.findViewById(R.id.modifyName);
        TextView modifyButton = mBottomWrapper.findViewById(R.id.modifyButton);

        profileName.setText(address.getName());
        Glide.with(getContext()).load(address.getProfileImg()).into(profileImage);


        // 수정 버튼
        modifyButton.setOnClickListener(v -> {
            String buttonName = modifyButton.getText().toString();
            // 수정버튼을 눌렀을 경우
            if(buttonName.equals("수정")) {
                Log.e("수정", "Clicked");
                modifyButton.setText("완료");
                String temp = profileName.getText().toString();
                profileName.setVisibility(View.GONE);
                modifyName.setVisibility(View.VISIBLE);
                modifyName.setText(temp);
            } else {
                Log.e("완료", "Clicked");
                modifyButton.setText("수정");
                String temp = modifyName.getText().toString();
                modifyName.setVisibility(View.GONE);
                profileName.setVisibility(View.VISIBLE);
                profileName.setText(address.getName());

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        User user = realm.where(User.class).equalTo("uid", address.getUid()).findFirst();
                        user.setName(temp);
                    }
                });
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
            }
        });


        // 닫기 버튼
        mBottomWrapper.findViewById(R.id.mClose).setOnClickListener(v -> onBackPressed());
    }

    public interface IOnBackPressed {
        boolean onBackPressed();
    }

    public boolean onBackPressed() {
        if(mBottomWrapper.getTranslationY() == 0) {
            new AnimManager(
                    AnimManager.make(mBottomWrapper, AnimManager.SHORT).translationY(0).translationY(3000).setInterpolator(new DecelerateInterpolator())
            ).start(AnimManager.TOGETHER);
            return true;
        } else {
            return false;
        }
    }


}
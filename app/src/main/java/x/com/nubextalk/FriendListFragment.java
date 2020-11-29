package x.com.nubextalk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aquery.AQuery;

import org.w3c.dom.Text;

import java.io.File;
import java.io.InputStream;

import io.realm.Realm;
import io.realm.RealmResults;
import x.com.nubextalk.Manager.AnimManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.Module.Adapter.FriendListAdapter;

import static android.app.Activity.RESULT_OK;

public class FriendListFragment extends Fragment implements FriendListAdapter.onItemSelectedListener {
    private Realm realm;

    private LinearLayout mBottomWrapper;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RealmResults<User> mResults;

    private User myProfile;

    private AQuery aq;
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

        realm           = Realm.getInstance(UtilityManager.getRealmConfig());
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

        // Realm데이터 중 myprofile image 가져오기 => startGallery()에서 쓰임
        // 0번째(내 프로필)에 있는 데이터에서 profileImage가져오기.
        myProfile = mResults.get(0);

        aq = new AQuery(getActivity());

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
            mBottomWrapper.findViewById(R.id.modifyImage).setVisibility(View.GONE);
        }

        // 프로필 이름, 이미지, 상태
        TextView profileName = mBottomWrapper.findViewById(R.id.profileName);
        ImageView profileImage = mBottomWrapper.findViewById(R.id.profileImage);
        ImageView profileStatus = mBottomWrapper.findViewById(R.id.profileStatus);
        // 이름 수정 및 수정 버튼
        EditText modifyName = mBottomWrapper.findViewById(R.id.modifyName);
        TextView modifyNameButton = mBottomWrapper.findViewById(R.id.modifyButton);
        // 프로필 사진 수정 버튼
        TextView modifyImageButton = mBottomWrapper.findViewById(R.id.modifyImage);
        // 상태레이아웃
        LinearLayout statusLayout = mBottomWrapper.findViewById(R.id.statusLayout);
        // 채팅 버튼
        Button chatButton = mBottomWrapper.findViewById(R.id.chatButton);

//        File file = new file(R.drawable.baseline_fiber_manual_record_red_800_24dp);
        profileName.setText(address.getName());
        // profilestatus를 가져오려면 int형식이 아니여야 한다. 너무 더러워.
        switch(address.getStatus()) {
            case 0 :
                aq.view(profileStatus).image(R.drawable.baseline_fiber_manual_record_teal_a400_24dp);
                break;
            case 1 :
                aq.view(profileStatus).image(R.drawable.baseline_fiber_manual_record_yellow_50_24dp);
                break;
            case 2 :
                aq.view(profileStatus).image(R.drawable.baseline_fiber_manual_record_red_800_24dp);
                break;
        }
        if(!address.getProfileImg().isEmpty()) {
            aq.view(profileImage).image(address.getProfileImg());
        }
//        Glide.with(getContext()).load(address.getProfileImg()).into(profileImage);


        // 이름 수정 버튼
        modifyNameButton.setOnClickListener(v -> {
            // 수정버튼을 눌렀을 경우
            String buttonName = modifyNameButton.getText().toString();
            if(buttonName.equals("수정")) {
                modifyNameButton.setText("완료");
                String temp = profileName.getText().toString();
                profileName.setVisibility(View.GONE);
                modifyName.setVisibility(View.VISIBLE);
                modifyName.setText(temp);
            } else {
                modifyNameButton.setText("수정");
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
                // 새로고침
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
            }
        });

        // 프로필 사진 수정 버튼 (myProfile만 가능)
        modifyImageButton.setOnClickListener(v -> {
            if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                startGallery();
            }
        });

        // status(상태) 수정 버튼 (myProfile만 가능)
        if(address.getUid().equals("1")) {
            profileStatus.setOnClickListener(v -> {

                // chatButton은 숨기고, status를 선택하는 Layout등장
                chatButton.setVisibility(View.INVISIBLE);
                statusLayout.setVisibility(View.VISIBLE);
                // status를 클릭
                LinearLayout working = statusLayout.findViewById(R.id.working_status);
                LinearLayout leaving = statusLayout.findViewById(R.id.leaving_status);
                LinearLayout vacation = statusLayout.findViewById(R.id.vacation_status);

                LinearLayout.OnClickListener onClickListener = new LinearLayout.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.working_status :
                                changeRealmData(0, statusLayout, chatButton);
                                break;
                            case R.id.leaving_status :
                                changeRealmData(1, statusLayout, chatButton);
                                break;
                            case R.id.vacation_status :
                                changeRealmData(2, statusLayout, chatButton);
                                break;
                            default :
                                changeRealmData(-1, statusLayout, chatButton);
                                break;
                        }
                    }
                };
                working.setOnClickListener(onClickListener);
                leaving.setOnClickListener(onClickListener);
                vacation.setOnClickListener(onClickListener);


            });
        }
        // 1대1채팅 버튼
        chatButton.setOnClickListener(v -> {
            /*
             * 1. address에서 uid를 찾는다 (대화할 상대의 uid)
             * 2. ChatRoomMember에서 해당 uid와, 내 uid를 갖고 있는 rid를 찾는다.
             * 3. rid를 가지고 와서 intent로 넘겨준다.
             * 지금 ChatRoomMember를 만들지 않음.
             */
        });


        // 닫기 버튼
        mBottomWrapper.findViewById(R.id.mClose).setOnClickListener(v -> {
            // 이름 수정 버튼이 눌러져 있는채로 닫힐 경우
            String buttonName = modifyNameButton.getText().toString();
            if(buttonName.equals("수정")) {
                modifyNameButton.setText("수정");
                modifyName.setVisibility(View.GONE);
                profileName.setVisibility(View.VISIBLE);
            }
            // status 수정 버튼이 눌린 채로 닫힐 경우
            if(chatButton.getVisibility() == View.INVISIBLE) {
                chatButton.setVisibility(View.VISIBLE);
                statusLayout.setVisibility(View.INVISIBLE);
            }

            onBackPressed();
        });
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

    // Gallery start
    private void startGallery() {
        // 갤러리를 들어가기 위한 intent
        Intent cameraIntent = new Intent(Intent.ACTION_PICK);
        cameraIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // 두 개의 차이점이 뭐
        cameraIntent.setType("image/*");
//        cameraIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // 갤러리 실행
            startActivityForResult(cameraIntent, 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1) {
            if(resultCode == RESULT_OK) {
                try {
                    // 선택된 이미지의 Uri값을 가져온다.
                    Uri imgUri = data.getData();
                    // Uri값을 String으로 변환시킨다.
                    getPicture(imgUri);
                    // realm데이터에 myprofile이미지를 변경한다.

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void getPicture(Uri uri) {
        int index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};

        // 이미지 경로로 해당 이미지에 대한 정보를 가지고 있는 cursor 호출
        Cursor cursor = getActivity().getContentResolver().query(uri, proj, null, null, null);

        if(cursor == null){
//            view.showToast("사진이 없습니다.");
//            view.setInitProfile();
            Log.e("null", "사진이 비어있습니다.");
        } else if (cursor.moveToFirst()) {
            index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            String imgPath = cursor.getString(index);

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    User user = realm.where(User.class).equalTo("uid", myProfile.getUid()).findFirst();
                    user.setProfileImg(imgPath);
                }
            });
            // 새로고침
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();

            Log.d("realPathFromURI", "realPathFromURI: " + imgPath);
            cursor.close();
        } else {
//            view.showToast("커서가 비었습니다.");
//            view.setInitProfile(); cursor.close();
            Log.e("null", "커서가 비어있습니다.");
        }

    }

    // 상태변경
    public void changeRealmData(int status, LinearLayout statusLayout , Button chatButton) {
        // 버튼을 눌렀다면
        if(status != -1) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    User user = realm.where(User.class).equalTo("uid", myProfile.getUid()).findFirst();
                    user.setStatus(status);
                }
            });
            // 원상복귀 후 새로고침
            chatButton.setVisibility(View.VISIBLE);
            statusLayout.setVisibility(View.INVISIBLE);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        } else { // 버튼을 누르지 않았다면
            chatButton.setVisibility(View.VISIBLE);
            statusLayout.setVisibility(View.INVISIBLE);
        }
    }
}
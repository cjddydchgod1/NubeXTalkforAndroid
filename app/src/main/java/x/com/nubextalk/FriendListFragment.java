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
import java.util.Date;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;
import x.com.nubextalk.Manager.AnimManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.ChatRoomMember;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.Module.Adapter.FriendListAdapter;

import static android.app.Activity.RESULT_OK;

public class FriendListFragment extends Fragment implements FriendListAdapter.onItemSelectedListener {
    private Realm realm;

    private LinearLayout mBottomWrapper;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RealmResults<User> mResults;
    private ChatRoomMember mChat;

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
        // 프로필 이름 설정
        profileName.setText(address.getName());
        // profilestatus 수정 버튼
        switch(address.getStatus()) {
            case 1 :
                aq.view(profileStatus).image(R.drawable.baseline_fiber_manual_record_yellow_50_24dp);
                break;
            case 2 :
                aq.view(profileStatus).image(R.drawable.baseline_fiber_manual_record_red_800_24dp);
                break;
            default : // 0과 기본으로 되어있는 설정
                aq.view(profileStatus).image(R.drawable.baseline_fiber_manual_record_teal_a400_24dp);
                break;
        }

        // 프로필 사진
        if(!address.getProfileImg().isEmpty()) {
            aq.view(profileImage).image(address.getProfileImg());
        }

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

                changeRealmData(temp, 0);
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
                                changeStatus(0, statusLayout, chatButton);
                                break;
                            case R.id.leaving_status :
                                changeStatus(1, statusLayout, chatButton);
                                break;
                            case R.id.vacation_status :
                                changeStatus(2, statusLayout, chatButton);
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
             */
            mChat = realm.where(ChatRoomMember.class).equalTo("uid", address.getUid()).findFirst();
            if(mChat==null){
                // 새로만든 채팅이 없다면 새로 만든다.
                temporary(address);
            } else {
                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                intent.putExtra("rid", mChat.getRid());
                ((MainActivity) getActivity()).startChatRoomActivity(intent);
            }
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
        cameraIntent.setType("image/*");
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
                    /* Uri값의 이미지값을 불러온다.
                     * 이미지값을 저장한다.
                     * 해당 이미지값을 서버에 올린다.
                     */
                    changeRealmData(imgUri, 2);
                    // realm데이터에 myprofile이미지를 변경한다.

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 상태변경
    public void changeStatus(int status, LinearLayout statusLayout , Button chatButton) {
        // 버튼을 눌렀다면
        if(status != -1) {
            // 원상복귀
            chatButton.setVisibility(View.VISIBLE);
            statusLayout.setVisibility(View.INVISIBLE);
            changeRealmData(status, 1);
        } else { // 버튼을 누르지 않았다면
            chatButton.setVisibility(View.VISIBLE);
            statusLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void changeRealmData(Object object, int a) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                User user = realm.where(User.class).equalTo("uid", myProfile.getUid()).findFirst();
                if(a==0) user.setName(object.toString());
                else if(a==1) user.setStatus((Integer) object);
                else if(a==2) user.setProfileImg(object.toString());
            }
        });
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
    /** 현재 ChatAddActivity에서 가지고 온 코드 **/

    public void temporary(User address) {
        String rid = getRandomString().toString();
        ChatRoom newChatRoom = new ChatRoom();
        newChatRoom.setRid(rid);
        newChatRoom.setRoomName(address.getName());
        newChatRoom.setRoomImg(address.getProfileImg());

        newChatRoom.setUpdatedDate(new Date());

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(newChatRoom);

                ChatRoomMember chatMember = new ChatRoomMember();
                chatMember.setRid(rid);
                chatMember.setUid(address.getUid());
                /**
                 * ChatRoomMember 모델이 Primary Key 가 없어서 copyToRealmOrUpdate 함수는
                 * 사용하지 못하기 때문에 copyToRealm 함수를 사용함.
                 * 참고: https://stackoverflow.com/questions/40999299/android-create-realm-table-without-primary-key
                 **/
                realm.copyToRealm(chatMember);
            }
        });

//        setResult(RESULT_OK); //MainActivity 로 결과 전달
//        finish();
    }

    public StringBuffer getRandomString() {
        StringBuffer temp = new StringBuffer();
        Random rnd = new Random();
        rnd.setSeed(System.currentTimeMillis());
        for (int i = 0; i < 20; i++) {
            int rIndex = rnd.nextInt(3);
            switch (rIndex) {
                case 0:
                    // a-z
                    temp.append((char) ((int) (rnd.nextInt(26)) + 97));
                    break;
                case 1:
                    // A-Z
                    temp.append((char) ((int) (rnd.nextInt(26)) + 65));
                    break;
                case 2:
                    // 0-9
                    temp.append((rnd.nextInt(10)));
                    break;
            }
        }
        return temp;
    }








}
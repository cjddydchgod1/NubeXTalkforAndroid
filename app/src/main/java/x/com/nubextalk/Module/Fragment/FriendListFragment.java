/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aquery.AQuery;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import x.com.nubextalk.ChatRoomActivity;
import x.com.nubextalk.MainActivity;
import x.com.nubextalk.Manager.AnimManager;
import x.com.nubextalk.Manager.FireBase.FirebaseStorageManager;
import x.com.nubextalk.Manager.FireBase.FirebaseStoreManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.ChatRoomMember;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.Model.User2;
import x.com.nubextalk.Module.Adapter.FriendListAdapter;
import x.com.nubextalk.R;

import static android.app.Activity.RESULT_OK;

public class FriendListFragment extends Fragment implements FriendListAdapter.onItemSelectedListener {
    private Realm realm;
    FirebaseStoreManager firebaseStoreManager;

    private ViewGroup rootview;
    private LinearLayout mBottomWrapper;
    private RecyclerView mRecyclerView;
    private FriendListAdapter mAdapter;
    private RealmResults<User2> mResults;
    private ArrayList<User2> mList; // mResults를 복사
    private ChatRoomMember mChat;
    private AQuery aq;

    private String myUid; // Uid
    private String TAG = "FriendListFragment";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i(TAG, "OnAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * uid를 받아온다.
         */
        myUid = "1";

        realm           = Realm.getInstance(UtilityManager.getRealmConfig());
        firebaseStoreManager = new FirebaseStoreManager();
        Log.i(TAG, "OnCreate");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "OnViewCreated");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootview        = (ViewGroup) inflater.inflate(R.layout.fragment_friend_list, container, false);
        mRecyclerView   = rootview.findViewById(R.id.friendRecycleview);
        mBottomWrapper  = rootview.findViewById(R.id.bottomWrapper);
        aq              = new AQuery(getActivity());

        /**
         * recyclerview 디자인 및 애니매이션
         */
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        /**
         * search를 FriendListFragment에서 사용가능하게
         */
        setHasOptionsMenu(true);
        getActivity().invalidateOptionsMenu();
        Log.i(TAG, "OncreateView");
        return rootview;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       /**
         * recyclerview data 넣기
         */
        makeData();
        Log.i(TAG, "OnActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        /**
         * recyclerview 애니매이션
         */
        mRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_fall_down));
        mRecyclerView.scheduleLayoutAnimation();
        Log.i(TAG, "OnStart");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "OnResume");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "OnPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "OnStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "OnDestoryView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        realm.close();
        Log.i(TAG, "OnDetach");
    }

    public void makeData() {

        /**
         * 현재 로그인 되어있는 uid와는 다른 친구들의 목록 불러오
         */
        mResults = realm.where(User2.class).notEqualTo("code", myUid).findAll();

        /**
         * Search하기 위해 mResult -> mList
         */
        mList = new ArrayList<>();
        mList.addAll(realm.copyFromRealm(mResults));

        /**
         * Adapter 설정
         */
        mAdapter = new FriendListAdapter(getActivity() ,mList, myUid, aq);
        ((FriendListAdapter) mAdapter).setOnItemSelectedListener(this);
        mRecyclerView.setAdapter(mAdapter);

        makeProfile();
    }

    public void makeProfile() {
        User2 myProfile = realm.where(User2.class).equalTo("code", myUid).findFirst();
        TextView myProfileName = rootview.findViewById(R.id.my_profileName);
        ImageView myProfileImage = rootview.findViewById(R.id.my_profileImage);
        ImageView myProfileStatus = rootview.findViewById(R.id.my_profileStatus);
//        String name = myProfile.getDepartment() + " ";
//        if(!myProfile.getAppImagePath().isEmpty()){
//            aq.view(myProfileImage).image(myProfile.getAppImagePath());
//        }
//        if(myProfile.getNickname()==null) {
//            name += myProfile.getName();
//        } else {
//            name += myProfile.getNickname();
//        }
//        myProfileName.setText(name);
        Log.d("Tlqkf", myProfile.getAppName());
        myProfileName.setText(myProfile.getAppName());
        switch(myProfile.getAppStatus()) {
            case "1" :
                aq.view(myProfileStatus).image(R.drawable.baseline_fiber_manual_record_yellow_50_24dp);
                break;
            case "2" :
                aq.view(myProfileStatus).image(R.drawable.baseline_fiber_manual_record_red_800_24dp);
                break;
            default :
                aq.view(myProfileStatus).image(R.drawable.baseline_fiber_manual_record_teal_a400_24dp);
                break;
            }
        rootview.findViewById(R.id.profileConstraintLayout).setOnClickListener(v -> {
            onSelected(myProfile);
        });
    }

    // 검색
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_toolbar, menu);
        MenuItem searchItem = menu.findItem(R.id.toolbar_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        // 바로 검색이 가능하게끔
        searchView.onActionViewExpanded();
        /**
         * keyboard
         */
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                inputMethodManager.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return true;
            }
        });
    }
    public void search(String query) {
        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        mList.clear();
        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (query.length() == 0) {
            Log.i(TAG, "notInput Alldata");
            mResults = realm.where(User2.class).findAll();
            mList.addAll(realm.copyFromRealm(mResults));
        }
        // 문자 입력을 할때..
        else
        {
            Log.i(TAG, "Input data");
            mList.add(realm.where(User2.class).equalTo("uid", myUid).findFirst());
            mList.addAll(realm.where(User2.class).isNull("nickname")
                        .contains("name", query)
                        .or()
                        .contains("department", query.toUpperCase())
                        .findAll());
            mList.addAll(realm.where(User2.class).isNotNull("nickname")
                          .contains("nickname", query)
                          .or()
                          .contains("department", query.toUpperCase())
                          .findAll());
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        Log.i(TAG, "Notify");

        mAdapter.notifyDataSetChanged();
    }



    @Override
    public void onSelected(User2 address) {
        initBottomsheet(address);
        new AnimManager(
                AnimManager.make(mBottomWrapper, AnimManager.SHORT).translationY(3000).translationY(0).setInterpolator(new DecelerateInterpolator())
        ).start(AnimManager.TOGETHER);
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void initBottomsheet(User2 address) {
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
//        String name;
//        if(address.getNickname()==null) {
//            name = address.getName();
//        } else {
//            name = address.getNickname();
//        }
//        profileName.setText(name);
        profileName.setText(address.getAppName());
        // profilestatus 수정 버튼
        switch(address.getAppStatus()) {
            case "1" :
                aq.view(profileStatus).image(R.drawable.baseline_fiber_manual_record_yellow_50_24dp);
                break;
            case "2" :
                aq.view(profileStatus).image(R.drawable.baseline_fiber_manual_record_red_800_24dp);
                break;
            default : // 0과 기본으로 되어있는 설정
                aq.view(profileStatus).image(R.drawable.baseline_fiber_manual_record_teal_a400_24dp);
                break;
        }

        /**
         * 데이터 초기화
         */
        // 내 프로필과 친구 프로필에서 이미지 수정버튼, 1대1채팅 버튼 유무
        if(address.getCode().equals(myUid)){
            modifyImageButton.setVisibility(View.VISIBLE);
            chatButton.setVisibility((View.GONE));
            profileStatus.setClickable(true);
        } else {
            modifyImageButton.setVisibility(View.GONE);
            chatButton.setVisibility(View.VISIBLE);
            profileStatus.setClickable(false);
        }
        statusLayout.setVisibility(View.INVISIBLE);
        modifyName.setVisibility(View.GONE);
        profileName.setVisibility(View.VISIBLE);
        modifyNameButton.setText("수정");

        // 프로필 사진
        if(!address.getAppImagePath().isEmpty()) {
            aq.view(profileImage).image(address.getAppImagePath());
        }

        // Nickname 변
//        modifyNameButton.setOnClickListener(v -> {
//            String buttonName = modifyNameButton.getText().toString();
//            if(buttonName.equals("수정")) { // 수정버튼을 눌렀을 경우
//                modifyNameButton.setText("완료");
//                profileName.setVisibility(View.GONE);
//                modifyName.setVisibility(View.VISIBLE);
//                modifyName.setText(profileName.getText().toString());
//            } else { // 완료버튼을 눌렀을 경우
//                updateNickname(address, modifyName.getText().toString());
//                refreshFragment();
//            }
//        });

        // 프로필 사진 변경 (myProfile만 가능)
        modifyImageButton.setOnClickListener(v -> {
            if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                startGallery();
                // refresh
                refreshFragment();
            }
        });

        // 프로필 Status 변경
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
                    chatButton.setVisibility(View.VISIBLE);
                    statusLayout.setVisibility(View.INVISIBLE);
                    switch (v.getId()) {
                        case R.id.working_status :
                            firebaseStoreManager.updateProfileStatus(0, myUid);
                            break;
                        case R.id.leaving_status :
                            firebaseStoreManager.updateProfileStatus(1, myUid);
                            break;
                        case R.id.vacation_status :
                            firebaseStoreManager.updateProfileStatus(2, myUid);
                            break;
                    }
                    refreshFragment();
                }
            };
            working.setOnClickListener(onClickListener);
            leaving.setOnClickListener(onClickListener);
            vacation.setOnClickListener(onClickListener);
        });

        // 1대1채팅 버튼
        chatButton.setOnClickListener(v -> {
            /**
              * 1. address에서 uid를 찾는다 (대화할 상대의 uid)
              * 2. ChatRoomMember에서 해당 uid와, 내 uid를 갖고 있는 rid를 찾는다.
              * 3. rid를 가지고 와서 intent로 넘겨준다.
              */
            mChat = realm.where(ChatRoomMember.class).equalTo("uid", address.getCode()).findFirst();
            if(mChat==null){
                // 새로만든 채팅이 없다면 새로 만든다.
//                temporary(address);
            } else {
                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                intent.putExtra("rid", mChat.getRid());
                ((MainActivity) getActivity()).startChatRoomActivity(intent);
            }
        });


        // 닫기 버튼
        mBottomWrapper.findViewById(R.id.mClose).setOnClickListener(v -> {
            onBackPressed();
        });
        // RecyclerView위를 감싸는 view
        LinearLayout wrapper = rootview.findViewById(R.id.wrapper);
        wrapper.setVisibility(View.VISIBLE);
        // 해당 뷰를 클릭했을 시 나가기.
        wrapper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getActionMasked() == MotionEvent.ACTION_UP) {
                    wrapper.setVisibility(View.GONE);
                    onBackPressed();
                }
                return true;
            }
        });

        // 하위 레이아웃(wrapper)에게 touch event 전달하지 않게 막기
        mBottomWrapper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });


    }

    // bottomsheet 닫기버튼
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

    // Gallery 실행 후 결과
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1) {
            if(resultCode == RESULT_OK) {
                try {
                    // 선택된 이미지의 Uri값을 가져온다.
                    Uri imgUri = data.getData();
                    /* Uri값의 이미지값을 불러온다.
                     * 이미지값을 저장한다.
                     * 해당 이미지값을 Storage에 올린다.
                     */
                    UploadTask uploadTask = FirebaseStorageManager.uploadFile(imgUri,"profiles/"+myUid);
                    Task<Uri> urlTask = uploadTask
                            .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if(!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    return FirebaseStorageManager.downloadFile("profiles/"+myUid);
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isSuccessful()) {
                                        Uri imgUri = task.getResult();
                                        if (imgUri != null){
                                            FirebaseStoreManager firebaseStoreManager = new FirebaseStoreManager();
                                            firebaseStoreManager.updateProfileImg(imgUri.toString(), myUid);
                                         }
                                     else
                                         Log.i("FirebaseStorageManager", "uploadProfileImgFail");
                                    }
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void refreshFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    public void updateNickname(User user, String name) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                user.setNickname(name);
                realm.copyToRealmOrUpdate(user);
            }
        });
    }

    /** 현재 ChatAddActivity에서 가지고 온 코드 **/

    public void temporary(User address) {
        String rid = getRandomString().toString();
        ChatRoom newChatRoom = new ChatRoom();
        newChatRoom.setRid(rid);
        newChatRoom.setRoomName(address.getName());
        newChatRoom.setRoomImg(address.getProfileImg());

        Date date = new Date();
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
                ChatContent chat = new ChatContent();
                chat.setCid(""); // Content ID 자동으로 유니크한 값 설정
                chat.setRid(rid); // RID 채팅방 아이디
                chat.setType(9); // 시스템 메세지
                chat.setContent("채팅방이 개설 되었습니다.");
                chat.setIsRead(true);
                chat.setSendDate(date);
                realm.copyToRealmOrUpdate(chat);
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
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
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Response;
import x.com.nubextalk.ChatAddActivity;
import x.com.nubextalk.ChatRoomActivity;
import x.com.nubextalk.MainActivity;
import x.com.nubextalk.Manager.AnimManager;
import x.com.nubextalk.Manager.FireBase.FirebaseStorageManager;
import x.com.nubextalk.Manager.FireBase.FirebaseStoreManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.ChatRoomMember;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.Module.Adapter.FriendListAdapter;
import x.com.nubextalk.Module.Case.FriendlistCase;
import x.com.nubextalk.PACS.ApiManager;
import x.com.nubextalk.R;

import static android.app.Activity.RESULT_OK;

public class FriendListFragment extends Fragment implements FriendListAdapter.onItemSelectedListener {
    private Realm realm;
    FirebaseStoreManager firebaseStoreManager;

    private ViewGroup rootview;
    private LinearLayout mBottomWrapper;
    private RecyclerView mRecyclerView;
    private FriendListAdapter mAdapter;
    private RealmResults<User> mResults;
    private ArrayList<User> mUserList; // mResults를 복사
    private RealmResults<ChatRoomMember> chatList;
    private AQuery aq;
    private ApiManager apiManager;
    private String myUid; // Uid
    private String mHid = "w34qjptO0cYSJdAwScFQ";
    private String TAG = "FriendListFragment";
    private User myProfile;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm           = Realm.getInstance(UtilityManager.getRealmConfig());
        firebaseStoreManager = new FirebaseStoreManager();
        apiManager = new ApiManager(getActivity(), realm);
//        Log.i(TAG, "OnCreate");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootview        = (ViewGroup) inflater.inflate(R.layout.fragment_friend_list, container, false);
        mRecyclerView   = rootview.findViewById(R.id.friendRecycleview);
        mBottomWrapper  = rootview.findViewById(R.id.bottomWrapper);
        mUserList       = new ArrayList<>();
        aq              = new AQuery(getActivity());

        /**
         * recyclerview 디자인 및 애니매이션
         */
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        getData();


//        Log.i(TAG, "OncreateView");
        return rootview;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /**
         * search를 FriendListFragment에서 사용가능하게
         */
        setHasOptionsMenu(true);
        getActivity().invalidateOptionsMenu();
//        Log.i(TAG, "OnActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        /**
         * recyclerview 애니매이션
         */
        mRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_fall_down));
        mRecyclerView.scheduleLayoutAnimation();
//        Log.i(TAG, "OnStart");

    }

    public void getData() {
                /**
         * PACS서버에서 Userlist를 가져와 Realm DB에 저장한다.
         */
        apiManager.getEmployeeList(new ApiManager.onApiListener() {
            @Override
            public void onSuccess(Response response, String body) {
                /**
                 * userlist -> realm 저장
                 */
                try{
                    /**
                     * String(body) -> JSONArray
                     */
                    JSONArray jsonArray = new JSONArray(body);
                    /**
                     * JSONArray -> JSONObject
                     * JSONObject -> User
                     * User -> ArrayList
                     */
                    int len = jsonArray.length();

                    for(int i=0; i<len; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        User user = realm.where(User.class).equalTo("code", jsonObject.getString("code")).findFirst();
                        if(user == null) {
                            user = new User();
                            user.setCode(jsonObject.getString("code"));
                        } else {
                            user = realm.copyFromRealm(user);
                        }
                        user.setUserId(jsonObject.getString("userid"));
                        user.setLastName(jsonObject.getString("lastname"));
                        user.setTypeCode(jsonObject.getString("typecode"));
                        user.setTypeCodeName(jsonObject.getString("typecodename"));
                        user.setRemoved(jsonObject.getString("removed"));
                        user.setAppImagePath(jsonObject.getString("app_IMG_PATH"));
                        user.setAppStatus(jsonObject.getString("app_STATUS"));
                        user.setAppName(jsonObject.getString("app_NAME"));
                        user.setAppFcmKey(jsonObject.getString("app_FCM_KEY"));
                        if(!UtilityManager.checkString(user.getAppNickName()))
                            user.setAppNickName(jsonObject.getString("lastname"));
                        mUserList.add(user);
                    }
                    /**
                     * ArrayList -> Realm
                     */
                    realm.executeTransaction(realm1 -> {
                        realm1.copyToRealmOrUpdate(mUserList);
                    });
                    makeData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

//        try{
//                    /**
//                     * String(body) -> JSONArray
//                     */
//                    String body = UtilityManager.loadJson(getActivity(), "example_user.json");
//                    JSONArray jsonArray = new JSONArray(body);
//                    /**
//                     * JSONArray -> JSONObject
//                     * JSONObject -> User
//                     * User -> ArrayList
//                     */
//                    int len = jsonArray.length();
//
//                    ArrayList<User> users = new ArrayList<>();
//                    for(int i=0; i<len; i++) {
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//                        User user = new User();
//                        user.setCode(jsonObject.getString("code"));
//                        user.setUserId(jsonObject.getString("userid"));
//                        user.setLastName(jsonObject.getString("lastname"));
//                        user.setTypeCode(jsonObject.getString("typecode"));
//                        user.setTypeCodeName(jsonObject.getString("typecodename"));
//                        user.setRemoved(jsonObject.getString("removed"));
//                        user.setAppImagePath(jsonObject.getString("app_IMG_PATH"));
//                        user.setAppStatus(jsonObject.getString("app_STATUS"));
//                        user.setAppName(jsonObject.getString("app_NAME"));
//                        user.setAppFcmKey(jsonObject.getString("app_FCM_KEY"));
//
//                        user.setAppNickName(jsonObject.getString("lastname"));
//                        Log.d(TAG, user.getLastName());
//                        mUserList.add(user);
//                    }
//                    /**
//                     * ArrayList -> Realm
//                     */
//                    realm.executeTransaction(realm1 -> {
//                        realm1.where(User.class).findAll().deleteAllFromRealm();
//                        realm1.copyToRealmOrUpdate(mUserList);
//                    });
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//    makeData();

    }

    public void makeData() {
        /**
         * mUserList에서 나의 정보만 뺀다.
         */
        for(User user : mUserList) {
            if(user.getUserId().equals(Config.getMyUID(realm))) {
                myProfile = user;
                mUserList.remove(user);
                break;
            }
        }

        /**
         * Adapter 설정
         */
        mAdapter = new FriendListAdapter(getActivity() ,mUserList, aq, FriendlistCase.NON_RADIO);
        mAdapter.setOnItemSelectedListener(this);
        mRecyclerView.setAdapter(mAdapter);

        makeProfile();
    }

    public void makeProfile() {
        myUid = myProfile.getUserId();
        TextView myProfileName = rootview.findViewById(R.id.my_profileName);
        ImageView myProfileImage = rootview.findViewById(R.id.my_profileImage);
        ImageView myProfileStatus = rootview.findViewById(R.id.my_profileStatus);
        /**
         * profileImage valid check
         */
        if(URLUtil.isValidUrl(myProfile.getAppImagePath())){
            aq.view(myProfileImage).image(myProfile.getAppImagePath());
        } else {
            aq.view(myProfileImage).image(R.drawable.baseline_account_circle_black_24dp);
        }
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
        mUserList.clear();
        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (query.length() == 0) {
            Log.i(TAG, "notInput Alldata");
            mResults = realm.where(User.class).findAll();
            mUserList.addAll(realm.copyFromRealm(mResults));
        }
        // 문자 입력을 할때..
        else
        {
            Log.i(TAG, "Input data");
            mUserList.addAll(realm.where(User.class).contains("appName", query).findAll());
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        Log.i(TAG, "Notify");

        mAdapter.notifyDataSetChanged();
    }



    @Override
    public void onSelected(User address) {
        initBottomsheet(address);
        new AnimManager(
                AnimManager.make(mBottomWrapper, AnimManager.SHORT).translationY(3000).translationY(0).setInterpolator(new DecelerateInterpolator())
        ).start(AnimManager.TOGETHER);
    }

    @Override
    public void onSelected(User address, RadioButton radioButton) {

    }
    @SuppressLint("ClickableViewAccessibility")
    protected void initBottomsheet(User address) {
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
        profileName.setText(address.getAppName());

        // profilestatus
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
        // 프로필 사진
        if(URLUtil.isValidUrl(address.getAppImagePath())){
            aq.view(profileImage).image(address.getAppImagePath());
        } else {
            aq.view(profileImage).image(R.drawable.baseline_account_circle_black_24dp);
        }

        /**
         * 데이터 초기화
         */
        // 내 프로필과 친구 프로필에서 이미지 수정버튼, 1대1채팅 버튼 유무
        /**
         * if(address.getCode().equals(myUid))
         */
        if(address.getUserId().equals(myUid)){
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

        // Nickname 변경
        modifyNameButton.setOnClickListener(v -> {
            String buttonName = modifyNameButton.getText().toString();
            if(buttonName.equals("수정")) { // 수정버튼을 눌렀을 경우
                modifyNameButton.setText("완료");
                profileName.setVisibility(View.GONE);
                modifyName.setVisibility(View.VISIBLE);
                modifyName.setText(profileName.getText().toString());
            } else { // 완료버튼을 눌렀을 경우
                updateNickname(address, modifyName.getText().toString());
                refreshFragment();
            }
        });

        // 프로필 사진 변경 (myProfile만 가능)
        modifyImageButton.setOnClickListener(v -> {
            if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                startGallery();
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
                    realm.executeTransaction(realm1 -> {
                        switch (v.getId()) {
                            case R.id.working_status :
                                address.setAppStatus("0");
                                break;
                            case R.id.leaving_status :
                                address.setAppStatus("1");
                                break;
                            case R.id.vacation_status :
                                address.setAppStatus("2");
                                break;
                        }
                    });
                    apiManager.setEmployeeAppInfo(address, new ApiManager.onApiListener() {
                        @Override
                        public void onSuccess(Response response, String body) {
                            refreshFragment();
                            Log.d("setEmplyee", body);
                        }
                    });
                }
            };
            working.setOnClickListener(onClickListener);
            leaving.setOnClickListener(onClickListener);
            vacation.setOnClickListener(onClickListener);
        });

        // 1대1채팅 버튼
        chatButton.setOnClickListener(v -> {
            /**
             * 1. 대화할 uid를 가지고 있는 rid를 모두 찾는다.
             * 2. rid중에서 memberCount값이 2인것을 찾는다.
             * 3. 해당 rid값으로 intent로 넘겨준다.
             */
            chatList = realm.where(ChatRoomMember.class).equalTo("uid", address.getUserId()).findAll();
            ChatRoom chatRoom = null;
            if(chatList.size() != 0) {

                Iterator<ChatRoomMember> mChat;
                /**
                 * 1대1채팅방을 찾았다면 그 값이 chatRoom
                 * 찾지 못했다면 chatRoom은 null값
                 */
                for(mChat = chatList.iterator(); chatRoom == null ;mChat.hasNext()) {
                    chatRoom = realm.where(ChatRoom.class)
                            .equalTo("rid", mChat.next().getRid())
                            .and()
                            .equalTo("memberCount", 2).findFirst();
                }
            }
            if(chatRoom==null){
                // 새로만든 채팅이 없다면 새로 만든다.
                ArrayList<User> list = new ArrayList<>();
                list.add(address);
                ChatAddActivity.createNewChat(realm, list, "");
            } else {
                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                intent.putExtra("rid", chatRoom.getRid());
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
                    UploadTask uploadTask = FirebaseStorageManager.uploadFile(imgUri,"hospital/"+mHid+"/users/"+myUid);
                    Task<Uri> urlTask = uploadTask
                            .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if(!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    return FirebaseStorageManager.downloadFile("hospital/"+mHid+"/users/"+myUid);
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isSuccessful()) {
                                        Uri imgUri = task.getResult();
                                        if (imgUri != null){
                                            realm.executeTransaction(realm1 -> {
                                                myProfile.setAppImagePath(imgUri.toString());
                                            });
                                            apiManager.setEmployeeAppInfo(myProfile, new ApiManager.onApiListener() {
                                                    @Override
                                                    public void onSuccess(Response response, String body) {
                                                        refreshFragment();
                                                    }
                                                });
                                         }
                                     else
                                         Log.i("FriendListFragment", "uploadProfileImgFail");
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
                user.setAppNickName(name);
                realm.copyToRealmOrUpdate(user);
            }
        });
    }
}
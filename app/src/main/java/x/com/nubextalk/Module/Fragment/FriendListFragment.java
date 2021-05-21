/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
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

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Response;
import x.com.nubextalk.ChatAddActivity;
import x.com.nubextalk.ChatRoomActivity;
import x.com.nubextalk.MainActivity;
import x.com.nubextalk.Manager.AnimManager;
import x.com.nubextalk.Manager.FireBase.FirebaseStorageManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.Module.Adapter.FriendListAdapter;
import x.com.nubextalk.PACS.ApiManager;
import x.com.nubextalk.ProfileImageViewActivity;
import x.com.nubextalk.R;

import static android.app.Activity.RESULT_OK;
import static x.com.nubextalk.Module.CodeResources.COMPLETE;
import static x.com.nubextalk.Module.CodeResources.DEFAULT_PROFILE;
import static x.com.nubextalk.Module.CodeResources.EMPTY;
import static x.com.nubextalk.Module.CodeResources.HOSPITAL_ID;
import static x.com.nubextalk.Module.CodeResources.MODIFICATION;
import static x.com.nubextalk.Module.CodeResources.PATH_IMAGE;
import static x.com.nubextalk.Module.CodeResources.PATH_STORAGE1;
import static x.com.nubextalk.Module.CodeResources.PATH_STORAGE2;
import static x.com.nubextalk.Module.CodeResources.SEARCH;
import static x.com.nubextalk.Module.CodeResources.STATUS_BUSY;
import static x.com.nubextalk.Module.CodeResources.STATUS_OFF;
import static x.com.nubextalk.Module.CodeResources.STATUS_ON;
import static x.com.nubextalk.Module.CodeResources.TITLE_FRIEND_LIST;

public class FriendListFragment extends Fragment implements FriendListAdapter.onItemSelectedListener {
    private Realm mRealm;
    private Context mContext;
    private Activity mActivity;
    private ViewGroup mRootview;
    private LinearLayout mBottomWrapper;
    private RecyclerView mRecyclerView;
    private FriendListAdapter mAdapter;
    private RealmResults<User> mResults;
    private ArrayList<User> mUserList;
    private AQuery mAquery;
    private ApiManager mApiManager;
    private String mMyUid;
    private String mHid = HOSPITAL_ID;
    private User mMyProfile;

    /* bottomSheet */
    // 프로필 이름, 이미지, 상태
    private TextView mProfileName;
    private ImageView mProfileImage;
    private ImageView mProfileStatus;
    // 수정 버튼
    private EditText mEditName;
    private TextView mMdoifyNameBtn;
    private TextView mModifyImageBtn;
    private ImageView mDelImageBtn;
    // 상태레이아웃
    private LinearLayout mStatusLayout;
    // 채팅 버튼
    private Button mChatBtn;
    //
    private LinearLayout mExitWrapper;

    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        if (context instanceof Activity)
            mActivity = (Activity) context;
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRealm = Realm.getInstance(UtilityManager.getRealmConfig());
        mApiManager = new ApiManager(mActivity, mRealm);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootview = (ViewGroup) inflater.inflate(R.layout.fragment_friend_list, container, false);
        mRecyclerView = mRootview.findViewById(R.id.friendRecycleview);
        mBottomWrapper = mRootview.findViewById(R.id.bottomWrapper);
        mUserList = new ArrayList<>();
        mAquery = new AQuery(mActivity);

        mActivity.setTitle(TITLE_FRIEND_LIST);

        /**
         * bottomSheet
         */
        // 프로필 이름, 이미지, 상태
        mProfileName = mBottomWrapper.findViewById(R.id.profileName);
        mProfileImage = mBottomWrapper.findViewById(R.id.profileImage);
        mProfileStatus = mBottomWrapper.findViewById(R.id.profileStatus);
        // 수정 버튼
        mEditName = mBottomWrapper.findViewById(R.id.modifyName);
        mMdoifyNameBtn = mBottomWrapper.findViewById(R.id.modifyButton);
        mModifyImageBtn = mBottomWrapper.findViewById(R.id.modifyImage);
        mDelImageBtn = mBottomWrapper.findViewById(R.id.deleteImage);
        // 상태레이아웃
        mStatusLayout = mBottomWrapper.findViewById(R.id.statusLayout);
        // 채팅 버튼
        mChatBtn = mBottomWrapper.findViewById(R.id.chatButton);
        mExitWrapper = mRootview.findViewById(R.id.wrapper);
        /**
         * recyclerview 디자인
         */
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        getDataFromPACS();

        /**
         * recyclerview 애니매이션
         */
        mRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(mActivity, R.anim.layout_animation_fall_down));
        mRecyclerView.scheduleLayoutAnimation();
        return mRootview;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /**
         * search를 FriendListFragment에서 사용가능하게
         */
        setHasOptionsMenu(true);
        mActivity.invalidateOptionsMenu();
    }

    @Override
    public void onDetach() {
        if (mRealm != null) {
            mRealm.close();
        }
        mContext = null;
        mActivity = null;
        super.onDetach();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        /** Modified By Jongho Lee*/
        if(hidden){
            if (mBottomWrapper.getTranslationY() == 0) {
                new AnimManager(
                        AnimManager.make(mBottomWrapper, AnimManager.SHORT).translationY(0).translationY(3000).setInterpolator(new DecelerateInterpolator())
                ).start(AnimManager.TOGETHER);
                mExitWrapper.setVisibility(View.GONE);
            }
        }
    }

    public void getDataFromPACS() {
        /**
         * PACS서버에서 Userlist를 가져와 Realm DB에 저장한다.
         */
        mApiManager.getEmployeeList(new ApiManager.onApiListener() {
            @Override
            public void onSuccess(Response response, String body) {
                /**
                 * userlist -> realm 저장
                 */
                try {
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

                    for (int i = 0; i < len; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        User user = mRealm.where(User.class).equalTo("code", jsonObject.getString("code")).findFirst();
                        if (user == null) {
                            user = new User();
                            user.setCode(jsonObject.getString("code"));
                        } else {
                            user = mRealm.copyFromRealm(user);
                        }
                        user.setUid(jsonObject.getString("userid"));
                        user.setLastName(jsonObject.getString("lastname"));
                        user.setTypeCode(jsonObject.getString("typecode"));
                        user.setTypeCodeName(jsonObject.getString("typecodename"));
                        user.setRemoved(jsonObject.getString("removed"));
                        user.setAppImagePath(jsonObject.getString("app_IMG_PATH"));
                        user.setAppStatus(jsonObject.getString("app_STATUS"));
                        user.setAppName(jsonObject.getString("app_NAME"));
                        user.setAppFcmKey(jsonObject.getString("app_FCM_KEY"));
                        if (!UtilityManager.checkString(user.getAppNickName()))
                            user.setAppNickName(jsonObject.getString("lastname"));
                        mUserList.add(user);
                    }
                    /**
                     * ArrayList -> Realm
                     */
                    mRealm.executeTransaction(realm1 -> {
                        realm1.copyToRealmOrUpdate(mUserList);
                    });
                    processData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 나의 유저정보는 뺀다.
     */
    public void processData() {
        for (User user : mUserList) {
            if (user.getUid().equals(Config.getMyUID(mRealm))) {
                mMyProfile = user;
                mUserList.remove(user);
                break;
            }
        }

        /**
         * Adapter 설정
         */
        mAdapter = new FriendListAdapter(mActivity, mUserList, mAquery, false);
        mAdapter.setOnItemSelectedListener(this);
        mRecyclerView.setAdapter(mAdapter);

        setMyProfile();
    }

    public void setMyProfile() {
        mMyUid = mMyProfile.getUid();
        TextView myProfileName = mRootview.findViewById(R.id.my_profileName);
        ImageView myProfileImage = mRootview.findViewById(R.id.my_profileImage);
        ImageView myProfileStatus = mRootview.findViewById(R.id.my_profileStatus);
        /**
         * profileImage valid check
         */
        if (URLUtil.isValidUrl(mMyProfile.getAppImagePath())) {
            mAquery.view(myProfileImage).image(mMyProfile.getAppImagePath());
        } else {
            mAquery.view(myProfileImage).image(DEFAULT_PROFILE);
            myProfileImage.setColorFilter(myProfileName.getTextColors().getDefaultColor());
        }
        myProfileName.setText(mMyProfile.getAppName());
        switch (mMyProfile.getAppStatus()) {
            case "1":
                mAquery.view(myProfileStatus).image(STATUS_BUSY);
                break;
            case "2":
                mAquery.view(myProfileStatus).image(STATUS_OFF);
                break;
            default:
                mAquery.view(myProfileStatus).image(STATUS_ON);
                break;
        }
        mRootview.findViewById(R.id.profileConstraintLayout).setOnClickListener(v -> {
            onSelected(mMyProfile);
        });
    }

    // 검색
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_toolbar, menu);
        MenuItem searchItem = menu.findItem(SEARCH);
        SearchView searchView = (SearchView) searchItem.getActionView();
        // 바로 검색이 가능하게끔
        searchView.onActionViewExpanded();

        //Change searchView widgets color
        SearchView.SearchAutoComplete searchAutoComplete =
                (SearchView.SearchAutoComplete) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchAutoComplete.setHintTextColor(getResources().getColor(R.color.cWhite, null));
        searchAutoComplete.setTextColor(getResources().getColor(R.color.cWhite, null));

        ImageView clearButton = (ImageView) searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        clearButton.setColorFilter(getResources().getColor(R.color.cWhite, null));

        /**
         * keyboard
         */
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
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
            mResults = mRealm.where(User.class).findAll();
            mUserList.addAll(mRealm.copyFromRealm(mResults));
        }
        // 문자 입력을 할때..
        else {
            mUserList.addAll(mRealm.where(User.class).contains("appName", query).findAll());
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSelected(User user) {
        initBottomsheet(user);
        new AnimManager(
                AnimManager.make(mBottomWrapper, AnimManager.SHORT, 0).translationY(3000).translationY(0).setInterpolator(new DecelerateInterpolator())
        ).start(AnimManager.TOGETHER);
    }


    protected void initBottomsheet(User user) {
        /**
         * 데이터 초기화
         */
        mProfileName.setText(user.getAppName());
        switch (user.getAppStatus()) {
            case "1":
                mAquery.view(mProfileStatus).image(STATUS_BUSY);
                break;
            case "2":
                mAquery.view(mProfileStatus).image(STATUS_OFF);
                break;
            default: // 0과 기본으로 되어있는 설정
                mAquery.view(mProfileStatus).image(STATUS_ON);
                break;
        }

        if (URLUtil.isValidUrl(user.getAppImagePath())) {
            mAquery.view(mProfileImage).image(user.getAppImagePath());
            mProfileImage.setEnabled(true);
        } else {
            mAquery.view(mProfileImage).image(DEFAULT_PROFILE);
            mProfileImage.setColorFilter(mProfileName.getTextColors().getDefaultColor());
            mProfileImage.setEnabled(false);
        }
        /**
         * 임시로 userId로 primaryKey를 사용하고 있지만, 추후에 code로 변경해야 한다.
         * if(user.getCode().equals(myUid))
         */
        // 이미지 수정버튼, 1대1채팅 버튼 유무 (내 프로필과 친구 프로필의 차이)
        if (user.getUid().equals(mMyUid)) {
            mDelImageBtn.setVisibility(View.VISIBLE);
            mModifyImageBtn.setVisibility(View.VISIBLE);
            mChatBtn.setVisibility((View.GONE));
            mProfileStatus.setEnabled(true);
        } else {
            mDelImageBtn.setVisibility(View.GONE);
            mModifyImageBtn.setVisibility(View.GONE);
            mChatBtn.setVisibility(View.VISIBLE);
            mProfileStatus.setEnabled(false);
        }
        mStatusLayout.setVisibility(View.INVISIBLE);
        mEditName.setVisibility(View.GONE);
        mProfileName.setVisibility(View.VISIBLE);
        mMdoifyNameBtn.setText(MODIFICATION);

        // 해당 exitWrapper클릭시 onBackPressed() 수행
        mExitWrapper.setVisibility(View.VISIBLE);

        // Profile Image 선택시 확대된 사진 띄우기
        mProfileImage.setOnClickListener(v ->{
            Intent intent = new Intent(mContext, ProfileImageViewActivity.class);
                            intent.putExtra("uid", user.getUid());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            mContext.startActivity(intent);
        });

        // Nickname 변경
        mMdoifyNameBtn.setOnClickListener(v -> {
            changeOpponentNickName(user);
        });

        // 프로필 사진 변경 (myProfile만 가능)
        mModifyImageBtn.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                startGallery();
            }
        });

        // 기본 프로필로 변경 (myProfile만 가능)
        mDelImageBtn.setOnClickListener(v -> {
            mRealm.executeTransaction(realm1 -> {
                user.setAppImagePath("null");
            });

            mApiManager.setEmployeeAppInfo(user, new ApiManager.onApiListener() {
                @Override
                public void onSuccess(Response response, String body) {
                    refreshFragment();
                }
            });
        });

        // 프로필 Status 변경
        mProfileStatus.setOnClickListener(v -> {
            changeMyStatus(user);
        });

        // 1대1채팅 버튼
        mChatBtn.setOnClickListener(v -> {
            /**
             * 1. 대화할 uid를 가지고 있는 rid를 모두 찾는다.
             * 2. rid중에서 memberCount값이 2인것을 찾는다.
             * 3. 해당 rid값으로 intent로 넘겨준다.
             */

            mChatBtn.setClickable(false); //중복 채팅방 생성 방지

            User.getChatroom(mRealm, user, new User.UserListener() {
                @Override
                public void onFindPersonalChatRoom(ChatRoom chatRoom) {
                    if (chatRoom == null) {
                        // 새로만든 채팅이 없다면 새로 만든다.
                        ArrayList<User> list = new ArrayList<>();
                        list.add(user);
                        Intent intent = new Intent(mActivity, ChatRoomActivity.class);
                        new ChatAddActivity().createNewChat(mRealm, mContext, list, EMPTY, new ChatAddActivity.onNewChatCreatedListener() {
                            @Override
                            public void onCreate(String rid) {
                                intent.putExtra("rid", rid);
                                ((MainActivity) getActivity()).startChatRoomActivity(intent);
                            }
                        });

                    } else {
                        Intent intent = new Intent(mActivity, ChatRoomActivity.class);
                        intent.putExtra("rid", chatRoom.getRid());
                        ((MainActivity) mActivity).startChatRoomActivity(intent);
                    }
                }
            });


        });


        // 닫기
        mBottomWrapper.findViewById(R.id.mClose).setOnClickListener(v -> {
            onBackPressed();
        });
        mExitWrapper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP) {
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
        if (mBottomWrapper.getTranslationY() == 0) {
            new AnimManager(
                    AnimManager.make(mBottomWrapper, AnimManager.SHORT).translationY(0).translationY(3000).setInterpolator(new DecelerateInterpolator())
            ).start(AnimManager.TOGETHER);
            mExitWrapper.setVisibility(View.GONE);
            return true;
        } else {
            return false;
        }
    }

    public void changeOpponentNickName(User user) {
        String buttonName = mMdoifyNameBtn.getText().toString();
        if (buttonName.equals(MODIFICATION)) { // 수정버튼을 눌렀을 경우
            mMdoifyNameBtn.setText(COMPLETE);
            mProfileName.setVisibility(View.GONE);
            mEditName.setVisibility(View.VISIBLE);
            mEditName.setText(mProfileName.getText().toString());
        } else { // 완료버튼을 눌렀을 경우
            updateNickname(user, mEditName.getText().toString());
            refreshFragment();
        }
    }

    public void changeMyStatus(User user) {
        // chatButton은 숨기고, status를 선택하는 Layout등장
        mChatBtn.setVisibility(View.INVISIBLE);
        mStatusLayout.setVisibility(View.VISIBLE);
        // status를 클릭
        LinearLayout working = mStatusLayout.findViewById(R.id.working_status);
        LinearLayout leaving = mStatusLayout.findViewById(R.id.leaving_status);
        LinearLayout vacation = mStatusLayout.findViewById(R.id.vacation_status);
        LinearLayout.OnClickListener onClickListener = new LinearLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChatBtn.setVisibility(View.VISIBLE);
                mStatusLayout.setVisibility(View.INVISIBLE);
                mRealm.executeTransaction(realm1 -> {
                    switch (v.getId()) {
                        case R.id.working_status:
                            user.setAppStatus("0");
                            break;
                        case R.id.leaving_status:
                            user.setAppStatus("1");
                            break;
                        case R.id.vacation_status:
                            user.setAppStatus("2");
                            break;
                    }
                });
                mApiManager.setEmployeeAppInfo(user, new ApiManager.onApiListener() {
                    @Override
                    public void onSuccess(Response response, String body) {
                        refreshFragment();
                    }
                });
            }
        };
        working.setOnClickListener(onClickListener);
        leaving.setOnClickListener(onClickListener);
        vacation.setOnClickListener(onClickListener);
    }

    // Gallery start
    private void startGallery() {
        // 갤러리를 들어가기 위한 intent
        Intent cameraIntent = new Intent(Intent.ACTION_PICK);
        cameraIntent.setType(PATH_IMAGE);
        if (cameraIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            // 갤러리 실행
            startActivityForResult(cameraIntent, 1);
        }
    }

    // Gallery 실행 후 결과
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri imgUri = data.getData();
                    /* Uri값의 이미지값을 불러온다.
                     * 이미지값을 저장한다.
                     * 해당 이미지값을 Storage에 올린다.
                     */

                    UploadTask uploadTask = FirebaseStorageManager.uploadFile(imgUri, PATH_STORAGE1 + mHid + PATH_STORAGE2 + mMyUid);
                    Task<Uri> urlTask = uploadTask
                            .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    return FirebaseStorageManager.downloadFile(PATH_STORAGE1 + mHid + PATH_STORAGE2 + mMyUid);
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri imgUri = task.getResult();
                                        if (imgUri != null) {
                                            mRealm.executeTransaction(realm1 -> {
                                                mMyProfile.setAppImagePath(imgUri.toString());
                                            });
                                            mApiManager.setEmployeeAppInfo(mMyProfile, new ApiManager.onApiListener() {
                                                @Override
                                                public void onSuccess(Response response, String body) {
                                                    refreshFragment();
                                                }
                                            });
                                        } else {
                                        }
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
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                user.setAppNickName(name);
                realm.copyToRealmOrUpdate(user);
            }
        });
    }
}
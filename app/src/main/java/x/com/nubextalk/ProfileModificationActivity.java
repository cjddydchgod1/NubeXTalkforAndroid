/*
 * Created By JONGHO, LEE, Researcher of Institute of Biomedical Engineering, TechHeim Co.,Ltd.
 * on  2021.
 * This software is not stable health-care system and not certificated any health-care authority(like CE, FDA)
 * Prohibit redistribution, modification, commercial use.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aquery.AQuery;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;

import io.realm.Realm;
import okhttp3.Response;
import x.com.nubextalk.Manager.FireBase.FirebaseStorageManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.PACS.ApiManager;

import static x.com.nubextalk.Module.CodeResources.DEFAULT_PROFILE;
import static x.com.nubextalk.Module.CodeResources.HOSPITAL_ID;
import static x.com.nubextalk.Module.CodeResources.MODIFY_DEFAULT_IMAGE;
import static x.com.nubextalk.Module.CodeResources.MODIFY_IMAGE;
import static x.com.nubextalk.Module.CodeResources.PATH_IMAGE;
import static x.com.nubextalk.Module.CodeResources.PATH_STORAGE1;
import static x.com.nubextalk.Module.CodeResources.PATH_STORAGE2;
import static x.com.nubextalk.Module.CodeResources.TITLE_MODIFY_PROFILE;

public class ProfileModificationActivity extends AppCompatActivity {
    private Realm mRealm;
    private AQuery mAquery;
    private User mUser;

    private Uri mImgUri;

    private ImageView mProfileImage;
    private TextView mProfileName;
    private Button mConfirmBtn;
    private Button mCancelBtn;
    private RadioGroup mStatusGroup;
    private String mStatus;

    private Boolean mChkModImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_modification);
        Intent intent = getIntent();
        String uid = intent.getStringExtra("uid");


        mRealm = Realm.getInstance(UtilityManager.getRealmConfig());
        mProfileImage = findViewById(R.id.change_profile_image);
        mProfileName = findViewById(R.id.change_profile_text);
        mConfirmBtn = findViewById(R.id.confirm_btn);
        mCancelBtn = findViewById(R.id.cancel_btn);
        mStatusGroup = findViewById(R.id.status_group);
        RadioButton rb_busy = findViewById(R.id.rgt_busy);
        RadioButton rb_exit = findViewById(R.id.rgt_exit);
        RadioButton rb_vacation = findViewById(R.id.rgt_vacation);

        mAquery = new AQuery(this);

        mUser = User.getMyAccountInfo(mRealm);

        /** 이름 설정 **/
        mProfileName.setText(mUser.getAppName());
        /** 이미지 설정 **/
        mChkModImage = false;
        if (URLUtil.isValidUrl(mUser.getAppImagePath())) {
            mAquery.view(mProfileImage).image(mUser.getAppImagePath());
        } else {
            mAquery.view(mProfileImage).image(DEFAULT_PROFILE);
            mProfileImage.setColorFilter(mProfileName.getTextColors().getDefaultColor());
        }
        /** 상태 설정 **/
        mStatus = mUser.getAppStatus();
        rb_busy.setChecked(mStatus.equals("0"));
        rb_exit.setChecked(mStatus.equals("1"));
        rb_vacation.setChecked(mStatus.equals("2"));

        /** 이미지 클릭시 이벤트 **/
        mProfileImage.setOnClickListener(v -> {
            String[] menuArray = new String[]{MODIFY_IMAGE, MODIFY_DEFAULT_IMAGE};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(TITLE_MODIFY_PROFILE)
                    .setItems(menuArray, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            switch (pos) {
                                case 0: /** 갤러리 실행 후 사진 변경 **/
                                    startGallery();
                                    break;
                                case 1: /** 기본 프로필 사진으로 변경 **/
                                    changeDefaultImage();
                                    break;
                            }
                        }
                    }).create().show();
        });
        /** 상태 라디오 이벤트 **/
        changeStatus();

        /** 확인 버튼 및 취소 버튼 **/
        mConfirmBtn.setOnClickListener(v -> {

            applyModification();
        });
        mCancelBtn.setOnClickListener(v -> {
            exitActivity();
        });
    }

    @Override
    protected void onDestroy() {
        if (mRealm != null) {
            mRealm.close();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exitActivity();
    }

    private void changeDefaultImage() {
        mAquery.view(mProfileImage).image(DEFAULT_PROFILE);
        mProfileImage.setColorFilter(mProfileName.getTextColors().getDefaultColor());
        /** Uri 초기화 방법 알아보자 **/
        mImgUri = null;
        mChkModImage = true;
    }

    private void applyModification() {
        /** 이미지 변경 **/
        if (mChkModImage && mImgUri != null) {
            UploadTask uploadTask = FirebaseStorageManager.uploadFile(mImgUri, PATH_STORAGE1 + HOSPITAL_ID + PATH_STORAGE2 + mUser.getUid());
            Task<Uri> urlTask = uploadTask
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return FirebaseStorageManager.downloadFile(PATH_STORAGE1 + HOSPITAL_ID + PATH_STORAGE2 + mUser.getUid());
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri imgUri = task.getResult();
                                if (imgUri != null) {
                                    changeUserInfo(imgUri);
                                } else {
                                }
                            }
                        }
                    });
        } else { /** 이미지 변경 X **/
            changeUserInfo();
        }


    }

    private void changeUserInfo(Uri imgUri) {
        mRealm.executeTransaction(realm1 -> {
            mUser.setAppStatus(mStatus);
            mUser.setAppImagePath(imgUri.toString());
        });
        ApiManager apiManager = new ApiManager(ProfileModificationActivity.this, mRealm);
        apiManager.setEmployeeAppInfo(mUser, new ApiManager.onApiListener() {
            @Override
            public void onSuccess(Response response, String body) {
                Toast.makeText(ProfileModificationActivity.this, "프로필이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                exitActivity();
            }
        });
    }

    private void changeUserInfo() {
        mRealm.executeTransaction(realm1 -> {
            mUser.setAppStatus(mStatus);
            if(mChkModImage) {
                mUser.setAppImagePath("");
            }
        });
        ApiManager apiManager = new ApiManager(ProfileModificationActivity.this, mRealm);
        apiManager.setEmployeeAppInfo(mUser, new ApiManager.onApiListener() {
            @Override
            public void onSuccess(Response response, String body) {
                Toast.makeText(ProfileModificationActivity.this, "프로필이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                exitActivity();
            }
        });
    }

    // Gallery start
    private void startGallery() {
        // 갤러리를 들어가기 위한 intent
        Intent cameraIntent = new Intent(Intent.ACTION_PICK);
        cameraIntent.setType(PATH_IMAGE);
        cameraIntent.setAction(Intent.ACTION_GET_CONTENT);
        if (cameraIntent.resolveActivity(this.getPackageManager()) != null) {
            // 갤러리 실행
            startActivityForResult(cameraIntent, 1);
        }
    }

    // Gallery 실행 후 결과
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                mChkModImage = true;
                mImgUri = data.getData();
                mProfileImage.setColorFilter(null);
                mAquery.view(mProfileImage).image(mImgUri);
            }
        }
    }

    private void changeStatus() {
        mStatusGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.rgt_busy:
                    mStatus = "0";
                    break;
                case R.id.rgt_exit:
                    mStatus = "1";
                    break;
                case R.id.rgt_vacation:
                    mStatus = "2";
                    break;
            }
        });
    }

    private void exitActivity() {
        Intent intent = new Intent(ProfileModificationActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
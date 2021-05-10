/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;

import io.realm.Realm;
import okhttp3.Response;
import x.com.nubextalk.Manager.FireBase.FirebaseFunctionsManager;
import x.com.nubextalk.Manager.FireBase.FirebaseStoreManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.ChatRoomMember;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.PACS.ApiManager;

import static x.com.nubextalk.Module.CodeResources.HOSPITAL_ID;
import static x.com.nubextalk.Module.CodeResources.MSG_LOGIN_FAIL;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ApiManager mApiManager;
    private Realm mRealm;

    private EditText mEditId;
    private EditText mEditPassword;

    private Intent mIntent;

    private CheckBox mCheckAutoLogin;

    private boolean mEqualUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("checkFirstAccess", Activity.MODE_PRIVATE);
        boolean checkFirstAccess = sharedPreferences.getBoolean("checkFirstAccess", false);

//        if (!checkFirstAccess) {
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean("checkFirstAccess", true);
//            editor.apply();
//
//            Intent tutorialIntent = new Intent(LoginActivity.this, TutorialActivity.class);
//            startActivity(tutorialIntent);
//        }
        Intent tutorialIntent = new Intent(LoginActivity.this, TutorialActivity.class);
        startActivity(tutorialIntent);

        setContentView(R.layout.activity_login);
        mRealm = Realm.getInstance(UtilityManager.getRealmConfig());
        mApiManager = new ApiManager(this, mRealm);

        mEditId = (EditText) findViewById(R.id.login_id_edit);
        mEditPassword = (EditText) findViewById(R.id.login_password_edit);

        Button mSignUpBtn = (Button) findViewById(R.id.login_sign_up);
        Button mSignInBtn = (Button) findViewById(R.id.login_sign_in);

        mCheckAutoLogin = findViewById(R.id.checkAutoLogin);

        mSignUpBtn.setOnClickListener(this);
        mSignInBtn.setOnClickListener(this);
        /**
         * AutoLoginCheck(Ext5) 확인후 자동 로그인
         * SessionID가 만료가 되지 않게 id, pwd를 박아서 다시 로그인하는 방식으로 하였다.
         */
        mIntent = new Intent(getApplicationContext(), MainActivity.class);
        Config myAutoLogin = Config.getAutoLogin(mRealm);
        if (myAutoLogin != null) {
            if (myAutoLogin.getExt1().equals("true")) {
                mApiManager.login(new ApiManager.onLoginApiListener() {
                    @Override
                    public void onSuccess(Response response, String body) {
                        startActivity(mIntent);
                    }

                    @Override
                    public void onFail() {
                        Toast.makeText(LoginActivity.this, MSG_LOGIN_FAIL, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }


    @Override
    public void onClick(View view) {
        final LoginActivity activity = this;

        switch (view.getId()) {
            case R.id.login_sign_up:
                startActivity(new Intent(activity, RegisterActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
            case R.id.login_sign_in:
                String id = String.valueOf(mEditId.getText());
                String password = String.valueOf(mEditPassword.getText());
                String autoLogin = "false";
                if (mCheckAutoLogin.isChecked())
                    autoLogin = "true";

                /** 마지막으로 로그인한 아이디와 일치하는지 확인 **/
                Config lastLoginID = Config.getLastLoginID(mRealm);
                if (lastLoginID == null)
                    mEqualUid = false;
                else {
                    if (lastLoginID.getExt1().equals(id))
                        mEqualUid = true;
                    else
                        mEqualUid = false;
                }

                mApiManager.login(id, password, autoLogin, new ApiManager.onLoginApiListener() {
                    @Override
                    public void onSuccess(Response response, String body) {
                        /**
                         * 전에 있던 사용자가 그대로 로그인했다면 데이터 유지
                         * 다른 사용자가 로그인했다면 데이터 삭제 및 해당 사용자가 속해있는 ChatRoom을 가져오기
                         */
                        if (!mEqualUid) {
                            mRealm.executeTransactionAsync(realm1 -> {
                                realm1.where(User.class).findAll().deleteAllFromRealm();
                                realm1.where(ChatRoom.class).findAll().deleteAllFromRealm();
                                realm1.where(ChatRoomMember.class).findAll().deleteAllFromRealm();
                                realm1.where(ChatContent.class).findAll().deleteAllFromRealm();

                                Config.settingInit(getApplicationContext(), realm1);
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    updateFirebaseStore(id);
                                }
                            }, new Realm.Transaction.OnError() {
                                @Override
                                public void onError(Throwable error) {
                                    error.printStackTrace();
                                }
                            });
                        } else {
                            updateFirebaseStore(id);
                        }
                    }

                    @Override
                    public void onFail() {
                        Toast.makeText(activity, MSG_LOGIN_FAIL, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }

    /**
     * uid, token을 firestore에 올리는 작업
     */
    private void updateFirebaseStore(String id) {
        FirebaseStoreManager firebaseStoreManager = new FirebaseStoreManager();
        firebaseStoreManager.updateUser(id, Config.getMyAccount(mRealm).getExt4()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseFunctionsManager.getUserAttendingChatRoom(HOSPITAL_ID, id);
                startActivity(mIntent);
            }
        });
    }
}
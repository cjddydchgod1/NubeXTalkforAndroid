/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ApiManager apiManager;
    private Realm realm;

    private EditText mEditId;
    private EditText mEditPassword;

    private Button mSignUpBtn;
    private Button mSignInBtn;

    private Intent intent;

    private CheckBox checkAutoLogin;

    private boolean equalUID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        realm = Realm.getInstance(UtilityManager.getRealmConfig());
        apiManager = new ApiManager(this, realm);

        mEditId = (EditText) findViewById(R.id.login_id_edit);
        mEditPassword = (EditText) findViewById(R.id.login_password_edit);

        mSignUpBtn = (Button) findViewById(R.id.login_sign_up);
        mSignInBtn = (Button) findViewById(R.id.login_sign_in);

        checkAutoLogin = findViewById(R.id.checkAutoLogin);

        mSignUpBtn.setOnClickListener(this);
        mSignInBtn.setOnClickListener(this);
        /**
         * AutoLoginCheck(Ext5) 확인후 자동 로그인
         * SessionID가 만료가 되지 않게 id, pwd를 박아서 다시 로그인하는 방식으로 하였다.
         */
        intent = new Intent(getApplicationContext(), MainActivity.class);
        Config myAutoLogin = Config.getAutoLogin(realm);
        if(myAutoLogin != null) {
            if(myAutoLogin.getExt1().equals("checked")) {
                apiManager.login(new ApiManager.onLoginApiListener() {
                    @Override
                    public void onSuccess(Response response, String body) {
                        startActivity(intent);
                    }

                    @Override
                    public void onFail() {
                        Toast.makeText(LoginActivity.this, "자동 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
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
                if(checkAutoLogin.isChecked())
                    autoLogin = "true";

                /** 마지막으로 로그인한 아이디와 일치하는지 확인 **/
                Config lastLoginID = Config.getLastLoginID(realm);
                if(lastLoginID == null || UtilityManager.checkString(lastLoginID.getExt1()))
                    equalUID = false;
                else {
                    if(lastLoginID.getExt1().equals(id))
                        equalUID = true;
                    else
                        equalUID = false;
                }

                apiManager.login(id, password, autoLogin,new ApiManager.onLoginApiListener() { // lee777 , tech1!
                    @Override
                    public void onSuccess(Response response, String body) {
                        Log.d("RESUlT onSuccess", response.toString());
                        /**
                         * 전에 있던 사용자가 그대로 로그인했다면 데이터 유지
                         * 다른 사용자가 로그인했다면 데이터 삭제 및 해당 사용자가 속해있는 ChatRoom을 가져오기
                         */
                        if(!equalUID) {
                            Log.e("equal", "false");
                            realm.executeTransactionAsync(realm1 -> {
                                realm1.where(User.class).findAll().deleteAllFromRealm();
                                realm1.where(ChatRoom.class).findAll().deleteAllFromRealm();
                                realm1.where(ChatRoomMember.class).findAll().deleteAllFromRealm();
                                realm1.where(ChatContent.class).findAll().deleteAllFromRealm();

                                Config.settingInit(getApplicationContext(), realm1);
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    /**
                                     * uid, token을 firestore에 올리는 작업
                                     */
                                    FirebaseStoreManager firebaseStoreManager = new FirebaseStoreManager();
                                    firebaseStoreManager.updateUser(id, Config.getMyAccount(realm).getExt4()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            FirebaseFunctionsManager.getUserAttendingChatRoom("w34qjptO0cYSJdAwScFQ", id);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }, new Realm.Transaction.OnError() {
                                @Override
                                public void onError(Throwable error) {
                                    error.printStackTrace();
                                }
                            });
                        } else {
                            /**
                             * uid, token을 firestore에 올리는 작업
                             */
                            Log.e("equal", "true");
                            FirebaseStoreManager firebaseStoreManager = new FirebaseStoreManager();
                            firebaseStoreManager.updateUser(id, Config.getMyAccount(realm).getExt4()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFail() {
                        Toast.makeText(activity, "아이디/비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }
}
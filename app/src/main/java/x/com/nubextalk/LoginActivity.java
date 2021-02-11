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
import x.com.nubextalk.Manager.FireBase.FirebaseStoreManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.PACS.ApiManager;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ApiManager apiManager;
    private Realm realm;

    private EditText mEditHospital;
    private EditText mEditId;
    private EditText mEditPassword;

    private Button mSignUpBtn;
    private Button mSignInBtn;

    private Intent intent;

    private CheckBox checkAutoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        realm = Realm.getInstance(UtilityManager.getRealmConfig());
        apiManager = new ApiManager(this, realm);

        mEditHospital = (EditText)findViewById(R.id.login_hospital_edit);
        mEditId = (EditText)findViewById(R.id.login_id_edit);
        mEditPassword = (EditText)findViewById(R.id.login_password_edit);

        mSignUpBtn = (Button)findViewById(R.id.login_sign_up);
        mSignInBtn = (Button)findViewById(R.id.login_sign_in);

        checkAutoLogin = findViewById(R.id.checkAutoLogin);

        mSignUpBtn.setOnClickListener(this);
        mSignInBtn.setOnClickListener(this);
        /**
         * AutoLoginCheck(Ext5) 확인후 자동 로그인
         * SessionID가 만료가 되지 않게 id, pwd를 박아서 다시 로그인하는 방식으로 하였다.
         */
        intent = new Intent(getApplicationContext(), MainActivity.class);
        Config myAccount = Config.getMyAccount(realm);
        if(myAccount != null) {
            if(myAccount.getAutoLogin()) {
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

        switch (view.getId()){
            case R.id.login_sign_up:
                startActivity(new Intent(activity, RegisterActivity.class));
                finish();
                break;
            case R.id.login_sign_in:
                String id = String.valueOf(mEditId.getText());
                String password = String.valueOf(mEditPassword.getText());
                boolean autoLogin = false;
                if(checkAutoLogin.isChecked())
                    autoLogin = true;
                apiManager.login(id, password, autoLogin,new ApiManager.onLoginApiListener() { // lee777 , tech1!
                    @Override
                    public void onSuccess(Response response, String body) {
                        Log.d("RESUlT", response.toString());
                        /**
                         * uid, token을 firestore에 올리는 작업
                         */
                        FirebaseStoreManager firebaseStoreManager = new FirebaseStoreManager();
                        firebaseStoreManager.updateUser(id, Config.getMyAccount(realm).getExt4()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(intent);
                            }
                        });


                    }

                    @Override
                    public void onFail() {
                        Toast.makeText(activity, "아이디/비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show();
                    }
                });
//                apiManager.login(id,password);
//                /**
//                 * uid, token을 firestore에 올리는 작업
//                 */
//                FirebaseStoreManager firebaseStoreManager = new FirebaseStoreManager();
//                firebaseStoreManager.updateUser(id, Config.getMyAccount(realm).getExt4());
//                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//                startActivity(intent);
//                finish();
                break;
        }
    }
}
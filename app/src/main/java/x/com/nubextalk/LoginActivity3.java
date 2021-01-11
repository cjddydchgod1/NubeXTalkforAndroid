/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import io.realm.Realm;
import okhttp3.Response;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.PACS.ApiManager;

public class LoginActivity3 extends AppCompatActivity implements View.OnClickListener {

    private ApiManager apiManager;
    private Realm realm;

    private EditText mEditHospital;
    private EditText mEditId;
    private EditText mEditPassword;

    private Button mSignUpBtn;
    private Button mSignInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login3);

        realm = Realm.getInstance(UtilityManager.getRealmConfig());
        apiManager = new ApiManager(this, realm);

        mEditHospital = (EditText)findViewById(R.id.login_hospital_edit);
        mEditId = (EditText)findViewById(R.id.login_id_edit);
        mEditPassword = (EditText)findViewById(R.id.login_password_edit);

        mSignUpBtn = (Button)findViewById(R.id.login_sign_up);
        mSignInBtn = (Button)findViewById(R.id.login_sign_in);

        mSignUpBtn.setOnClickListener(this);
        mSignInBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_sign_up:
                break;
            case R.id.login_sign_in:
                String id = String.valueOf(mEditId.getText());
                String password = String.valueOf(mEditPassword.getText());
                apiManager.login(id, password, new ApiManager.onApiListener() { // lee777 , tech1!
                    @Override
                    public void onSuccess(Response response, String body) {
                        Log.d("RESUlT", response.toString());
                    }
                });
                break;
        }
    }
}
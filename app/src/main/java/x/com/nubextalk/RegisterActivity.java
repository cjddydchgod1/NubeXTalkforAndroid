/*
 * Created By JONGHO, LEE, Researcher of Institute of Biomedical Engineering, TechHeim Co.,Ltd.
 * on  2021.
 * This software is not stable health-care system and not certificated any health-care authority(like CE, FDA)
 * Prohibit redistribution, modification, commercial use.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import io.realm.Realm;
import io.realm.RealmResults;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.ChatRoomMember;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.Model.User;

import static x.com.nubextalk.Module.CodeResources.HTTPS_SSL;
import static x.com.nubextalk.Module.CodeResources.HTTP_SSL;
import static x.com.nubextalk.Module.CodeResources.PACS_HOSPITAL_NAME;
import static x.com.nubextalk.Module.CodeResources.PACS_SERVER_IP;

public class RegisterActivity extends AppCompatActivity {

    private SwitchCompat mSslSwitch;
    private EditText mEditName;
    private EditText mEditHost;
    private EditText mEditPort;
    private Realm mRealm;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRealm = Realm.getInstance(UtilityManager.getRealmConfig());
        mEditName = findViewById(R.id.editTextHospitalName);
        mSslSwitch = findViewById(R.id.editTextSSL);
        mEditHost = findViewById(R.id.editTextHost);
        mEditPort = findViewById(R.id.editTextPort);

        //기본 병원 이름이랑 병원 PACS 서버 IP 입력되어 있음
        mEditName.setText(PACS_HOSPITAL_NAME);
        mEditHost.setText(PACS_SERVER_IP);

        Button registerButton = findViewById(R.id.buttonRegister);
        final RegisterActivity activity = this;

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Config serverInfo = Config.getServerInfo(realm);
                        String ssl = mSslSwitch.isChecked() ? HTTPS_SSL : HTTP_SSL;
                        String host = mEditHost.getText().toString().equals("") ? PACS_SERVER_IP : mEditHost.getText().toString();
                        String port = mEditPort.getText().toString().equals("") ? "" : ":".concat(mEditPort.getText().toString());
                        String name = mEditName.getText().toString().equals("") ? "" : mEditName.getText().toString();
                        if (serverInfo == null) {
                            serverInfo = new Config();
                            serverInfo.setCODENAME("ServerInfo");
                            serverInfo.setCODE("ServerInfo");
                        }
                        serverInfo.setExt1(ssl);
                        serverInfo.setExt2(host);
                        serverInfo.setExt3(port);
                        serverInfo.setExt4(name);
                        realm.copyToRealmOrUpdate(serverInfo);

                    }
                });
                clearModels(mRealm);
                startActivity(new Intent(activity, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
    }

    /**
     * 서버 최초 등록 및 재등록 시에 Config 모델을 제외한 나머지 모델들 데이터 초기화 함수
     *
     * @param realm
     */
    private void clearModels(Realm realm) {
        final RealmResults<User> userRealmResults = realm.where(User.class).findAll();
        final RealmResults<ChatRoom> chatRoomRealmResults = realm.where(ChatRoom.class).findAll();
        final RealmResults<ChatRoomMember> chatRoomMemberRealmResults = realm.where(ChatRoomMember.class).findAll();
        final RealmResults<ChatContent> chatContentRealmResults = realm.where(ChatContent.class).findAll();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                userRealmResults.deleteAllFromRealm();
                chatRoomRealmResults.deleteAllFromRealm();
                chatRoomMemberRealmResults.deleteAllFromRealm();
                chatContentRealmResults.deleteAllFromRealm();
            }
        });

    }

}
/*
 * Created By Jong Ho, Lee on  2021.
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

public class RegisterActivity extends AppCompatActivity {

    private SwitchCompat mSslSwitch;
    private EditText mEditName;
    private EditText mEditHost;
    private EditText mEditPort;
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRealm = Realm.getInstance(UtilityManager.getRealmConfig());
        mEditName = findViewById(R.id.editTextHospitalName);
        mSslSwitch = findViewById(R.id.editTextSSL);
        mEditHost = findViewById(R.id.editTextHost);
        mEditPort = findViewById(R.id.editTextPort);

        Button registerButton = findViewById(R.id.buttonRegister);
        final RegisterActivity activity = this;

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Config serverInfo = Config.getServerInfo(realm);
                        String ssl = mSslSwitch.isChecked() ? "https://" : "http://";
                        String host = mEditHost.getText().toString().equals("") ? "121.166.85.235" : mEditHost.getText().toString();
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
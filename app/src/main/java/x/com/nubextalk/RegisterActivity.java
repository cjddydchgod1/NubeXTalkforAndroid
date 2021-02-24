/*
 * Created By Jong Ho, Lee on  2021.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import io.realm.Realm;
import io.realm.RealmResults;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.ChatRoomMember;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.Model.User;

public class RegisterActivity extends AppCompatActivity {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch sslInput;
    private EditText nameInput;
    private EditText hostInput;
    private EditText portInput;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        realm = Realm.getInstance(UtilityManager.getRealmConfig());
        nameInput = findViewById(R.id.editTextHospitalName);
        sslInput = findViewById(R.id.editTextSSL);
        hostInput = findViewById(R.id.editTextHost);
        portInput = findViewById(R.id.editTextPort);

        Button registerButton = findViewById(R.id.buttonRegister);
        final RegisterActivity activity = this;

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Config serverInfo = Config.getServerInfo(realm);
                        String ssl = sslInput.isChecked() ? "https://" : "http://";
                        String host = hostInput.getText().toString().equals("") ? "121.166.85.235" : hostInput.getText().toString();
                        String port = portInput.getText().toString().equals("") ? "" : ":".concat(portInput.getText().toString());
                        String name = nameInput.getText().toString().equals("") ? "" : nameInput.getText().toString();
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
                clearModels(realm);
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
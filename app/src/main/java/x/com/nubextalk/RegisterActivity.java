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
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.Config;

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
                        if (serverInfo == null) {
                            serverInfo = new Config();
                            serverInfo.setCODENAME("ServerInfo");
                            serverInfo.setCODE("ServerInfo");
                        }
                        String ssl = sslInput.isChecked() ? "https://" : "http://";
                        String host = hostInput.getText().toString().equals("") ? "192.168.3.156" : hostInput.getText().toString();
                        String port = portInput.getText().toString().equals("") ? "" : ":".concat(portInput.getText().toString());
                        String name = nameInput.getText().toString().equals("") ? "" : nameInput.getText().toString();
                        serverInfo.setOid(name);
                        serverInfo.setExt1(ssl);
                        serverInfo.setExt2(host);
                        serverInfo.setExt3(port);
                        serverInfo.setExt4(name);
                        realm.copyToRealmOrUpdate(serverInfo);
                        startActivity(new Intent(activity, LoginActivity.class));
                        finish();
                    }
                });
            }
        });
    }
}
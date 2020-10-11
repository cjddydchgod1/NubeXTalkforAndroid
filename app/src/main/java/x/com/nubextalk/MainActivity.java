/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import io.realm.Realm;
import io.realm.RealmResults;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.ChatRoomMember;
import x.com.nubextalk.Model.User;

/**
 * Github Commint Message는 다음을 따라주시길 바랍니다.
 *
 * ex:)
 * [이종호] 2020.08.26
 * 1. 메세지1
 * 2. 메세지2
 * ....
 */
public class MainActivity extends AppCompatActivity {

    private Realm realm;
    private static RealmResults<ChatContent> mChatContentResult;
    private static RealmResults<ChatRoom> mChatRoomResult;
    private static RealmResults<User> mUserResult;
    private static RealmResults<ChatRoomMember> mChatRoomMemberResult;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        realm = Realm.getInstance(UtilityManager.getRealmConfig());
        mChatContentResult = ChatContent.getAll(realm);
        mChatRoomResult = ChatRoom.getAll(realm);
        mUserResult = User.getAll(realm);

        realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mUserResult.deleteAllFromRealm();
                        }
        });
        realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mChatContentResult.deleteAllFromRealm();
                        }
        });
        realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            mChatRoomResult.deleteAllFromRealm();
                        }
        });

        if(mChatContentResult.size() == 0){
            ChatContent.init(this, realm);
        }
        if(mChatRoomResult.size() == 0){
            ChatRoom.init(this, realm);
        }
        if(mUserResult.size() == 0){
            User.init(this, realm);
        }

    }
     @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void initPermission() {
        PermissionListener pm = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                onBackPressed();
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            new TedPermission().with(this)
                    .setPermissionListener(pm)
                    .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ).check();
        }
        else{

        }
    }
}

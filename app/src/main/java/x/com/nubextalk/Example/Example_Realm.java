/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Example;

import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.Example_Model_Address;
import x.com.nubextalk.R;

import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.joanzapata.iconify.widget.IconButton;

import java.util.UUID;

/**
 * Realm 기본 사용법
 *  - 가이드라인 공식 홈페이지 : https://realm.io/docs/java/5.8.0/#getting-started
 *
 *  Stetho Inspect 사용 : chrome://inspect
 *
 */
public class Example_Realm extends AppCompatActivity implements View.OnClickListener {

    private Realm realm;
    private Button btn01, btn02, btn03, btn04, btn05, btn06,btn07, btn08, btn09, btn10, btn11;


    /**
     * Realm LifeCycle Close
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example_realm);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
         * Realm Object Initial
         */
        realm = Realm.getInstance(UtilityManager.getRealmConfig());


        btn01 = findViewById(R.id.btn01);
        btn02 = findViewById(R.id.btn02);
        btn03 = findViewById(R.id.btn03);
        btn04 = findViewById(R.id.btn04);
        btn05 = findViewById(R.id.btn05);
        btn06 = findViewById(R.id.btn06);
        btn07 = findViewById(R.id.btn07);
        btn08 = findViewById(R.id.btn08);
        btn09 = findViewById(R.id.btn09);
        btn10 = findViewById(R.id.btn10);
        btn11 = findViewById(R.id.btn11);

        btn01.setOnClickListener(this);
        btn02.setOnClickListener(this);
        btn03.setOnClickListener(this);
        btn04.setOnClickListener(this);
        btn05.setOnClickListener(this);
        btn06.setOnClickListener(this);
        btn07.setOnClickListener(this);
        btn08.setOnClickListener(this);
        btn09.setOnClickListener(this);
        btn10.setOnClickListener(this);
        btn11.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn01:
                /**
                 * 데이터 초기화 함수 (w/h JSON)
                 *  - Primary key 가 중복되면 Exception
                 */
                Example_Model_Address.init(getApplicationContext(), realm);
                break;
            case R.id.btn02:
                /**
                 * Data Write Single Row
                 */
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Example_Model_Address model = new Example_Model_Address();
                        model.setOid(UUID.randomUUID().toString());
                        model.setTid("TEST");
                        model.setName("Test Place");
                        model.setDesc("Test Place Desc");
                        model.setAddress("Test Place Address");
                        model.setPhone("000-000-0000");
                        model.setEmail("test@test.com");
                        model.setTypeCode("TEST");
                        model.setTypeFont("{fas-user}");
                        model.setTypeText("테스트");
                        model.setRuntime("24/7 Run");
                        model.setRating("5.0");

                        realm.copyToRealmOrUpdate(model);
                    }
                });
                break;
            case R.id.btn03:
                /**
                 * Data Write Multi Rows
                 */
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmList<Example_Model_Address> list = new RealmList<>();
                        for (int i= 0; i<5; i++){
                            Example_Model_Address model = new Example_Model_Address();
                            model.setOid(UUID.randomUUID().toString());
                            model.setTid("TEST" + i);
                            model.setName("Test Place" + i);
                            model.setDesc("Test Place Desc" + i);
                            model.setAddress("Test Place Address" + i);
                            model.setPhone("000-000-0000");
                            model.setEmail("test@test.com");
                            model.setTypeCode("TEST");
                            model.setTypeFont("{fas-user}");
                            model.setTypeText("테스트");
                            model.setRuntime("24/7 Run");
                            model.setRating("5.0");
                            list.add(model);
                        }
                        realm.copyToRealmOrUpdate(list);
                    }
                });
                break;
            case R.id.btn04:
                /**
                 * Data Query All
                 */
                RealmResults<Example_Model_Address> result01 = realm.where(Example_Model_Address.class).findAll();
                break;
            case R.id.btn05:
                /**
                 * Data Query Filter
                 */
                RealmResults<Example_Model_Address> result02 = realm.where(Example_Model_Address.class).equalTo("oid", "ChIJ-6VoBtZv5kcRQ4U5AxyIEk0").findAll();
                Example_Model_Address result03 = realm.where(Example_Model_Address.class).equalTo("oid", "ChIJ-6VoBtZv5kcRQ4U5AxyIEk0").findFirst();
                break;
            case R.id.btn06:
                /**
                 * Data Query Filter Advance
                 */
                RealmQuery<Example_Model_Address> query = realm.where(Example_Model_Address.class);
                query.equalTo("oid", "ChIJ-6VoBtZv5kcRQ4U5AxyIEk0");
                //result04.contains("oid", "ChIJ");
                //result04.in("oid", new String[]{"ChIJ-6VoBtZv5kcRQ4U5AxyIEk0", "xxxx-asdfFQWEREZAVA"});

                RealmResults<Example_Model_Address> result04 = query.findAll();
                break;
            case R.id.btn07:
                /**
                 * Data Query Sort
                 */
                RealmResults<Example_Model_Address> result07 = realm.where(Example_Model_Address.class).sort("name", Sort.ASCENDING).findAll();
                break;
            case R.id.btn08:
                /**
                 * Data Query & Write(Modify)
                 */
                Example_Model_Address result08 = realm.where(Example_Model_Address.class).equalTo("name", "TEST").findFirst();
                if(result08 != null){
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            result08.setDesc("Test Desc Modified");
                            realm.copyToRealmOrUpdate(result08);
                        }
                    });
                }
                break;
            case R.id.btn09:
                /**
                 * Data Delete Single Row
                 */
                Example_Model_Address result09 = realm.where(Example_Model_Address.class).equalTo("name", "TEST").findFirst();
                if(result09 != null){
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            result09.deleteFromRealm();
                        }
                    });
                }
                break;
            case R.id.btn10:
                /**
                 * Data Delete all
                 */
                RealmResults<Example_Model_Address> result10 = realm.where(Example_Model_Address.class).findAll();
                if(result10 != null){
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            result10.deleteAllFromRealm();
                        }
                    });
                }
                break;
            case R.id.btn11:
                /**
                 * All Model Data Delete
                 */
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.deleteAll();
                    }
                });
                break;
            default:
                break;

        }
    }
}

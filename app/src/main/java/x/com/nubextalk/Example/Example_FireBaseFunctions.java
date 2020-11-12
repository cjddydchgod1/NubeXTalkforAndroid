package x.com.nubextalk.Example;

import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;
import x.com.nubextalk.Manager.FireBase.FirebaseFunctionsManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.R;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Firebase Function 예제
 *  - FirebaseFunctionsManager.java 에 Function 별 기능 처리하여 호출하는 방식
 *  - OnComplete으로 Request가 성공했는지 Callback Listener
 */
public class Example_FireBaseFunctions extends AppCompatActivity implements View.OnClickListener {

    Realm realm;
    Button buttonFunctions;
    TextView txtResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example_fire_base_functions);

        realm = Realm.getInstance(UtilityManager.getRealmConfig());
        buttonFunctions = findViewById(R.id.btn);
        txtResults = findViewById(R.id.txtResult);

        buttonFunctions.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn:
                Config myProfile = realm.where(Config.class).equalTo("CODE", "USER_ME").findFirst();
                if(myProfile != null){
                    String txt = "Function Request : 'executeTest'";
                    txtResults.setText(txt);

                    FirebaseFunctionsManager.executeTest(myProfile.getExt1());
                }
                break;
        }
    }
}

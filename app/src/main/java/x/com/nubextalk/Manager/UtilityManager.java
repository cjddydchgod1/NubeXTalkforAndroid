package x.com.nubextalk.Manager;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.IOException;
import java.io.InputStream;

import io.realm.RealmConfiguration;
import x.com.nubextalk.R;


public class UtilityManager {

    public static RealmConfiguration getRealmConfig(){
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        return config;
    }

    public static String loadJson(Context context, String fileName) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open("json/"+fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return jsonString;
    }

    public static Boolean checkString(String s){
        if(s == null){ return false; }
        if(s.isEmpty()) { return false; }
        if(s.equals("")) { return false; }
        if(s.equals("null")) { return false; }
        return true;
    }

    public static int checkNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return 1;

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return 2;
        }
        return 0;
    }

    public static MaterialDialog showLoadingDialog(Context context){
        return new MaterialDialog.Builder(context)
                .title(R.string.dlg_title_load)
                .content(R.string.dlg_desc_load)
                .progress(true, 0)
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show();
    }

    public static MaterialDialog dismissLoadingDlg(MaterialDialog dialog){
        if(dialog == null){ return null; }
        dialog.dismiss();
        return null;
    }

    public static int dpToPx(Context ctx, int val) {
        return (int) (val * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(Context ctx, int val) {
        return (int) (val / Resources.getSystem().getDisplayMetrics().density);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}

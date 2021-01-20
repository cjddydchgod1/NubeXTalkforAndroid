/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aquery.AQuery;

import co.moonmonkeylabs.realmsearchview.RealmSearchAdapter;
import co.moonmonkeylabs.realmsearchview.RealmSearchViewHolder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.R;

public class ChatAddSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater mInflater;
    private Realm realm;
    private Context context;
    private OnItemSelectedListner clickListener;
    private AQuery aq;

    public void setItemSelectedListener(OnItemSelectedListner listener) {
        this.clickListener = listener;
    }

    public interface OnItemSelectedListner {
        void onItemSelected(User user);
    }

    public ChatAddSearchAdapter(Context context, Realm realm) {
        this.realm = realm;
        this.context = context;
        this.aq = new AQuery(context);
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vh = mInflater.inflate(R.layout.item_friend, parent, false);
        return new ViewItemHolder(vh);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    public class ViewItemHolder extends RecyclerView.ViewHolder {
        public TextView profileName;
        public CircleImageView profileImage;
        public ImageView profileStatus;

        public ViewItemHolder(View itemView) {
            super(itemView);
            profileName = itemView.findViewById(R.id.profileName);
            profileImage = itemView.findViewById(R.id.profileImage);
            profileStatus = itemView.findViewById(R.id.profileStatus);
        }

    }

    @Override
    public int getItemCount() {
        return 0;
    }







//    @Override
//    public void onBindViewHolder(ViewItemHolder viewHolder, int position) {
//        if (viewHolder instanceof ViewItemHolder){
//            User user = realmResults.get(position);
//            viewHolder.profileName.setText(user.getAppName());
//            if (URLUtil.isValidUrl(user.getAppImagePath())) {
//                aq.view(viewHolder.profileImage).image(user.getAppImagePath());
//            } else {
//                aq.view(viewHolder.profileImage).image(R.drawable.baseline_account_circle_black_24dp);
//
//            }
//            switch (user.getAppStatus()) {
//                case "1":
//                    aq.view(viewHolder.profileStatus).image(R.drawable.baseline_fiber_manual_record_yellow_50_24dp);
//                    break;
//                case "2":
//                    aq.view(viewHolder.profileStatus).image(R.drawable.baseline_fiber_manual_record_red_800_24dp);
//                    break;
//                default: // 0과 기본으로 되어있는 설정
//                    aq.view(viewHolder.profileStatus).image(R.drawable.baseline_fiber_manual_record_teal_a400_24dp);
//                    break;
//            }
//            viewHolder.itemView.setOnClickListener(v -> {
//                if (clickListener != null) {
//                    clickListener.onItemSelected(user);
//                }
//            });
//        }
//    }



    /**
     * RealmSearchAdapter 에 있는 filter 함수 중 findAllSorted 함수가 현재 Realm 버전에서는
     * deprecated 된 함수여서 사용할 수 없기 때문에 오버라이딩하여 현재 Realm 버전에 맞게 수정함
     **/
//    @Override
//    public void filter(String input) {
//        RealmQuery<User> where = this.realm.where(User.class);
//        if (!input.isEmpty()) {
//            where = where.contains("appName", input);
//        }
//        RealmResults businesses;
//        businesses = where.sort("appName", Sort.ASCENDING).findAll();
//        this.updateRealmResults(businesses);
//    }
}

/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Adapter;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

public class ChatAddSearchAdapter extends RealmSearchAdapter<User, ChatAddSearchAdapter.ViewItemHolder> {
    private final LayoutInflater mInflater;
    private Realm realm;
    private Context context;
    private OnItemSelectedListner clickListener;
    private AQuery aq;

    public interface OnItemSelectedListner {
        void onItemSelected(User user);
    }

    public void setItemSelectedListener(OnItemSelectedListner listener) {
        this.clickListener = listener;
    }

    public ChatAddSearchAdapter(Context context, Realm realm, String fillterColumnName) {
        super(context, realm, fillterColumnName);
        this.realm = realm;
        this.context = context;
        this.aq = new AQuery(context);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewItemHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
        View vh = mInflater.inflate(R.layout.item_friend, viewGroup, false);
        return new ViewItemHolder(vh);
    }

    @Override
    public void onBindRealmViewHolder(ViewItemHolder viewHolder, int position) {
        if (viewHolder instanceof ViewItemHolder) {
            User user = realmResults.get(position);
            viewHolder.profileName.setText(user.getName());

            if (!user.getProfileImg().isEmpty()) {
                aq.view(viewHolder.profileImage).image(user.getProfileImg());
            } else {
                aq.view(viewHolder.profileImage).image(R.drawable.baseline_account_circle_black_24dp);

            }
            if (user.getStatus() == 0) { // 초록
                viewHolder.profileStatus.setImageResource(R.drawable.baseline_fiber_manual_record_teal_a400_24dp);
            } else if (user.getStatus() == 1) { // 빨강
                viewHolder.profileStatus.setImageResource(R.drawable.baseline_fiber_manual_record_red_800_24dp);
            } else { // 노랑
                viewHolder.profileStatus.setImageResource(R.drawable.baseline_fiber_manual_record_yellow_50_24dp);
            }

            viewHolder.itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemSelected(user);
                }
            });
        }
    }

    public class ViewItemHolder extends RealmSearchViewHolder {
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

    /**
     * RealmSearchAdapter 에 있는 filter 함수 중 findAllSorted 함수가 현재 Realm 버전에서는
     * deprecated 된 함수여서 사용할 수 없기 때문에 오버라이딩하여 현재 Realm 버전에 맞게 수정함
     **/
    @Override
    public void filter(String input) {
        RealmQuery<User> where = this.realm.where(User.class);
        if (!input.isEmpty()) {
            where = where.contains("name", input);
        }
        RealmResults businesses;
        businesses = where.sort("name", Sort.ASCENDING).findAll();
        this.updateRealmResults(businesses);
    }
}

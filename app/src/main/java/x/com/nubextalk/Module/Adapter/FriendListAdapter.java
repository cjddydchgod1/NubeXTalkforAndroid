/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Adapter;

import android.content.Context;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aquery.AQuery;

import java.util.ArrayList;

import x.com.nubextalk.Model.User;
import static x.com.nubextalk.Module.CodeResources.NON_RADIO;
import static x.com.nubextalk.Module.CodeResources.RADIO;
import x.com.nubextalk.R;

public class FriendListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<User> mDataSet;
    private Context mContext;
    private onItemSelectedListener listener;
    private AQuery aq;
    private int sel_type;
    private int mLastCheckedPosition = -1;

    public interface onItemSelectedListener{
        void onSelected(User address);
    }

    public void setOnItemSelectedListener(onItemSelectedListener listener){
        this.listener = listener;
    }

    public FriendListAdapter(Context context, ArrayList<User> data, AQuery aq, int sel_type) {
        this.mDataSet = data;
        this.mContext = context;
        this.aq = aq;
        this.sel_type = sel_type;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView;
        mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend,parent,false);
        return new FriendViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User mCurrent = mDataSet.get(position);
        if(holder instanceof FriendViewHolder) {
            FriendViewHolder friendViewHolder = (FriendViewHolder) holder;
            friendViewHolder.bintTo(mCurrent);
            if(sel_type == RADIO)
                friendViewHolder.radioButton.setChecked(mLastCheckedPosition == position);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }


    public class FriendViewHolder extends RecyclerView.ViewHolder {
        private final TextView profileName;
        private final ImageView profileImage;
        private final ImageView profileStatus;
        private final RadioButton radioButton;
        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            // item_friend.xml에서 불러온다.
            profileName     = itemView.findViewById(R.id.profileName);
            profileImage    = itemView.findViewById(R.id.profileImage);
            profileStatus   = itemView.findViewById(R.id.profileStatus);
            radioButton     = itemView.findViewById(R.id.select_user);
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        int copyLastCheckedPosition = mLastCheckedPosition;
                        mLastCheckedPosition = getAdapterPosition();
                        if(sel_type == RADIO) {
                            notifyItemChanged(copyLastCheckedPosition);
                            notifyItemChanged(mLastCheckedPosition);
                        }
                        listener.onSelected(mDataSet.get(mLastCheckedPosition));
                    }
                }
            };
            itemView.setOnClickListener(clickListener);
            radioButton.setOnClickListener(clickListener);
        }
        public void bintTo(User user) {
            if(sel_type == RADIO)
                radioButton.setVisibility(View.VISIBLE);
            profileName.setText(user.getAppName());
            if(URLUtil.isValidUrl(user.getAppImagePath())){
                aq.view(profileImage).image(user.getAppImagePath());
            } else {
                aq.view(profileImage).image(R.drawable.baseline_account_circle_black_24dp);
            }
            // 초록
            switch(user.getAppStatus()) {
                case "1" :
                    aq.view(profileStatus).image(R.drawable.baseline_fiber_manual_record_yellow_50_24dp);
                    break;
                case "2" :
                    aq.view(profileStatus).image(R.drawable.baseline_fiber_manual_record_red_800_24dp);
                    break;
                default :
                    aq.view(profileStatus).image(R.drawable.baseline_fiber_manual_record_teal_a400_24dp);
                    break;
            }
        }
    }
}

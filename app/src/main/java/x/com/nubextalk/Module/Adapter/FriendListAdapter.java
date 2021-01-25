/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Adapter;

import android.content.Context;
import android.provider.MediaStore;
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
import x.com.nubextalk.Module.Case.FriendlistCase;
import x.com.nubextalk.R;

public class FriendListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<User> mDataSet;
    private Context mContext;
    private onItemSelectedListener listener;
    private AQuery aq;
    private FriendlistCase sel_type;
    private String TAG = "FriendListAdapter";

    public interface onItemSelectedListener{
        void onSelected(User address, RadioButton radioButton);
        void onSelected(User address);
    }

    public void setOnItemSelectedListener(onItemSelectedListener listener){
        this.listener = listener;
    }

    public FriendListAdapter(Context context, ArrayList<User> data, AQuery aq, FriendlistCase sel_type) {
        this.mDataSet = data;
        this.mContext = context;
        this.aq = aq;
        this.sel_type = sel_type;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView;
        if(sel_type == FriendlistCase.NON_RADIO) {
            mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend,parent,false);
            return new FriendViewHolder(mItemView);
        } else if(sel_type == FriendlistCase.RADIO) {
            mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_radiobutton,parent,false);
            return new FriendRadioViewHolder(mItemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User mCurrent = mDataSet.get(position);
        if(holder instanceof FriendViewHolder) {
            FriendViewHolder friendViewHolder = (FriendViewHolder) holder;
            friendViewHolder.bintTo(mCurrent);
            friendViewHolder.itemView.setOnClickListener(v -> {
                if(listener != null)
                    listener.onSelected(mCurrent);
            });
        } else if(holder instanceof FriendRadioViewHolder) {
            FriendRadioViewHolder friendRadioViewHolder = (FriendRadioViewHolder) holder;
            friendRadioViewHolder.bintTo(mCurrent);
            friendRadioViewHolder.itemView.setOnClickListener(v -> {
                if(listener != null)
                    listener.onSelected(mCurrent, friendRadioViewHolder.radioButton);
            });
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

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            // item_friend.xml에서 불러온다.
            profileName     = itemView.findViewById(R.id.profileName);
            profileImage    = itemView.findViewById(R.id.profileImage);
            profileStatus   = itemView.findViewById(R.id.profileStatus);
        }
        public void bintTo(User user) {
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

    public class FriendRadioViewHolder extends RecyclerView.ViewHolder {
        private final TextView profileName;
        private final ImageView profileImage;
        private final ImageView profileStatus;
        private final RadioButton radioButton;
        public FriendRadioViewHolder(@NonNull View itemView) {
            super(itemView);
            // item_friend.xml에서 불러온다.
            profileName     = itemView.findViewById(R.id.profileName);
            profileImage    = itemView.findViewById(R.id.profileImage);
            profileStatus   = itemView.findViewById(R.id.profileStatus);
            radioButton     = itemView.findViewById(R.id.select_user);
            radioButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSelected(mDataSet.get(getAdapterPosition()), radioButton);
                }
            });
        }
        public void bintTo(User user) {
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

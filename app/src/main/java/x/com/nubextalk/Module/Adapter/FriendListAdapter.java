/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aquery.AQuery;

import java.util.ArrayList;

import x.com.nubextalk.Model.User;
import x.com.nubextalk.Model.User2;
import x.com.nubextalk.R;

public class FriendListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<User2> mDataSet;
    private Context mContext;
    private String uid;
    private onItemSelectedListener listener;
    private AQuery aq;

    public interface onItemSelectedListener{
        void onSelected(User2 address);
    }
    public void setOnItemSelectedListener(onItemSelectedListener listener){
        this.listener = listener;
    }

    public FriendListAdapter(Context context, ArrayList<User2> data, String uid, AQuery aq) {
        this.mDataSet = data;
        this.mContext = context;
        this.uid = uid;
        this.aq = aq;
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
        // LinkedList에서 하나씩.
        User2 mCurrent = mDataSet.get(position);

        FriendViewHolder friendViewHolder = (FriendViewHolder) holder;
        friendViewHolder.bintTo(mCurrent);
        friendViewHolder.itemView.setOnClickListener(v -> {
            if(listener != null) {
                listener.onSelected(mDataSet.get(position));
            }
        });
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
            profileName = itemView.findViewById(R.id.profileName);
            profileImage = itemView.findViewById(R.id.profileImage);
            profileStatus = itemView.findViewById(R.id.profileStatus);
        }
        public void bintTo(User2 user) {
//            String name = user.getDepartment() + " ";
//            if(user.getNickname()==null) {
//                name += user.getName();
//            } else {
//                name += user.getNickname();
//            }
//            profileName.setText(name);
            profileName.setText(user.getAppName());
            if(!user.getAppImagePath().isEmpty()){
                aq.view(profileImage).image(user.getAppImagePath());
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

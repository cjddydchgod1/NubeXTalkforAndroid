/*
 * Created By JONGHO, LEE, Researcher of Institute of Biomedical Engineering, TechHeim Co.,Ltd.
 * on  2021.
 * This software is not stable health-care system and not certificated any health-care authority(like CE, FDA)
 * Prohibit redistribution, modification, commercial use.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aquery.AQuery;

import java.io.File;
import java.util.ArrayList;

import x.com.nubextalk.Manager.ImageManager;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.R;

import static x.com.nubextalk.Module.CodeResources.DEFAULT_PROFILE;
import static x.com.nubextalk.Module.CodeResources.STATUS_BUSY;
import static x.com.nubextalk.Module.CodeResources.STATUS_OFF;
import static x.com.nubextalk.Module.CodeResources.STATUS_ON;

public class FriendListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<User> mDataSet;
    private Context mContext;
    private onItemSelectedListener mClickListener;
    private AQuery mAquery;
    private boolean mIsRadio;
    private int mLastCheckedPosition = -1;

    private static String mProfilePath;

    public interface onItemSelectedListener {
        void onSelected(User address);
    }

    public void setOnItemSelectedListener(onItemSelectedListener listener) {
        this.mClickListener = listener;
    }

    public FriendListAdapter(Context context, ArrayList<User> data, AQuery aq, boolean isRadio) {
        this.mDataSet = data;
        this.mContext = context;
        this.mAquery = aq;
        this.mIsRadio = isRadio;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView;
        mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User mCurrent = mDataSet.get(position);
        if (holder instanceof FriendViewHolder) {
            FriendViewHolder friendViewHolder = (FriendViewHolder) holder;
            friendViewHolder.bintTo(mCurrent);
            if (mIsRadio)
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
            profileName = itemView.findViewById(R.id.profileName);
            profileImage = itemView.findViewById(R.id.profileImage);
            profileStatus = itemView.findViewById(R.id.profileStatus);
            radioButton = itemView.findViewById(R.id.select_user);
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mClickListener != null) {
                        int copyLastCheckedPosition = mLastCheckedPosition;
                        mLastCheckedPosition = getAdapterPosition();
                        if (mIsRadio) {
                            notifyItemChanged(copyLastCheckedPosition);
                            notifyItemChanged(mLastCheckedPosition);
                        }
                        mClickListener.onSelected(mDataSet.get(mLastCheckedPosition));
                    }
                }
            };
            itemView.setOnClickListener(clickListener);
            radioButton.setOnClickListener(clickListener);
        }

        public void bintTo(User user) {
            if (mIsRadio)
                radioButton.setVisibility(View.VISIBLE);
            profileName.setText(user.getAppName());
            if (URLUtil.isValidUrl(user.getAppImagePath())) {
                profileImage.setColorFilter(null);
                mAquery.view(profileImage).image(user.getAppImagePath());
            } else {
                mAquery.view(profileImage).image(DEFAULT_PROFILE);
                profileImage.setColorFilter(profileName.getTextColors().getDefaultColor());
            }

            switch (user.getAppStatus()) {
                case "1":
                    mAquery.view(profileStatus).image(STATUS_BUSY);
                    break;
                case "2":
                    mAquery.view(profileStatus).image(STATUS_OFF);
                    break;
                default:
                    mAquery.view(profileStatus).image(STATUS_ON);
                    break;
            }
        }
    }
}

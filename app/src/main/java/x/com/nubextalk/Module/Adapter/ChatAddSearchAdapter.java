/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aquery.AQuery;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.R;

import static x.com.nubextalk.Module.CodeResources.DEFAULT_PROFILE;
import static x.com.nubextalk.Module.CodeResources.EMPTY;
import static x.com.nubextalk.Module.CodeResources.STATUS_BUSY;
import static x.com.nubextalk.Module.CodeResources.STATUS_OFF;
import static x.com.nubextalk.Module.CodeResources.STATUS_ON;

public class ChatAddSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private AQuery mAquery;
    private LayoutInflater mInflater;
    private OnItemSelectedListener mClickListener;
    private ArrayList<User> mUserList;


    public interface OnItemSelectedListener {
        void onItemSelected(User user);
    }

    public void setItemSelectedListener(OnItemSelectedListener listener) {
        this.mClickListener = listener;
    }

    public ChatAddSearchAdapter(Context context, ArrayList<User> userRealmResults) {
        this.mContext = context;
        this.mUserList = userRealmResults;
        this.mAquery = new AQuery(context);
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.item_friend, parent, false);
        return new ViewItemHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewItemHolder mHolder = (ViewItemHolder) holder;
        User user = mUserList.get(position);

        //이름 설정
        if (user.getAppName() != null) {
            mHolder.profileName.setText(user.getAppName());
        } else {
            mHolder.profileName.setText(EMPTY);
        }

        //사진 설정
        if (URLUtil.isValidUrl(user.getAppImagePath())) {
            mAquery.view(mHolder.profileImage).image(user.getAppImagePath());
        } else {
            Drawable drawable = mHolder.profileImage.getContext().getResources().getDrawable(DEFAULT_PROFILE, null);
            mAquery.view(mHolder.profileImage).image(drawable);
        }

        //상태 설정
        switch (user.getAppStatus()) {
            case "1":
                mAquery.view(mHolder.profileStatus).image(STATUS_BUSY);
                break;
            case "2":
                mAquery.view(mHolder.profileStatus).image(STATUS_OFF);
                break;
            default: // 0과 기본으로 되어있는 설정
                mAquery.view(mHolder.profileStatus).image(STATUS_ON);
                break;
        }

        mHolder.itemView.setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.onItemSelected(user);
            }
        });
    }

    public class ViewItemHolder extends RecyclerView.ViewHolder {

        public TextView profileName;
        public ImageView profileStatus;
        public CircleImageView profileImage;
        public View userLayout;

        public ViewItemHolder(@NonNull View itemView) {
            super(itemView);
            profileName = itemView.findViewById(R.id.profileName);
            profileStatus = itemView.findViewById(R.id.profileStatus);
            profileImage = itemView.findViewById(R.id.profileImage);
            userLayout = itemView.findViewById(R.id.profileConstraintLayout);
        }
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public void addItem(@NonNull User user) {
        if (!mUserList.contains(user)) {
            mUserList.add(user);
            notifyDataSetChanged();
        }
    }

    public void deleteAllItem() {
        mUserList.clear();
        notifyDataSetChanged();
    }

    public void setItem(ArrayList<User> userList) {
        this.mUserList = userList;
        notifyDataSetChanged();
    }

}

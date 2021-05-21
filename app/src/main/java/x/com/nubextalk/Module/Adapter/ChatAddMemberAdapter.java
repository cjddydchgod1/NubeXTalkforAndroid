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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aquery.AQuery;
import com.joanzapata.iconify.widget.IconButton;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import x.com.nubextalk.ChatAddActivity;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.R;

import static x.com.nubextalk.Module.CodeResources.DEFAULT_PROFILE;
import static x.com.nubextalk.Module.CodeResources.ICON_MINUS;

public class ChatAddMemberAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private AQuery mAquery;
    private LayoutInflater mInflater;
    private ArrayList<ChatAddActivity.ChatAddActivityUser> mUserList;

    public ChatAddMemberAdapter(Context context, ArrayList<ChatAddActivity.ChatAddActivityUser> userList) {
        this.mInflater = LayoutInflater.from(context);
        this.mUserList = userList;
        this.mContext = context;
        this.mAquery = new AQuery(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        View mItemView = mInflater.inflate(R.layout.item_new_chat_added_profile, parent, false);
        return new ViewItemHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewItemHolder) {
            ViewItemHolder mHolder = (ViewItemHolder) holder;

            User user = mUserList.get(position).getUser();

            mHolder.profileName.setText(user.getAppName());

            if (URLUtil.isValidUrl(mUserList.get(position).getUser().getAppImagePath())) {
                mAquery.view(mHolder.profileImage).image(user.getAppImagePath());
            } else {
                Drawable drawable = mHolder.profileImage.getContext().getResources().getDrawable(DEFAULT_PROFILE, null);
                mAquery.view(mHolder.profileImage).image(drawable);
            }

            //기존 채팅방에서 가져온 사용자인 경우 아이템 삭제 버튼 사라짐
            if (mUserList.get(position).getIsAlreadyChatRoomUser()) {
                mHolder.deleteItemButton.setVisibility(View.GONE);
            } else {
                mHolder.deleteItemButton.setVisibility(View.VISIBLE);
                mHolder.deleteItemButton.setOnClickListener(v -> deleteItem(position));
            }


        }
    }

    public void addItem(@NonNull ChatAddActivity.ChatAddActivityUser chatRoomUserStatus) {

        //userList 아이템에 있는지 비교, 존재하면 아이템 중복 추가 방지
        for (ChatAddActivity.ChatAddActivityUser chatRoomUserStatus1 : mUserList) {
            if (chatRoomUserStatus1.getUser().getUid().equals(chatRoomUserStatus.getUser().getUid())) {
                return;
            }
        }
        mUserList.add(chatRoomUserStatus);
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        mUserList.remove(position);
        notifyDataSetChanged();
    }

    public static class ViewItemHolder extends RecyclerView.ViewHolder {
        public TextView profileName;
        public CircleImageView profileImage;
        public IconButton deleteItemButton;

        public ViewItemHolder(View itemView) {
            super(itemView);
            profileName = itemView.findViewById(R.id.new_chat_added_profile_name);
            profileImage = itemView.findViewById(R.id.new_chat_added_profile_img);
            deleteItemButton = itemView.findViewById(R.id.new_chat_added_delete_btn);
            deleteItemButton.setText(ICON_MINUS);
        }
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public ArrayList<ChatAddActivity.ChatAddActivityUser> getItemList() {
        return mUserList;
    }
}

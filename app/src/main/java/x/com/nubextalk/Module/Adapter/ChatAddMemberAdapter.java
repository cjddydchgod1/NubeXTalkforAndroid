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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aquery.AQuery;
import com.joanzapata.iconify.widget.IconButton;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import x.com.nubextalk.ChatAddActivity;
import x.com.nubextalk.R;

public class ChatAddMemberAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater mInflater;
    public ArrayList<ChatAddActivity.ChatAddActivityUser> userList;
    private Context context;
    private AQuery aq;

    public ChatAddMemberAdapter(Context context, ArrayList<ChatAddActivity.ChatAddActivityUser> userList) {
        this.mInflater = LayoutInflater.from(context);
        this.userList = userList;
        this.context = context;
        this.aq = new AQuery(context);
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

            mHolder.profileName.setText(userList.get(position).getUser().getAppName());

            if (URLUtil.isValidUrl(userList.get(position).getUser().getAppImagePath())) {
                aq.view(mHolder.profileImage).image(userList.get(position).getUser().getAppImagePath());
            } else {
                aq.view(mHolder.profileImage).image(R.drawable.baseline_account_circle_black_24dp);
            }

            //기존 채팅방에서 가져온 사용자인 경우 아이템 삭제 버튼 사라짐
            if (userList.get(position).getIsAlreadyChatRoomUser()) {
                mHolder.deleteItemButton.setVisibility(View.INVISIBLE);
            }

            mHolder.deleteItemButton.setOnClickListener(v -> deleteItem(position));
        }
    }

    public void addItem(@NonNull ChatAddActivity.ChatAddActivityUser chatRoomUserStatus) {

        //userList 아이템에 있는지 비교, 존재하면 아이템 중복 추가 방지
        for (ChatAddActivity.ChatAddActivityUser chatRoomUserStatus1 : userList) {
            if (chatRoomUserStatus1.getUser().getUserId().equals(chatRoomUserStatus.getUser().getUserId())) {
                return;
            }
        }
        userList.add(chatRoomUserStatus);
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        userList.remove(position);
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
            deleteItemButton.setText("{fas-times 10dp #ffffff}");
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public ArrayList<ChatAddActivity.ChatAddActivityUser> getItemList() {
        return userList;
    }
}

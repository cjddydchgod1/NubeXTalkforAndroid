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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import x.com.nubextalk.Model.User;
import x.com.nubextalk.R;

public class ChatAddMemberAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater mInflater;
    public ArrayList<User> userList;
    private Context context;

    public ChatAddMemberAdapter(Context context, ArrayList<User> userList) {
        this.mInflater = LayoutInflater.from(context);
        this.userList = userList;
        this.context = context;
    }

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
            mHolder.profileName.setText(userList.get(position).getName());
            Glide.with(mHolder.profileImage).
                    load(userList.get(position).getProfileImg()).
                    into(((ViewItemHolder) mHolder).profileImage);

            mHolder.itemView.setOnClickListener(v -> {
                deleteItem(userList.get(position));
            });

        }
    }

    public void addItem(@NonNull User user) {
        if (!userList.contains(user)) {
            userList.add(user);
            notifyDataSetChanged();
        }
    }

    public void deleteItem(@NonNull User user) {
        if (userList.contains(user)) {
            userList.remove(user);
            notifyDataSetChanged();
        }
    }

    public class ViewItemHolder extends RecyclerView.ViewHolder {

        public TextView profileName;
        public ImageView profileImage;

        public ViewItemHolder(View itemView) {
            super(itemView);
            profileName = itemView.findViewById(R.id.new_chat_added_profile_name);
            profileImage = itemView.findViewById(R.id.new_chat_added_profile_img);
            profileImage.setBackground(new ShapeDrawable(new OvalShape()));
            profileImage.setClipToOutline(true);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public ArrayList<User> getUserList() {
        return userList;
    }
}

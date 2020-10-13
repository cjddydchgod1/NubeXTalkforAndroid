/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Adapter;

// 채팅방 액티비티와 Chat class 어댑터

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;

import io.realm.Realm;
import io.realm.RealmResults;

import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private LayoutInflater mInflater;
    private User mUserData;
    private RealmResults<ChatContent> mChatData;

    private Context mContext;
    private String id ="1234"; //저장된 아이디를 가져와 넣을 예정
    private Realm realm = Realm.getDefaultInstance();

    private String mDate ="0000.00.00";




    public ChatAdapter(Context context, RealmResults<ChatContent>  mChatLog) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mChatData = mChatLog;

    }


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(
                R.layout.item_chat, parent, false);
        return new ChatViewHolder(mItemView, this);    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ChatViewHolder holder, int position) {

        String uid = mChatData.get(position).getUid();
        mUserData = realm.where(User.class).equalTo("uid", uid).findFirst();

        // 시간 형식 나누기
        SimpleDateFormat formatChatTime = new SimpleDateFormat("HH:mm");
        SimpleDateFormat formatChatDate = new SimpleDateFormat("yyyy.MM.dd (E)");

        String sendTime = formatChatTime.format(mChatData.get(position).getSendDate());
        String sendDate =formatChatDate.format(mChatData.get(position).getSendDate());

        // 날짜변경시 날짜 보여주기
        if(mDate.equals(sendDate)){
            holder.date.setVisibility(View.GONE);
        }
        else{
            mDate = sendDate;
            holder.date.setText(mDate);
        }

        // 아이디가 같은 경우 즉, 자신이 보낸 메세시의 경우 우측 하단에 표시
        if(mChatData.get(position).getUid().equals(this.id)){
            holder.my_chat.setText(mChatData.get(position).getContent());
            holder.my_time.setText(sendTime);
            holder.profileImage.setVisibility(View.GONE);
            holder.profileName.setVisibility(View.GONE);
            holder.other_chat.setVisibility(View.GONE);
            holder.other_time.setVisibility(View.GONE);

        }
        // 아이디가 다른 경우 , 즉 자신이 보낸 메세지가 아닌경우 좌측 하단에 표시
        else {

            Glide.with(this.mContext).load(mUserData.getProfileImg()).into(holder.profileImage);
            holder.profileName.setText(mUserData.getName());
            holder.other_chat.setText(mChatData.get(position).getContent());
            holder.other_time.setText(sendTime);

            holder.my_chat.setVisibility(View.GONE);
            holder.my_time.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChatData.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        public ImageView profileImage;
        public TextView profileName;
        public TextView other_chat;
        public TextView other_time;
        public TextView my_chat;
        public TextView my_time;
        public TextView date;
        final ChatAdapter mAdapter;

        public ChatViewHolder(@NonNull View itemView, ChatAdapter mAdapter) {
            super(itemView);
            date = itemView.findViewById(R.id.text_date);
            profileImage = itemView.findViewById(R.id.profile_image);
            profileName = itemView.findViewById(R.id.profile_name);
            other_chat = itemView.findViewById(R.id.other_chat_text);
            other_time = itemView.findViewById(R.id.other_time);
            my_chat = itemView.findViewById(R.id.my_chat_text);
            my_time = itemView.findViewById(R.id.my_time);

            this.mAdapter = mAdapter;
        }
    }
}

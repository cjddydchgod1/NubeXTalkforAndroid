/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Adapter;

// 채팅방 액티비티와 Chat class 어댑터

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aquery.AQuery;

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
    private String mDate ="0000.00.00";

    private Realm realm = Realm.getDefaultInstance();




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
        ChatContent chat = mChatData.get(position);
        if(chat.getContent() == null){
            return;
        }
        String uid = chat.getUid();
        mUserData = realm.where(User.class).equalTo("uid", uid).findFirst();
        Log.d("TAG",chat.getContent());

        // 시간 형식 나누기
        SimpleDateFormat formatChatTime = new SimpleDateFormat("HH:mm");
        SimpleDateFormat formatChatDate = new SimpleDateFormat("yyyy.MM.dd (E)");

        String sendTime = formatChatTime.format(chat.getSendDate());
        String sendDate =formatChatDate.format(chat.getSendDate());

        // 날짜변경시 날짜 보여주기
        if(mDate.equals(sendDate)){
            holder.date.setVisibility(View.GONE);
        }
        else{
            mDate = sendDate;
            holder.date.setText(mDate);
        }

        // 아이디가 같은 경우 즉, 자신이 보낸 메세시의 경우 우측 하단에 표시
        if(chat.getUid().equals(this.id)){
            switch (chat.getType()){
                case 0:
                    holder.my_chat_text.setText(chat.getContent());
                    holder.my_chat_image.setVisibility(View.GONE);
                    break;
                case 1:
                    holder.aq.id(R.id.my_chat_image).image(chat.getContent());
//                    Glide.with(this.mContext).load(chat.getContent()).override(500,300).into(holder.my_chat_image);
                    holder.my_chat_text.setVisibility(View.GONE);
                    break;
            }
            holder.my_time.setText(sendTime);

            holder.profileImage.setVisibility(View.GONE);
            holder.profileName.setVisibility(View.GONE);
            holder.other_chat_text.setVisibility(View.GONE);
            holder.other_time.setVisibility(View.GONE);

        }
        // 아이디가 다른 경우 , 즉 자신이 보낸 메세지가 아닌경우 좌측 하단에 표시
        else {
            holder.aq.id(R.id.profile_image).image(mUserData.getProfileImg());
            holder.profileName.setText(mUserData.getName());
            switch (chat.getType()){
                case 0:
                    holder.other_chat_text.setText(chat.getContent());
                    holder.other_chat_image.setVisibility(View.GONE);
                    break;
                case 1:
//                  holder.other_chat_image.setImageURI();
                    holder.other_chat_text.setVisibility(View.GONE);
                    break;
            }
            holder.other_time.setText(sendTime);

            holder.my_chat_text.setVisibility(View.GONE);
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
        public TextView other_chat_text;
        public ImageView other_chat_image;
        public TextView other_time;

        public TextView my_chat_text;
        public ImageView my_chat_image;
        public TextView my_time;
        public TextView date;
        final ChatAdapter mAdapter;

        private AQuery aq ;


        public ChatViewHolder(@NonNull View itemView, ChatAdapter mAdapter) {
            super(itemView);
            aq = new AQuery(itemView.getContext(), itemView);

            date = itemView.findViewById(R.id.text_date);
            profileImage = itemView.findViewById(R.id.profile_image);
            profileName = itemView.findViewById(R.id.profile_name);
            other_chat_text = itemView.findViewById(R.id.other_chat_text);
            other_chat_image = itemView.findViewById(R.id.other_chat_image);
            other_time = itemView.findViewById(R.id.other_time);

            my_chat_text = itemView.findViewById(R.id.my_chat_text);
            my_chat_image = itemView.findViewById(R.id.my_chat_image);
            my_time = itemView.findViewById(R.id.my_time);

            this.mAdapter = mAdapter;
        }
    }
}

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

import io.realm.RealmViewHolder;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.R;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mInflater;
    private User mUserData;
    private RealmResults<ChatContent> mChatData;
    private Context mContext;
    private String id ="1234"; //저장된 아이디를 가져와 넣을 예정
    private Realm realm = Realm.getInstance(UtilityManager.getRealmConfig());

    public ChatAdapter(Context context, RealmResults<ChatContent>  mChatLog) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mChatData = mChatLog;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0){
            View mItemView = mInflater.inflate(R.layout.item_chat, parent, false);
            return new ChatViewHolder(mItemView, this);
        }
        else{
            View mItemView = mInflater.inflate(R.layout.item_chat_media, parent, false);
            return new ChatMediaViewHolder(mItemView, this);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatContent chat = mChatData.get(position);
        if(chat.getContent() == null){
            return;
        }
        String uid = chat.getUid();
        mUserData = realm.where(User.class).equalTo("uid", uid).findFirst();

        // 시간 형식 나누기
        SimpleDateFormat formatChatTime = new SimpleDateFormat("HH:mm");
        SimpleDateFormat formatChatDate = new SimpleDateFormat("yyyy.MM.dd (E)");

        String sendTime = formatChatTime.format(chat.getSendDate());
        String sendDate =formatChatDate.format(chat.getSendDate());

        if(chat.getType() == 0){
            ChatViewHolder cvHolder = (ChatViewHolder) holder;
            if(chat.getFirst()){
                cvHolder.date.setText(sendDate);
                cvHolder.date.setVisibility(View.VISIBLE);
            }
            else{
                cvHolder.date.setVisibility(View.GONE);
            }

            // 아이디가 같은 경우 즉, 자신이 보낸 메세시의 경우 우측 하단에 표시
            if(chat.getUid().equals(this.id)){
                cvHolder.my_chat_text.setText(chat.getContent());
                cvHolder.my_time.setText(sendTime);

                cvHolder.my_chat_text.setVisibility(View.VISIBLE);
                cvHolder.my_time.setVisibility(View.VISIBLE);

                cvHolder.profileImage.setVisibility(View.GONE);
                cvHolder.profileName.setVisibility(View.GONE);
                cvHolder.other_chat_text.setVisibility(View.GONE);
                cvHolder.other_time.setVisibility(View.GONE);
            }
            // 아이디가 다른 경우 , 즉 자신이 보낸 메세지가 아닌경우 좌측 하단에 표시
            else {
                cvHolder.aq.id(R.id.profile_image).image(mUserData.getProfileImg());
                cvHolder.profileName.setText(mUserData.getName());
                cvHolder.other_chat_text.setText(chat.getContent());
                cvHolder.other_time.setText(sendTime);

                cvHolder.profileImage.setVisibility(View.VISIBLE);
                cvHolder.profileName.setVisibility(View.VISIBLE);
                cvHolder.other_chat_text.setVisibility(View.VISIBLE);
                cvHolder.other_time.setVisibility(View.VISIBLE);

                cvHolder.my_chat_text.setVisibility(View.GONE);
                cvHolder.my_time.setVisibility(View.GONE);
            }
        }
        else{
            ChatMediaViewHolder cvmHolder = (ChatMediaViewHolder) holder;
            if(chat.getFirst()){
                cvmHolder.date.setText(sendDate);
                cvmHolder.date.setVisibility(View.VISIBLE);
            }
            else{
                cvmHolder.date.setVisibility(View.GONE);
            }

            // 아이디가 같은 경우 즉, 자신이 보낸 메세시의 경우 우측 하단에 표시
            if(chat.getUid().equals(this.id)){
                cvmHolder.aq.id(R.id.my_chat_image).image(chat.getContent());
                cvmHolder.my_time.setText(sendTime);

                cvmHolder.my_chat_image.setVisibility(View.VISIBLE);
                cvmHolder.my_time.setVisibility(View.VISIBLE);

                cvmHolder.profileImage.setVisibility(View.GONE);
                cvmHolder.profileName.setVisibility(View.GONE);
                cvmHolder.other_time.setVisibility(View.GONE);
                cvmHolder.other_chat_image.setVisibility(View.GONE);
            }
            // 아이디가 다른 경우 , 즉 자신이 보낸 메세지가 아닌경우 좌측 하단에 표시
            else {
                cvmHolder.aq.id(R.id.profile_image).image(mUserData.getProfileImg());
                cvmHolder.profileName.setText(mUserData.getName());
                cvmHolder.aq.id(R.id.other_chat_image).image(chat.getContent());
                cvmHolder.other_time.setText(sendTime);

                cvmHolder.profileImage.setVisibility(View.VISIBLE);
                cvmHolder.profileName.setVisibility(View.VISIBLE);
                cvmHolder.other_time.setVisibility(View.VISIBLE);
                cvmHolder.other_chat_image.setVisibility(View.VISIBLE);

                cvmHolder.my_chat_image.setVisibility(View.GONE);
                cvmHolder.my_time.setVisibility(View.GONE);

            }
        }
    }

    @Override
    public int getItemCount() {
        return mChatData.size();
    }
    @Override
    public int getItemViewType(int position){
        ChatContent chat = mChatData.get(position);
        int type = chat.getType();
        if(type == 0){
            return 0;
        }
        else{
            return 1;
        }
    }

    public void update(RealmResults<ChatContent> data) {
        this.mChatData = data;
        notifyDataSetChanged();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        public ImageView profileImage;
        public TextView profileName;
        public TextView other_chat_text;
        public TextView other_time;

        public TextView my_chat_text;
        public TextView my_time;
        public TextView date;
        final ChatAdapter mAdapter;

        private AQuery aq ;


        public ChatViewHolder(@NonNull View itemView, ChatAdapter mAdapter){
            super(itemView);
            aq = new AQuery(itemView.getContext(), itemView);

            date = itemView.findViewById(R.id.text_date);
            profileImage = itemView.findViewById(R.id.profile_image);
            profileName = itemView.findViewById(R.id.profile_name);
            other_chat_text = itemView.findViewById(R.id.other_chat_text);
            other_time = itemView.findViewById(R.id.other_time);

            my_chat_text = itemView.findViewById(R.id.my_chat_text);
            my_time = itemView.findViewById(R.id.my_time);

            this.mAdapter = mAdapter;
        }
    }
    public static class ChatMediaViewHolder extends RecyclerView.ViewHolder {
        public ImageView profileImage;
        public TextView profileName;
        public ImageView other_chat_image;
        public TextView other_time;

        public ImageView my_chat_image;
        public TextView my_time;
        public TextView date;
        final ChatAdapter mAdapter;

        private AQuery aq ;


        public ChatMediaViewHolder(@NonNull View itemView, ChatAdapter mAdapter) {
            super(itemView);
            aq = new AQuery(itemView.getContext(), itemView);

            date = itemView.findViewById(R.id.text_date);
            profileImage = itemView.findViewById(R.id.profile_image);
            profileName = itemView.findViewById(R.id.profile_name);
            other_chat_image = itemView.findViewById(R.id.other_chat_image);
            other_time = itemView.findViewById(R.id.other_time);

            my_chat_image = itemView.findViewById(R.id.my_chat_image);
            my_time = itemView.findViewById(R.id.my_time);
            this.mAdapter = mAdapter;
        }
    }
}

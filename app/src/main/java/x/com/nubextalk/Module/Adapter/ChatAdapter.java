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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

import x.com.nubextalk.Manager.FireBase.FirebaseStorageManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.R;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Realm realm;

    private LayoutInflater mInflater;
    private User mUserData;
    private RealmResults<ChatContent> mChatData;
    private Context mContext;
    private String mUid;

    public ChatAdapter(Context context, RealmResults<ChatContent>  mChatLog) {
        this.realm = Realm.getInstance(UtilityManager.getRealmConfig());

        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mChatData = mChatLog;
        this.mUid = UtilityManager.getUid();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0){
            View mItemView = mInflater.inflate(R.layout.item_chat, parent, false);
            return new ChatViewHolder(mItemView, this);
        }
        else if(viewType == 1){
            View mItemView = mInflater.inflate(R.layout.item_chat_media, parent, false);
            return new ChatMediaViewHolder(mItemView, this);
        }
        else{
            View mItemView = mInflater.inflate(R.layout.item_chat_system,parent,false);
            return new ChatSystemViewHolder(mItemView,this);
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
            if(chat.getUid().equals(this.mUid)){
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
        else if(chat.getType() == 1){
            ChatMediaViewHolder cmvHolder = (ChatMediaViewHolder) holder;
            if(chat.getFirst()){
                cmvHolder.date.setText(sendDate);
                cmvHolder.date.setVisibility(View.VISIBLE);
            }
            else{
                cmvHolder.date.setVisibility(View.GONE);
            }
            if(chat.getUid().equals(mUid)){
                cmvHolder.aq.id(R.id.my_chat_image).image(chat.getContent());
                cmvHolder.my_time.setText(sendTime);
                cmvHolder.my_chat_image.setVisibility(View.VISIBLE);
                cmvHolder.my_time.setVisibility(View.VISIBLE);
                cmvHolder.profileImage.setVisibility(View.GONE);
                cmvHolder.profileName.setVisibility(View.GONE);
                cmvHolder.other_time.setVisibility(View.GONE);
                cmvHolder.other_chat_image.setVisibility(View.GONE);
            }
            // 아이디가 다른 경우 , 즉 자신이 보낸 메세지가 아닌경우 좌측 하단에 표시
            else {
                cmvHolder.aq.id(R.id.profile_image).image(mUserData.getProfileImg());
                cmvHolder.profileName.setText(mUserData.getName());
                cmvHolder.aq.id(R.id.other_chat_image).image(chat.getContent());
                cmvHolder.other_time.setText(sendTime);
                cmvHolder.profileImage.setVisibility(View.VISIBLE);
                cmvHolder.profileName.setVisibility(View.VISIBLE);
                cmvHolder.other_time.setVisibility(View.VISIBLE);
                cmvHolder.other_chat_image.setVisibility(View.VISIBLE);
                cmvHolder.my_chat_image.setVisibility(View.GONE);
                cmvHolder.my_time.setVisibility(View.GONE);
            }
        }
        else{
            ChatSystemViewHolder csvHolder = (ChatSystemViewHolder) holder;//채팅방 내용이 없으면 그냥 만들면 first == true 일듯
            if(chat.getFirst()){
                csvHolder.date.setText(sendDate);
                csvHolder.date.setVisibility(View.VISIBLE);
            }
            else{
                csvHolder.date.setVisibility(View.GONE);
            }
            csvHolder.chat_system.setText(chat.getContent());
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
        else if(type == 1){
            return 1;
        }
        else{
            return 9;
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

            date = itemView.findViewById(R.id.chat_date);
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

            date = itemView.findViewById(R.id.chat_date);
            profileImage = itemView.findViewById(R.id.profile_image);
            profileName = itemView.findViewById(R.id.profile_name);
            other_chat_image = itemView.findViewById(R.id.other_chat_image);
            other_time = itemView.findViewById(R.id.other_time);

            my_chat_image = itemView.findViewById(R.id.my_chat_image);
            my_time = itemView.findViewById(R.id.my_time);
            this.mAdapter = mAdapter;
        }
    }
    public static class ChatSystemViewHolder extends RecyclerView.ViewHolder {
        public TextView chat_system;
        public TextView date;

        final ChatAdapter mAdapter;


        public ChatSystemViewHolder(@NonNull View itemView, ChatAdapter mAdapter) {
            super(itemView);
            chat_system = itemView.findViewById(R.id.chat_system);
            date = itemView.findViewById(R.id.chat_date);
            this.mAdapter = mAdapter;
        }
    }

}

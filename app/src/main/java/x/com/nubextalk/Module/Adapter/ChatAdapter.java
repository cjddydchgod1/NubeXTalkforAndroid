/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Adapter;

// 채팅방 액티비티와 Chat class 어댑터

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.aquery.AQuery;

import java.text.SimpleDateFormat;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import x.com.nubextalk.ChatImageActivity;
import x.com.nubextalk.ImageViewActivity;
import x.com.nubextalk.Manager.DateManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.Config;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.R;

import static x.com.nubextalk.Module.CodeResources.DATE_FINAL;
import static x.com.nubextalk.Module.CodeResources.DATE_FORMAT_1;
import static x.com.nubextalk.Module.CodeResources.DATE_FORMAT_2;
import static x.com.nubextalk.Module.CodeResources.DATE_FORMAT_3;
import static x.com.nubextalk.Module.CodeResources.EMPTY;
import static x.com.nubextalk.Module.CodeResources.EMPTY_IMAGE;
import static x.com.nubextalk.Module.CodeResources.SENDING;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Realm realm;

    private LayoutInflater mInflater;
    private Context mContext;
    private User mUserData;
    private RealmResults<ChatContent> mChatData;
    private String mUid;

    public ChatAdapter(Context context, RealmResults<ChatContent> mChatLog) {
        this.realm = Realm.getInstance(UtilityManager.getRealmConfig());

        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mChatData = mChatLog;
        this.mUid = Config.getMyUID(realm);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View mItemView = mInflater.inflate(R.layout.item_chat, parent, false);
            return new ChatViewHolder(mItemView, this);
        } else if (viewType == 1) {
            View mItemView = mInflater.inflate(R.layout.item_chat_media, parent, false);
            return new ChatMediaViewHolder(mItemView, this);
        } else if (viewType == 2) {
            View mItemView = mInflater.inflate(R.layout.item_chat_pacs, parent, false);
            return new ChatPacsViewHolder(mItemView, this);
        } else {
            View mItemView = mInflater.inflate(R.layout.item_chat_system, parent, false);
            return new ChatSystemViewHolder(mItemView, this);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatContent chat = mChatData.get(position);
        String preSenderId = (position > 0) ? mChatData.get(position - 1).getUid() : mUid;
        if (chat == null) {
            return;
        }
        String uid = chat.getUid();
        mUserData = realm.where(User.class).equalTo("userId", uid).findFirst();

        // 시간 형식 나누기
        SimpleDateFormat formatChatTime = new SimpleDateFormat(DATE_FORMAT_1);
        SimpleDateFormat formatChatDate = new SimpleDateFormat(DATE_FORMAT_2);

        String sendDate;
        String sendTime;

        if (DateManager.convertDate(chat.getSendDate(), DATE_FORMAT_3).equals(DATE_FINAL)) {
            sendTime = SENDING;
            sendDate = EMPTY;
        } else {
            sendTime = formatChatTime.format(chat.getSendDate());
            sendDate = formatChatDate.format(chat.getSendDate());
        }

        if (chat.getType() == 0) {
            ChatViewHolder cvHolder = (ChatViewHolder) holder;
            if (chat.getFirst()) {
                cvHolder.date.setText(sendDate);
                cvHolder.date.setVisibility(View.VISIBLE);
            } else {
                cvHolder.date.setVisibility(View.GONE);
            }

            // 아이디가 같은 경우 즉, 자신이 보낸 메세시의 경우 우측 하단에 표시
            if (chat.getUid().equals(mUid)) {
                cvHolder.my_chat_text.setText(chat.getContent());
                cvHolder.myTime.setText(sendTime);

                cvHolder.my_chat_text.setVisibility(View.VISIBLE);
                cvHolder.myTime.setVisibility(View.VISIBLE);

                cvHolder.profileImage.setVisibility(View.GONE);
                cvHolder.profileName.setVisibility(View.GONE);
                cvHolder.other_chat_text.setVisibility(View.GONE);
                cvHolder.otherTime.setVisibility(View.GONE);
            }
            // 아이디가 다른 경우 , 즉 자신이 보낸 메세지가 아닌경우 좌측 하단에 표시
            else {
                cvHolder.aq.id(R.id.profile_image).image(mUserData.getAppImagePath());
                cvHolder.profileName.setText(mUserData.getAppName());
                cvHolder.other_chat_text.setText(chat.getContent());
                cvHolder.otherTime.setText(sendTime);

                if (chat.getUid().equals(preSenderId)) {
                    cvHolder.profileImage.setVisibility(View.GONE);
                    cvHolder.profileName.setVisibility(View.GONE);
                } else {
                    cvHolder.profileImage.setVisibility(View.VISIBLE);
                    cvHolder.profileName.setVisibility(View.VISIBLE);
                }
                cvHolder.other_chat_text.setVisibility(View.VISIBLE);
                cvHolder.otherTime.setVisibility(View.VISIBLE);

                cvHolder.my_chat_text.setVisibility(View.GONE);
                cvHolder.myTime.setVisibility(View.GONE);
            }
        } else if (chat.getType() == 1) {
            ChatMediaViewHolder cmvHolder = (ChatMediaViewHolder) holder;
            if (chat.getFirst()) {
                cmvHolder.date.setText(sendDate);
                cmvHolder.date.setVisibility(View.VISIBLE);
            } else {
                cmvHolder.date.setVisibility(View.GONE);
            }

            if (chat.getUid().equals(mUid)) {
                cmvHolder.aq.id(R.id.my_chat_image).image(chat.getContent());

                cmvHolder.myTime.setText(sendTime);
                cmvHolder.myTime.setVisibility(View.VISIBLE);
                cmvHolder.myChatImg.setVisibility(View.VISIBLE);

                if (!chat.getContent().equals(EMPTY_IMAGE)) {
                    cmvHolder.myChatImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, ChatImageActivity.class);
                            intent.putExtra("cid", chat.getCid());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            mContext.startActivity(intent);
                        }
                    });
                }

                cmvHolder.profileImage.setVisibility(View.GONE);
                cmvHolder.profileName.setVisibility(View.GONE);
                cmvHolder.otherTime.setVisibility(View.GONE);
                cmvHolder.otherChatImg.setVisibility(View.GONE);
            }
            // 아이디가 다른 경우 , 즉 자신이 보낸 메세지가 아닌경우 좌측 하단에 표시
            else {
                cmvHolder.aq.id(R.id.profile_image).image(mUserData.getAppImagePath());
                cmvHolder.aq.id(R.id.other_chat_image).image(chat.getContent());

                cmvHolder.profileName.setText(mUserData.getAppName());
                cmvHolder.otherTime.setText(sendTime);
                if (chat.getUid().equals(preSenderId)) {
                    cmvHolder.profileImage.setVisibility(View.GONE);
                    cmvHolder.profileName.setVisibility(View.GONE);

                } else {
                    cmvHolder.profileImage.setVisibility(View.VISIBLE);
                    cmvHolder.profileName.setVisibility(View.VISIBLE);
                }
                cmvHolder.otherTime.setVisibility(View.VISIBLE);
                cmvHolder.otherChatImg.setVisibility(View.VISIBLE);
                cmvHolder.otherChatImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ChatImageActivity.class);
                        intent.putExtra("cid", chat.getCid());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        mContext.startActivity(intent);
                    }
                });


                cmvHolder.myChatImg.setVisibility(View.GONE);
                cmvHolder.myTime.setVisibility(View.GONE);
            }
        } else if (chat.getType() == 2) {
            ChatPacsViewHolder cpvHolder = (ChatPacsViewHolder) holder;
            if (chat.getFirst()) {
                cpvHolder.date.setText(sendDate);
                cpvHolder.date.setVisibility(View.VISIBLE);
            } else {
                cpvHolder.date.setVisibility(View.GONE);
            }
            if (chat.getUid().equals(mUid)) {
                cpvHolder.myTime.setText(sendTime);
                cpvHolder.myPacsDescription.setText(chat.getContent());
                cpvHolder.myChatPacs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ImageViewActivity.class);
                        intent.putExtra("studyId", chat.getExt1());
                        Log.d("PACS", chat.getExt1());
                        mContext.startActivity(intent);
                    }
                });

                cpvHolder.myChatPacs.setVisibility(View.VISIBLE);
                cpvHolder.myTime.setVisibility(View.VISIBLE);

                cpvHolder.profileImage.setVisibility(View.GONE);
                cpvHolder.profileName.setVisibility(View.GONE);
                cpvHolder.otherTime.setVisibility(View.GONE);
                cpvHolder.otherChatPacs.setVisibility(View.GONE);
            }
            // 아이디가 다른 경우 , 즉 자신이 보낸 메세지가 아닌경우 좌측 하단에 표시
            else {
                cpvHolder.aq.id(R.id.profile_image).image(mUserData.getAppImagePath());
                cpvHolder.profileName.setText(mUserData.getAppName());
                cpvHolder.otherTime.setText(sendTime);
                cpvHolder.otherPacsDescription.setText(chat.getContent());
                cpvHolder.otherChatPacs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ImageViewActivity.class);
                        intent.putExtra("studyId", chat.getExt1());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        mContext.startActivity(intent);
                    }
                });


                if (chat.getUid().equals(preSenderId)) {
                    cpvHolder.profileImage.setVisibility(View.GONE);
                    cpvHolder.profileName.setVisibility(View.GONE);
                } else {
                    cpvHolder.profileImage.setVisibility(View.VISIBLE);
                    cpvHolder.profileName.setVisibility(View.VISIBLE);
                }
                cpvHolder.otherTime.setVisibility(View.VISIBLE);
                cpvHolder.otherChatPacs.setVisibility(View.VISIBLE);


                cpvHolder.myChatPacs.setVisibility(View.GONE);
                cpvHolder.myTime.setVisibility(View.GONE);
            }

        } else {
            ChatSystemViewHolder csvHolder = (ChatSystemViewHolder) holder;//채팅방 내용이 없으면 그냥 만들면 first == true 일듯
            if (chat.getFirst()) {
                csvHolder.date.setText(sendDate);
                csvHolder.date.setVisibility(View.VISIBLE);
            } else {
                csvHolder.date.setVisibility(View.GONE);
            }
            csvHolder.chatSystem.setText(chat.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return mChatData.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatContent chat = mChatData.get(position);
        int type = chat.getType();
        if (type == 0) {
            return 0;
        } else if (type == 1) {
            return 1;
        } else if (type == 2) {
            return 2;
        } else {
            return 9;
        }
    }

    public void update() {
        notifyDataSetChanged();
        this.mChatData = this.mChatData.sort("sendDate", Sort.ASCENDING);
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        final ChatAdapter mAdapter;
        private AQuery aq;

        public ImageView profileImage;
        public TextView profileName;
        public TextView other_chat_text;
        public TextView otherTime;

        public TextView my_chat_text;
        public TextView myTime;
        public TextView date;

        public ChatViewHolder(@NonNull View itemView, ChatAdapter mAdapter) {
            super(itemView);
            this.mAdapter = mAdapter;
            this.aq = new AQuery(itemView.getContext(), itemView);

            date = itemView.findViewById(R.id.chat_date);
            profileImage = itemView.findViewById(R.id.profile_image);
            profileName = itemView.findViewById(R.id.profile_name);
            other_chat_text = itemView.findViewById(R.id.other_chat_text);
            otherTime = itemView.findViewById(R.id.other_time);

            my_chat_text = itemView.findViewById(R.id.my_chat_text);
            myTime = itemView.findViewById(R.id.my_time);
        }
    }

    public static class ChatMediaViewHolder extends RecyclerView.ViewHolder {
        final ChatAdapter mAdapter;
        private AQuery aq;

        public ImageView profileImage;
        public TextView profileName;
        public ImageView otherChatImg;
        public TextView otherTime;

        public ImageView myChatImg;
        public TextView myTime;
        public TextView date;

        public ChatMediaViewHolder(@NonNull View itemView, ChatAdapter mAdapter) {
            super(itemView);
            this.mAdapter = mAdapter;
            this.aq = new AQuery(itemView.getContext(), itemView);

            date = itemView.findViewById(R.id.chat_date);
            profileImage = itemView.findViewById(R.id.profile_image);
            profileName = itemView.findViewById(R.id.profile_name);
            otherChatImg = itemView.findViewById(R.id.other_chat_image);
            otherTime = itemView.findViewById(R.id.other_time);

            myChatImg = itemView.findViewById(R.id.my_chat_image);
            myTime = itemView.findViewById(R.id.my_time);
        }
    }

    public static class ChatPacsViewHolder extends RecyclerView.ViewHolder {
        final ChatAdapter mAdapter;
        private AQuery aq;

        public ImageView profileImage;
        public TextView profileName;
        public ConstraintLayout otherChatPacs;
        public TextView otherPacsDescription;
        public Button otherPacsButton;
        public TextView otherTime;

        public ConstraintLayout myChatPacs;
        public TextView myPacsDescription;
        public Button myPacsButton;

        public TextView myTime;
        public TextView date;


        public ChatPacsViewHolder(@NonNull View itemView, ChatAdapter mAdapter) {
            super(itemView);
            this.mAdapter = mAdapter;
            this.aq = new AQuery(itemView.getContext(), itemView);

            date = itemView.findViewById(R.id.chat_date);
            profileImage = itemView.findViewById(R.id.profile_image);
            profileName = itemView.findViewById(R.id.profile_name);
            otherChatPacs = itemView.findViewById(R.id.other_chat_pacs);
            otherPacsDescription = itemView.findViewById(R.id.other_pacs_description);
            otherPacsButton = itemView.findViewById(R.id.other_pacs_button);
            otherTime = itemView.findViewById(R.id.other_time);

            myChatPacs = itemView.findViewById(R.id.my_chat_pacs);
            myPacsDescription = itemView.findViewById(R.id.my_pacs_description);
            myPacsButton = itemView.findViewById(R.id.my_pacs_button);
            myTime = itemView.findViewById(R.id.my_time);


        }
    }

    public static class ChatSystemViewHolder extends RecyclerView.ViewHolder {
        final ChatAdapter mAdapter;

        public TextView chatSystem;
        public TextView date;

        public ChatSystemViewHolder(@NonNull View itemView, ChatAdapter mAdapter) {
            super(itemView);
            this.mAdapter = mAdapter;

            chatSystem = itemView.findViewById(R.id.chat_system);
            date = itemView.findViewById(R.id.chat_date);
        }
    }

}

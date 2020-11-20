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

import java.text.SimpleDateFormat;

import io.realm.Realm;
import io.realm.RealmResults;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.R;

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    SimpleDateFormat df = new SimpleDateFormat("MM월 dd일 HH:mm");
    private RealmResults<ChatRoom> mDataset;
    private final LayoutInflater mInflater;
    private Realm realm;
    private Context context;
    private OnItemLongSelectedListener longClickListener;
    private OnItemSelectedListener clickListener;

    public interface OnItemSelectedListener {
        void onItemSelected(ChatRoom chatRoom);
    }

    public interface OnItemLongSelectedListener {
        void onItemLongSelected(ChatRoom chatRoom);
    }

    public void setItemSelectedListener(OnItemSelectedListener listener) {
        this.clickListener = listener;
    }

    public void setItemLongSelectedListener(OnItemLongSelectedListener listener) {
        this.longClickListener = listener;
    }

    public ChatListAdapter(Context context, RealmResults<ChatRoom> mChatList) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mDataset = mChatList;
        this.realm = Realm.getInstance(UtilityManager.getRealmConfig());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        View mItemView = mInflater.inflate(
                R.layout.item_chatlist, parent, false);
        return new ViewItemHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewItemHolder) {
            ViewItemHolder mHolder = (ViewItemHolder) holder;

            ChatContent content;
            String roomId = mDataset.get(position).getRid();
            content = realm.where(ChatContent.class).equalTo("rid", roomId).findFirst();
            Glide.with(mHolder.profileImg).
                    load(mDataset.get(position).getRoomImg()).
                    into(((ViewItemHolder) mHolder).profileImg);
            mHolder.friendName.setText(mDataset.get(position).getRoomName());
            if (content != null) {
                mHolder.lastMsg.setText(content.getContent());
                mHolder.time.setText(df.format(content.getSendDate()));
                setStatusImg(mHolder, position);
                setChatNotify(mHolder, position);
                setChatFixTop(mHolder, position);
            }
            mHolder.itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onItemLongSelected(mDataset.get(position));
                }
                return false;
            });

            mHolder.itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemSelected(mDataset.get(position));
                }
            });
        }
    }

    public class ViewItemHolder extends RecyclerView.ViewHolder {

        public TextView lastMsg;
        public TextView friendName;
        public TextView time;
        public TextView remain;
        public ImageView profileImg;
        public ImageView statusImg;
        public ImageView notifyImg;

        public ImageView fixTopImg;
        public View chatLayout;

        public ViewItemHolder(View itemView) {
            super(itemView);
            lastMsg = itemView.findViewById(R.id.chat_list_last_message);
            friendName = itemView.findViewById(R.id.chat_list_friend_name);
            time = itemView.findViewById(R.id.chat_list_chat_time);
            remain = itemView.findViewById(R.id.chat_list_chat_remain);
            profileImg = itemView.findViewById(R.id.chat_list_chat_picture);
            profileImg.setBackground(new ShapeDrawable(new OvalShape()));
            profileImg.setClipToOutline(true);
            statusImg = itemView.findViewById(R.id.chat_list_friend_status);
            notifyImg = itemView.findViewById(R.id.chat_list_notify_status);
            fixTopImg = itemView.findViewById(R.id.chat_list_fixtop_status);
            chatLayout = itemView.findViewById(R.id.chat_list_layout);
        }

    }


    public void setChatFixTop(@NonNull ViewItemHolder holder, int position) {
        if (!mDataset.get(position).getSettingFixTop()) {
            holder.fixTopImg.setVisibility(View.INVISIBLE);
        } else {
            holder.fixTopImg.setVisibility(View.VISIBLE);
        }
    }

    public void setChatNotify(@NonNull ViewItemHolder holder, int position) {
        if (!mDataset.get(position).getSettingAlarm()) {
            holder.notifyImg.setVisibility(View.VISIBLE);
        } else {
            holder.notifyImg.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 채팅방 타입이 1대1 채팅방인 경우에는 대화 상대방의 상태가 보여야하고
     * 채팅방 타입이 단체방인 경우에는 어떻게 하지?
     **/
    public void setStatusImg(@NonNull ViewItemHolder holder, int position) {
//        if (mDataset.get(position).getStatus() == 0) {
//            holder.statusImg.setImageResource(R.drawable.oval_status_off);
//        } else if (mDataset.get(position).getStatus() == 1) {
//            holder.statusImg.setImageResource(R.drawable.oval_status_on);
//        }
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

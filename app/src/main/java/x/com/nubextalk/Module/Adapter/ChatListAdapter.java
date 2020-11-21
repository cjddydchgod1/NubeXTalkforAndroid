/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;
import x.com.nubextalk.ChatList;
import x.com.nubextalk.ChatRoomActivity;
import x.com.nubextalk.MainActivity;
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

    public void setItemSelectedListener(OnItemSelectedListener listener){
        this.clickListener = listener;
    }

    public void setItemLongSelectedListener(OnItemLongSelectedListener listener) {
        this.longClickListener = listener;
    }

    public ChatListAdapter(Context context, RealmResults<ChatRoom> mChatList) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mDataset = mChatList;
        realm = Realm.getDefaultInstance();
//        sortChatList(mDataset);
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
            mHolder.friendName.setText(mDataset.get(position).getRoomName());
            mHolder.lastMsg.setText(content.getContent());
            mHolder.time.setText(df.format(content.getSendDate()));

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
            setStatusImg(mHolder, position);
            setChatNotify(mHolder, position);
            setChatFixTop(mHolder, position);
        }
    }

    public class ViewItemHolder extends RecyclerView.ViewHolder {

        public TextView lastMsg;
        public TextView friendName;
        public TextView time;
        public TextView remain;
        public CircleImageView profileImg;
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

    public void setStatusImg(@NonNull ViewItemHolder holder, int position) {
//        if (mDataset.get(position).getStatus() == 0) {
//            holder.statusImg.setImageResource(R.drawable.oval_status_off);
//        } else if (mDataset.get(position).getStatus() == 1) {
//            holder.statusImg.setImageResource(R.drawable.oval_status_on);
//        }
        holder.statusImg.setImageResource(R.drawable.oval_status_off);
    }

    public void sortChatList(LinkedList<ChatList> chatList) {
        Collections.sort(chatList, new Comparator<ChatList>() {
            @Override
            public int compare(ChatList o1, ChatList o2) {
                if (!o1.getFixTop() && !o2.getFixTop()) { //o1, o2 둘 다 상단 고정 아닐 때
                    if (o1.getTime().after(o2.getTime())) return -1; //o1가 o2보다 시간이 최신일 때
                    else return +1;
                } else if (!o1.getFixTop() && o2.getFixTop()) { //o1은 상단 고정 아니고 o2는 상단 고정일 때
                    return +1;
                } else if (o1.getFixTop() && !o2.getFixTop()) { //o1은 상단 고정 o2는 상단 고정 아닐 때
                    return -1;
                } else if (o1.getFixTop() && o2.getFixTop()) { //o1, o2 둘 다 상단 고정일 때
                    if (o1.getTime().after(o2.getTime())) return -1; //o1가 o2보다 시간이 최신일 때
                    else return +1;
                }
                return 0;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

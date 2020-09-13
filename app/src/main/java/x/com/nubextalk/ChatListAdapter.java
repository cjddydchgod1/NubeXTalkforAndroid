package x.com.nubextalk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {
    private final LinkedList<ChatListActivity.ChatList> mDataset;
    private final LayoutInflater mInflater;

    private Context context;

    public ChatListAdapter(Context context, LinkedList<ChatListActivity.ChatList> mChatList) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mDataset = mChatList;

    }

    public static class ChatListViewHolder extends RecyclerView.ViewHolder {
        public TextView lastMsg;
        public TextView friendName;
        public TextView time;
        public TextView remain;
        public CircleImageView profileImg;
        public ImageView statusImg;
        public View chatLayout;
        final ChatListAdapter mAdapter;

        public ChatListViewHolder(View itemView, ChatListAdapter adapter) {
            super(itemView);
            lastMsg = itemView.findViewById(R.id.chat_last_message);
            friendName = itemView.findViewById(R.id.chat_name);
            time = itemView.findViewById(R.id.chat_time);
            remain = itemView.findViewById(R.id.chat_remain);
            profileImg = itemView.findViewById(R.id.chat_picture);
            statusImg = itemView.findViewById(R.id.friend_status);
            chatLayout = itemView.findViewById(R.id.chat_list_layout);
            this.mAdapter = adapter;
        }
    }

    @NonNull
    @Override
    public ChatListAdapter.ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                 int viewType) {
        View mItemView = mInflater.inflate(
                R.layout.item_chatlist, parent, false);
        return new ChatListViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {
        holder.profileImg.setImageResource(mDataset.get(position).getProfileUrl());
        holder.friendName.setText(mDataset.get(position).getName());
        holder.lastMsg.setText(mDataset.get(position).getMsg());
        holder.time.setText(mDataset.get(position).getTime());
        holder.remain.setText(mDataset.get(position).getRemain());
        holder.statusImg.setImageResource(mDataset.get(position).getStatusUrl());
        holder.chatLayout.setOnClickListener((view) -> {
            Toast.makeText(context, holder.friendName.getText(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

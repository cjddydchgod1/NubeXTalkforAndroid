package x.com.nubextalk;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {
    SimpleDateFormat df = new SimpleDateFormat("MM월 dd일 HH:mm");
    private LinkedList<ChatList> mDataset;
    private final LayoutInflater mInflater;

    private Context context;

    public ChatListAdapter(Context context, LinkedList<ChatList> mChatList) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mDataset = mChatList;
        sortChatList(mDataset);
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder {

        public TextView lastMsg;
        public TextView friendName;
        public TextView time;
        public TextView remain;
        public CircleImageView profileImg;
        public ImageView statusImg;
        public ImageView notifyImg;
        public ImageView fixTopImg;
        public View chatLayout;
        final ChatListAdapter mAdapter;

        public ChatListViewHolder(View itemView, ChatListAdapter adapter) {
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
        class ChatListLongClickMenu implements View.OnCreateContextMenuListener {
            private int pos;
            private boolean notify;
            private boolean fixTop;

            public ChatListLongClickMenu(ChatListViewHolder holder, int position) {
                holder.chatLayout.setOnCreateContextMenuListener(this);
                this.pos = position;
                this.notify = mDataset.get(pos).getNotify();
                this.fixTop = mDataset.get(pos).getFixTop();
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                MenuItem EditAlarm = null;
                if (notify) {
                    EditAlarm = menu.add(Menu.NONE, 1001, 1, "알림끄기");
                } else if (!notify) {
                    EditAlarm = menu.add(Menu.NONE, 1001, 1, "알림켜기");
                }
                MenuItem AddChat = menu.add(Menu.NONE, 1002, 2, "대화상대 추가");

                MenuItem EditTopPin = null;
                if (fixTop) {
                    EditTopPin = menu.add(Menu.NONE, 1003, 3, "상단 고정 해제");
                } else if (!fixTop) {
                    EditTopPin = menu.add(Menu.NONE, 1003, 3, "상단 고정");
                }

                MenuItem Delete = menu.add(Menu.NONE, 1004, 4, "나가기");

                EditAlarm.setOnMenuItemClickListener(onEditMenu);
                AddChat.setOnMenuItemClickListener(onEditMenu);
                EditTopPin.setOnMenuItemClickListener(onEditMenu);
                Delete.setOnMenuItemClickListener(onEditMenu);
            }

            private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case 1001: //채팅방 알림설정
                            setContextChatNotify();
                            break;
                        case 1002: //채팅방 대화상대 추가
                            break;
                        case 1003: //채팅방 상단고정
                            setContextChatFixTop();
                            break;
                        case 1004: //채팅방 나가기
                            delContextChat();
                            break;
                    }
                    return true;
                }

                public void delContextChat() {
                    mDataset.remove(pos);
                    holder.mAdapter.notifyItemRemoved(pos);
                    holder.mAdapter.notifyDataSetChanged();
                }

                public void setContextChatFixTop() {
                    if (fixTop) {
                        mDataset.get(pos).setFixTop(false);
                        sortChatList(mDataset);

                    } else if (!fixTop) {
                        mDataset.get(pos).setFixTop(true);
                        ChatList chatItem = mDataset.get(pos);
                        mDataset.remove(pos);
                        mDataset.add(0, chatItem);
                        holder.mAdapter.notifyItemMoved(pos, 0);
                        sortChatList(mDataset);
                    }
                    holder.mAdapter.notifyDataSetChanged();
                }

                public void setContextChatNotify() {
                    if (notify) {
                        mDataset.get(pos).setNotify(false);
                    } else if (!notify) {
                        mDataset.get(pos).setNotify(true);
                    }
                    holder.mAdapter.notifyDataSetChanged();
                }
            };
        }
        if (!mDataset.isEmpty()) {
            new ChatListLongClickMenu(holder, position);
            holder.profileImg.setImageResource(mDataset.get(position).getProfileUrl());
            holder.friendName.setText(mDataset.get(position).getName());
            holder.lastMsg.setText(mDataset.get(position).getMsg());
            holder.time.setText(df.format(mDataset.get(position).getTime()));
            holder.remain.setText(mDataset.get(position).getRemain());

            setStatusImg(holder, position);
            setChatNotify(holder, position);
            setChatFixTop(holder, position);
            setChatIntent(holder);
        }


    }

    public void setChatIntent(@NonNull ChatListViewHolder holder) {
        holder.chatLayout.setOnClickListener((view) -> {
            Toast.makeText(context, holder.friendName.getText(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        });
    }

    public void setChatFixTop(@NonNull ChatListViewHolder holder, int position) {
        if (!mDataset.get(position).getFixTop()) {
            holder.fixTopImg.setVisibility(View.INVISIBLE);
        } else {
            holder.fixTopImg.setVisibility(View.VISIBLE);
        }
    }

    public void setChatNotify(@NonNull ChatListViewHolder holder, int position) {
        if (!mDataset.get(position).getNotify()) {
            holder.notifyImg.setVisibility(View.VISIBLE);
        } else {
            holder.notifyImg.setVisibility(View.INVISIBLE);
        }
    }

    public void setStatusImg(@NonNull ChatListViewHolder holder, int position) {
        if (mDataset.get(position).getStatus() == 0) {
            holder.statusImg.setImageResource(R.drawable.oval_status_off);
        } else if (mDataset.get(position).getStatus() == 1) {
            holder.statusImg.setImageResource(R.drawable.oval_status_on);
        }
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

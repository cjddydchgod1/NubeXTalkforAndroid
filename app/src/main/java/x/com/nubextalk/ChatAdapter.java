package x.com.nubextalk;

// 채팅방 액티비티와 Chat class 어댑터

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private LayoutInflater mInflater;
    private LinkedList<Chat> mChatData; // 리스트로 채팅 데이터를 가져옴
    private Context mContext;
    private int id =1234; //저장된 아이디를 가져와 넣을 예정



    public ChatAdapter(Context context, LinkedList<Chat> mChatLog) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mChatData = mChatLog;

    }
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(
                R.layout.chat_item, parent, false);
        return new ChatViewHolder(mItemView, this);    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ChatViewHolder holder, int position) {
        // 아이디가 같은 경우 즉, 자신이 보낸 메세시의 경우 우측 하단에 표시
        if(mChatData.get(position).getId() == this.id){
            holder.my_chat.setText(mChatData.get(position).getChat());
            holder.my_time.setText(mChatData.get(position).getTime());

            holder.profileImage.setVisibility(View.GONE);
            holder.profileName.setVisibility(View.GONE);
            holder.other_chat.setVisibility(View.GONE);
            holder.other_time.setVisibility(View.GONE);

        }
        // 아이디가 다른 경우 , 즉 자신이 보낸 메세지가 아닌경우 좌측 하단에 표시
        else {
            holder.profileImage.setImageResource(mChatData.get(position).getProfileImage());
            holder.profileName.setText(mChatData.get(position).getProfile_name());
            holder.other_chat.setText(mChatData.get(position).getChat());
            holder.other_time.setText(mChatData.get(position).getTime());

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
        final ChatAdapter mAdapter;

        public ChatViewHolder(@NonNull View itemView, ChatAdapter mAdapter) {
            super(itemView);
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

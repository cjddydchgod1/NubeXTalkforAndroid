/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aquery.AQuery;
import com.joanzapata.iconify.widget.IconButton;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import x.com.nubextalk.Manager.DateManager;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.ChatContent;
import x.com.nubextalk.Model.ChatRoom;
import x.com.nubextalk.Model.ChatRoomMember;
import x.com.nubextalk.Model.User;
import x.com.nubextalk.R;

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private RealmResults<ChatRoom> mDataset;
    private final LayoutInflater mInflater;
    private Realm realm;
    private Context context;
    private OnItemLongSelectedListener longClickListener;
    private OnItemSelectedListener clickListener;
    private AQuery aq;

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
        this.aq = new AQuery(context);
        this.realm = Realm.getInstance(UtilityManager.getRealmConfig());
        sortChatRoomByDate();

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
            String dataPattern = "yyyy년 MM월 dd일 HH:mm";
            ViewItemHolder mHolder = (ViewItemHolder) holder;
            ChatContent lastContent;

            String roomId = mDataset.get(position).getRid();
            ChatRoom chatRoom = realm.where(ChatRoom.class).equalTo("rid", roomId).findFirst();
            String roomImgUrl = mDataset.get(position).getRoomImg();
            lastContent = realm.where(ChatContent.class) // 이 방식은 나중에 채팅 메세지가 많아지면 별로 좋은 방법이 아니므로 ChatRoom 에 lastContentId 를 넣는건 어떨
                    .equalTo("rid", roomId)
                    .sort("sendDate", Sort.DESCENDING).findFirst();

            if (URLUtil.isValidUrl(roomImgUrl)) {
                aq.view(mHolder.profileImg).image(roomImgUrl);
            } else {
                if (chatRoom.getChatRoomUsers(realm, roomId).size() > 2) {
                    aq.view(mHolder.profileImg).image(R.drawable.ic_twotone_group_24);
                } else {
                    aq.view(mHolder.profileImg).image(R.drawable.baseline_account_circle_black_24dp);
                }
            }
            mHolder.friendName.setText(mDataset.get(position).getRoomName());

            if (lastContent != null) { //채팅방 내용 있는 경우
                if (lastContent.getType() == 1) { //사진, 동영상 파일
                    mHolder.lastMsg.setText("새 사진");
                } else {
                    mHolder.lastMsg.setText(lastContent.getContent());
                }

                String convertedDate = DateManager.convertDate(lastContent.getSendDate(), dataPattern);
                mHolder.time.setText(DateManager.getTimeInterval(convertedDate, dataPattern));

                setStatusImg(mHolder, position);
                setNotify(mHolder, position);

            } else { // 채팅방 내용 없는 경우 (주로 처음 새로 만들었을 때)
                mHolder.lastMsg.setText("");
                String convertedDate = DateManager.convertDate(
                        mDataset.get(position).getUpdatedDate(), dataPattern);
                mHolder.time.setText(DateManager.getTimeInterval(convertedDate, dataPattern));

                setStatusImg(mHolder, position);
                setNotify(mHolder, position);
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

            setUnreadMessage(mHolder, position);
        }
    }

    public class ViewItemHolder extends RecyclerView.ViewHolder {

        public TextView lastMsg;
        public TextView friendName;
        public TextView time;
        public TextView remain;
        public CircleImageView profileImg;
        public ImageView statusImg;
        public IconButton notifyImg1;
        public IconButton notifyImg2;
        public View chatLayout;

        public ViewItemHolder(View itemView) {
            super(itemView);
            lastMsg = itemView.findViewById(R.id.chat_list_last_message);
            friendName = itemView.findViewById(R.id.chat_list_friend_name);
            time = itemView.findViewById(R.id.chat_list_chat_time);
            remain = itemView.findViewById(R.id.chat_list_chat_remain);
            profileImg = itemView.findViewById(R.id.chat_list_chat_picture);
            statusImg = itemView.findViewById(R.id.chat_list_friend_status);
            notifyImg1 = itemView.findViewById(R.id.chat_list_notify1);
            notifyImg2 = itemView.findViewById(R.id.chat_list_notify2);
            chatLayout = itemView.findViewById(R.id.chat_list_layout);
        }

    }

    /**
     * ChatRoom Acitivity 에서 chatContent 를 만들 때의 sendDate 와  해당 chatRoom 의 updatedDate 도 맞춰
     * 줘야함.
     **/
    public void sortChatRoomByDate() {
        this.mDataset = this.mDataset.sort("settingFixTop", Sort.DESCENDING,
                "updatedDate", Sort.DESCENDING);
    }

    public void setNotify(@NonNull ViewItemHolder holder, int position) {
        boolean fixTop;
        boolean alarm;
        String fixTopIcon = "{fas-map-pin 16dp}";
        String alarmOff = "{far-bell-slash 16dp}";

        fixTop = mDataset.get(position).getSettingFixTop(); //default false
        alarm = mDataset.get(position).getSettingAlarm(); //default true

        if (fixTop && alarm) { // 상단 고정만인 경우
            holder.notifyImg1.setText(fixTopIcon);
            holder.notifyImg2.setText("");
        } else if (!fixTop && !alarm) { // 알림 해제만 한 경우
            holder.notifyImg1.setText(alarmOff);
            holder.notifyImg2.setText("");
        } else if (fixTop && !alarm) { // 상단 고정 + 알림 해제 한 경우
            holder.notifyImg1.setText(fixTopIcon);
            holder.notifyImg2.setText(alarmOff);
        } else { //기본 상태일 때
            holder.notifyImg1.setText("");
            holder.notifyImg2.setText("");
        }
    }

    public void setUnreadMessage(@NonNull ViewItemHolder holder, int position) {
        int unreadMessages = 0;
        ChatRoom chatRoom = mDataset.get(position);
        RealmResults<ChatContent> chatContents;
        chatContents = realm.where(ChatContent.class).equalTo("rid", chatRoom.getRid()).findAll();

        for (ChatContent chatContent : chatContents) {
            if (!chatContent.getIsRead()) {
                unreadMessages += 1;
            }
        }

        if (unreadMessages == 0) {
            holder.remain.setText("");
            holder.remain.setVisibility(View.INVISIBLE);
        } else {
            holder.remain.setText(Integer.toString(unreadMessages));
            holder.remain.setVisibility(View.VISIBLE);

        }

    }


    /**
     * 채팅방 타입이 1대1 채팅방인 경우에는 대화 상대방의 상태가 보여야하는데 내가 누구인지 알아야 상대방 상태 표시 가
     * 채팅방 타입이 단체방인 경우에는 어떻게 하지?
     **/
    public void setStatusImg(@NonNull ViewItemHolder holder, int position) {
        String rid = mDataset.get(position).getRid();
        User myAccount = (User) User.getMyAccountInfo(realm);
        RealmResults<ChatRoomMember> users = ChatRoom.getChatRoomUsers(realm, rid);
        Log.d("CHATROOM", "멤버수: " + users.size());
        if (users.size() == 2) { // 1 대 1 채팅방인 경우
            for (ChatRoomMember user : users) {
                if (!user.getUid().equals(myAccount.getUserId())) {
                    // profilestatus
                    User anotherUser = realm.where(User.class).equalTo("userId", user.getUid()).findFirst();
                    switch (anotherUser.getAppStatus()) {
                        case "1":
                            aq.view(holder.statusImg).image(R.drawable.baseline_fiber_manual_record_yellow_50_24dp);
                            break;
                        case "2":
                            aq.view(holder.statusImg).image(R.drawable.baseline_fiber_manual_record_red_800_24dp);
                            break;
                        default: // 0과 기본으로 되어있는 설정
                            aq.view(holder.statusImg).image(R.drawable.baseline_fiber_manual_record_teal_a400_24dp);
                            break;
                    }
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

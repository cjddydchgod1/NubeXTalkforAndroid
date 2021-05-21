/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.RadioButton;
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

import static x.com.nubextalk.Module.CodeResources.DATE_FORMAT4;
import static x.com.nubextalk.Module.CodeResources.DEFAULT_GROUP_PROFILE;
import static x.com.nubextalk.Module.CodeResources.DEFAULT_PROFILE;
import static x.com.nubextalk.Module.CodeResources.EMPTY;
import static x.com.nubextalk.Module.CodeResources.ICON_ALARM_OFF;
import static x.com.nubextalk.Module.CodeResources.ICON_FIX_TOP;
import static x.com.nubextalk.Module.CodeResources.PICTURE;
import static x.com.nubextalk.Module.CodeResources.STATUS_BUSY;
import static x.com.nubextalk.Module.CodeResources.STATUS_OFF;
import static x.com.nubextalk.Module.CodeResources.STATUS_ON;

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private RealmResults<ChatRoom> mDataset;
    private LayoutInflater mInflater;
    private Realm mRealm;
    private Context mContext;
    private OnItemLongSelectedListener mLongClickListener;
    private OnItemSelectedListener mClickListener;
    private AQuery mAquery;
    private boolean mIsRadio;
    private int mLastCheckedPosition = -1;

    public interface OnItemSelectedListener {
        void onItemSelected(ChatRoom chatRoom);
    }

    public interface OnItemLongSelectedListener {
        void onItemLongSelected(ChatRoom chatRoom);
    }

    public void setItemSelectedListener(OnItemSelectedListener listener) {
        this.mClickListener = listener;
    }

    public void setItemLongSelectedListener(OnItemLongSelectedListener listener) {
        this.mLongClickListener = listener;
    }

    public ChatListAdapter(Context context, RealmResults<ChatRoom> mChatList, boolean isRadio) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mDataset = mChatList;
        this.mAquery = new AQuery(context);
        this.mRealm = Realm.getInstance(UtilityManager.getRealmConfig());
        this.mIsRadio = isRadio;
        sortChatRoomByDate();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.item_chatlist, parent, false);
        return new ViewItemHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatRoom mCurrent = mDataset.get(position);

        if (holder instanceof ViewItemHolder) {
            ViewItemHolder viewItemHolder = (ViewItemHolder) holder;
            viewItemHolder.bindTo(mCurrent);

            if (mIsRadio)
                viewItemHolder.setVisible();
            viewItemHolder.radioButton.setChecked(mLastCheckedPosition == position);
        }
    }

    public class ViewItemHolder extends RecyclerView.ViewHolder {

        public TextView lastMsg;
        public TextView chatRoomName;
        public TextView time;
        public TextView remain;
        public TextView memberCount;
        public CircleImageView chatRoomImg;
        public ImageView statusImg;
        public IconButton notifyImg1;
        public IconButton notifyImg2;
        public View chatLayout;
        public RadioButton radioButton;

        public ViewItemHolder(View itemView) {
            super(itemView);
            lastMsg = itemView.findViewById(R.id.chat_list_last_message);
            chatRoomName = itemView.findViewById(R.id.chat_list_friend_name);
            time = itemView.findViewById(R.id.chat_list_chat_time);
            remain = itemView.findViewById(R.id.chat_list_chat_remain);
            chatRoomImg = itemView.findViewById(R.id.chat_list_chat_picture);
            statusImg = itemView.findViewById(R.id.chat_list_friend_status);
            memberCount = itemView.findViewById(R.id.chat_member_count);
            notifyImg1 = itemView.findViewById(R.id.chat_list_notify1);
            notifyImg2 = itemView.findViewById(R.id.chat_list_notify2);
            chatLayout = itemView.findViewById(R.id.chat_list_layout);
            radioButton = itemView.findViewById(R.id.select_chat);


            //채팅방 목록 아이템 누르면 발생 이벤트
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mClickListener != null) {
                        int copyLastCheckedPosition = mLastCheckedPosition;
                        mLastCheckedPosition = getAdapterPosition();
                        if (mIsRadio) {
                            notifyItemChanged(copyLastCheckedPosition);
                            notifyItemChanged(mLastCheckedPosition);
                        }
                        mClickListener.onItemSelected(mDataset.get(mLastCheckedPosition));
                    }
                }
            };
            itemView.setOnClickListener(clickListener);
            radioButton.setOnClickListener(clickListener);

            View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mLongClickListener != null)
                        mLongClickListener.onItemLongSelected(mDataset.get(getAdapterPosition()));
                    return false;
                }
            };
            itemView.setOnLongClickListener(longClickListener);
        }

        public void setVisible() {
            remain.setVisibility(View.GONE);
            time.setVisibility(View.GONE);
            radioButton.setVisibility(View.VISIBLE);
        }

        public void bindTo(ChatRoom chatroom) {
            String roomId = chatroom.getRid();
            int roomMemberCount = ChatRoom.getChatRoomUsers(mRealm, roomId).size();
            String roomImgUrl = chatroom.getRoomImg();
            ChatContent lastContent = mRealm.where(ChatContent.class) // 이 방식은 나중에 채팅 메세지가 많아지면 별로 좋은 방법이 아니므로 ChatRoom 에 lastContentId 를 넣는건 어떨
                    .equalTo("rid", roomId)
                    .sort("sendDate", Sort.DESCENDING).findFirst();

            //아이템 데이터 초기화
            String datePattern = DATE_FORMAT4;

            //채팅방 목록 사진 설정
            if (URLUtil.isValidUrl(roomImgUrl)) {
                mAquery.view(chatRoomImg).image(roomImgUrl);
            } else {
                if (roomMemberCount > 2) {
                    mAquery.view(chatRoomImg).image(DEFAULT_GROUP_PROFILE);
                } else {
                    mAquery.view(chatRoomImg).image(DEFAULT_PROFILE);
                }
                chatRoomImg.setColorFilter(lastMsg.getTextColors().getDefaultColor());
            }

            //채팅방 목록 이름 설정
            chatRoomName.setText(chatroom.getRoomName());

            //채팅방 목록에 보이는 채팅메세지 설정
            if (lastContent != null) { //채팅방 내용 있는 경우
                if (lastContent.getType() == 1) { //사진
                    lastMsg.setText(PICTURE);
                } else {
                    lastMsg.setText(lastContent.getContent());
                }
                if (lastContent.getSendDate() != null) {
                    String convertedDate = DateManager.convertDate(lastContent.getSendDate(), datePattern);
                    time.setText(DateManager.getTimeInterval(convertedDate, datePattern));
                }

            } else { // 채팅방 내용 없는 경우 (주로 처음 새로 만들었을 때)
                lastMsg.setText(EMPTY);
                String convertedDate = DateManager.convertDate(
                        chatroom.getUpdatedDate(), datePattern);
                time.setText(DateManager.getTimeInterval(convertedDate, datePattern));
            }

            //채팅방 목록 상태 설정 (1:1 채팅인 경우 상대방 상태 설정)
            setStatusImg(chatroom);

            //채팅방 멤버수 설정
            memberCount.setText(String.valueOf(roomMemberCount));


            //채팅방 목록 알림 및 상단고정 아이콘 설정
            setNotify(chatroom);

            //채팅방에서 안 읽은 메세지 개수 표시
            setUnreadMessage(chatroom);

        }

        /**
         * 채팅방 목록 알림 및 상단고정 아이콘 설정 함수
         *
         * @param chatRoom
         */
        public void setNotify(ChatRoom chatRoom) {
            boolean fixTop;
            boolean alarm;

            fixTop = chatRoom.getSettingFixTop(); //default false
            alarm = chatRoom.getSettingAlarm(); //default true

            if (fixTop && alarm) { // 상단 고정만인 경우
                notifyImg1.setText(ICON_FIX_TOP);
                notifyImg2.setText(EMPTY);
            } else if (!fixTop && !alarm) { // 알림 해제만 한 경우
                notifyImg1.setText(ICON_ALARM_OFF);
                notifyImg2.setText(EMPTY);
            } else if (fixTop && !alarm) { // 상단 고정 + 알림 해제 한 경우
                notifyImg1.setText(ICON_FIX_TOP);
                notifyImg2.setText(ICON_ALARM_OFF);
            } else { //기본 상태일 때
                notifyImg1.setText(EMPTY);
                notifyImg2.setText(EMPTY);
            }

            notifyImg1.setTextColor(lastMsg.getTextColors().getDefaultColor());
            notifyImg2.setTextColor(lastMsg.getTextColors().getDefaultColor());

        }

        /**
         * 채팅방 타입이 1대1 채팅방인 경우에는 대화 상대방의 상태 표시
         * 채팅방 타입이 단체방인 경우에는 상태 표시 안함
         **/
        public void setStatusImg(ChatRoom chatRoom) {
            String rid = chatRoom.getRid();
            User myAccount = User.getMyAccountInfo(mRealm);
            RealmResults<ChatRoomMember> users = ChatRoom.getChatRoomUsers(mRealm, rid);
            if (users.size() == 2) { // 1 대 1 채팅방인 경우
                for (ChatRoomMember user : users) {
                    if (!user.getUid().equals(myAccount.getUid())) {
                        // profilestatus
                        User anotherUser = mRealm.where(User.class).equalTo("uid", user.getUid()).findFirst();
                        switch (anotherUser.getAppStatus()) {
                            case "1":
                                mAquery.view(statusImg).image(STATUS_BUSY);
                                break;
                            case "2":
                                mAquery.view(statusImg).image(STATUS_OFF);
                                break;
                            default: // 0과 기본으로 되어있는 설정
                                mAquery.view(statusImg).image(STATUS_ON);
                                break;
                        }
                    }
                }
            } else {
                statusImg.setVisibility(View.INVISIBLE);
            }

        }

        /**
         * 채팅방에서 안 읽은 메세지 개수 표시 함수
         *
         * @param chatRoom
         */
        public void setUnreadMessage(ChatRoom chatRoom) {
            int unreadMessages = 0;
            RealmResults<ChatContent> chatContents;
            chatContents = mRealm.where(ChatContent.class).equalTo("rid", chatRoom.getRid()).findAll();

            for (ChatContent chatContent : chatContents) {
                if (!chatContent.getIsRead()) {
                    unreadMessages += 1;
                }
            }

            if (unreadMessages == 0) {
                remain.setText(EMPTY);
                remain.setVisibility(View.INVISIBLE);
            } else {
                remain.setText(Integer.toString(unreadMessages));
                remain.setVisibility(View.VISIBLE);
            }

        }
    }


    /** Modified By Jongho Lee*/
    public void updateData(RealmResults<ChatRoom> dataSet){
        this.mDataset = dataSet;
        sortChatRoomByDate();
        notifyDataSetChanged();
    }

    /**
     * 채팅방 목록 시간 순서대로 정렬
     */
    public void sortChatRoomByDate() {
        this.mDataset = this.mDataset.sort("settingFixTop", Sort.DESCENDING,
                "updatedDate", Sort.DESCENDING);
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

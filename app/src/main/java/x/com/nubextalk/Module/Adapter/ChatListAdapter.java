/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Adapter;

import android.content.Context;
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
import x.com.nubextalk.Module.Case.ChatlistCase;
import x.com.nubextalk.R;

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private RealmResults<ChatRoom> mDataset;
    private final LayoutInflater mInflater;
    private Realm realm;
    private Context context;
    private OnItemLongSelectedListener longClickListener;
    private OnItemSelectedListener clickListener;
    private AQuery aq;
    private ChatlistCase sel_type;
    public interface OnItemSelectedListener {
        void onItemSelected(ChatRoom chatRoom);
        void onItemSelected(ChatRoom chatRoom, RadioButton radioButton);
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

    public ChatListAdapter(Context context, RealmResults<ChatRoom> mChatList, ChatlistCase sel_type) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mDataset = mChatList;
        this.aq = new AQuery(context);
        this.realm = Realm.getInstance(UtilityManager.getRealmConfig());
        this.sel_type = sel_type;
        sortChatRoomByDate();

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(sel_type == ChatlistCase.NON_RADIO) {
            View mItemView = mInflater.inflate(
                    R.layout.item_chatlist, parent, false);
            return new ViewItemHolderNonRadio(mItemView);
        } else if(sel_type == ChatlistCase.RADIO) {
            View mItemView = mInflater.inflate(R.layout.item_chatlist_radiobutton, parent, false);
            return new ViewItemHolderRadio(mItemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String roomId = mDataset.get(position).getRid();
        int roomMemberCount = ChatRoom.getChatRoomUsers(realm, roomId).size();
        String roomImgUrl = mDataset.get(position).getRoomImg();
        ChatContent lastContent = realm.where(ChatContent.class) // 이 방식은 나중에 채팅 메세지가 많아지면 별로 좋은 방법이 아니므로 ChatRoom 에 lastContentId 를 넣는건 어떨
                .equalTo("rid", roomId)
                .sort("sendDate", Sort.DESCENDING).findFirst();

        ChatRoom mCurrent = mDataset.get(position);
        if (holder instanceof ViewItemHolderNonRadio) {
            //아이템 데이터 초기화
            String datePattern = "yyyy-MM-dd'T'HH:mm:ss";
            ViewItemHolderNonRadio mHolder = (ViewItemHolderNonRadio) holder;

            //채팅방 목록 사진 설정
            if (URLUtil.isValidUrl(roomImgUrl)) {
                aq.view(mHolder.chatRoomImg).image(roomImgUrl);
            } else {
                if (roomMemberCount > 2) {
                    aq.view(mHolder.chatRoomImg).image(R.drawable.ic_twotone_group_24);
                } else {
                    aq.view(mHolder.chatRoomImg).image(R.drawable.baseline_account_circle_black_24dp);
                }
            }

            //채팅방 목록 이름 설정
            mHolder.chatRoomName.setText(mCurrent.getRoomName());

            //채팅방 목록에 보이는 채팅메세지 설정
            if (lastContent != null) { //채팅방 내용 있는 경우
                if (lastContent.getType() == 1) { //사진
                    mHolder.lastMsg.setText("새 사진");
                } else {
                    mHolder.lastMsg.setText(lastContent.getContent());
                }
                if (lastContent.getSendDate() != null) {
                    String convertedDate = DateManager.convertDate(lastContent.getSendDate(), datePattern);
                    mHolder.time.setText(DateManager.getTimeInterval(convertedDate, datePattern));
                }

            } else { // 채팅방 내용 없는 경우 (주로 처음 새로 만들었을 때)
                mHolder.lastMsg.setText("");
                String convertedDate = DateManager.convertDate(
                        mCurrent.getUpdatedDate(), datePattern);
                mHolder.time.setText(DateManager.getTimeInterval(convertedDate, datePattern));
            }

            //채팅방 목록 상태 설정 (1:1 채팅인 경우 상대방 상태 설정)
            mHolder.setStatusImg(mCurrent);

            //채팅방 목록 알림 및 상단고정 아이콘 설정
            mHolder.setNotify(mCurrent);

            //채팅방 목록 아이템 꾹 누르면 발생 이벤트
            mHolder.itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onItemLongSelected(mCurrent);
                }
                return false;
            });

            //채팅방 목록 아이템 누르면 발생 이벤트
            mHolder.itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemSelected(mCurrent);
                }
            });

            //채팅방에서 안 읽은 메세지 개수 표시
            mHolder.setUnreadMessage(mCurrent);
        } else if (holder instanceof ViewItemHolderRadio) {
            ViewItemHolderRadio mHolder = (ViewItemHolderRadio) holder;

            //채팅방 목록 사진 설정
            if (URLUtil.isValidUrl(roomImgUrl)) {
                aq.view(mHolder.chatRoomImg).image(roomImgUrl);
            } else {
                if (roomMemberCount > 2) {
                    aq.view(mHolder.chatRoomImg).image(R.drawable.ic_twotone_group_24);
                } else {
                    aq.view(mHolder.chatRoomImg).image(R.drawable.baseline_account_circle_black_24dp);
                }
            }

            //채팅방 목록 이름 설정
            mHolder.chatRoomName.setText(mCurrent.getRoomName());

            //채팅방 목록에 보이는 채팅메세지 설정
            if (lastContent != null) { //채팅방 내용 있는 경우
                if (lastContent.getType() == 1) //사진
                    mHolder.lastMsg.setText("새 사진");
                else
                    mHolder.lastMsg.setText(lastContent.getContent());
            } else { // 채팅방 내용 없는 경우 (주로 처음 새로 만들었을 때)
                mHolder.lastMsg.setText("");
            }

            //채팅방 목록 상태 설정 (1:1 채팅인 경우 상대방 상태 설정)
            mHolder.setStatusImg(mCurrent);

            //채팅방 목록 알림 및 상단고정 아이콘 설정
            mHolder.setNotify(mCurrent);

            //채팅방 목록 아이템 누르면 발생 이벤트
            mHolder.itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemSelected(mCurrent, mHolder.radioButton);
                }
            });
        }
    }

    public class ViewItemHolderNonRadio extends RecyclerView.ViewHolder {

        public TextView lastMsg;
        public TextView chatRoomName;
        public TextView time;
        public TextView remain;
        public CircleImageView chatRoomImg;
        public ImageView statusImg;
        public IconButton notifyImg1;
        public IconButton notifyImg2;
        public View chatLayout;

        public ViewItemHolderNonRadio(View itemView) {
            super(itemView);
            lastMsg = itemView.findViewById(R.id.chat_list_last_message);
            chatRoomName = itemView.findViewById(R.id.chat_list_friend_name);
            time = itemView.findViewById(R.id.chat_list_chat_time);
            remain = itemView.findViewById(R.id.chat_list_chat_remain);
            chatRoomImg = itemView.findViewById(R.id.chat_list_chat_picture);
            statusImg = itemView.findViewById(R.id.chat_list_friend_status);
            notifyImg1 = itemView.findViewById(R.id.chat_list_notify1);
            notifyImg2 = itemView.findViewById(R.id.chat_list_notify2);
            chatLayout = itemView.findViewById(R.id.chat_list_layout);
        }

        /**
         * 채팅방 목록 알림 및 상단고정 아이콘 설정 함수
         *
         * @param chatRoom
         */
        public void setNotify(ChatRoom chatRoom) {
            boolean fixTop;
            boolean alarm;
            String fixTopIcon = "{fas-map-pin 16dp}";
            String alarmOff = "{far-bell-slash 16dp}";

            fixTop = chatRoom.getSettingFixTop(); //default false
            alarm = chatRoom.getSettingAlarm(); //default true

            if (fixTop && alarm) { // 상단 고정만인 경우
                notifyImg1.setText(fixTopIcon);
                notifyImg2.setText("");
            } else if (!fixTop && !alarm) { // 알림 해제만 한 경우
                notifyImg1.setText(alarmOff);
                notifyImg2.setText("");
            } else if (fixTop && !alarm) { // 상단 고정 + 알림 해제 한 경우
                notifyImg1.setText(fixTopIcon);
                notifyImg2.setText(alarmOff);
            } else { //기본 상태일 때
                notifyImg1.setText("");
                notifyImg2.setText("");
            }
        }

        /**
         * 채팅방 타입이 1대1 채팅방인 경우에는 대화 상대방의 상태가 보여야하는데 내가 누구인지 알아야 상대방 상태 표시 가
         * 채팅방 타입이 단체방인 경우에는 어떻게 하지?
         **/
        public void setStatusImg(ChatRoom chatRoom) {
            String rid = chatRoom.getRid();
            User myAccount = (User) User.getMyAccountInfo(realm);
            RealmResults<ChatRoomMember> users = ChatRoom.getChatRoomUsers(realm, rid);
            if (users.size() == 2) { // 1 대 1 채팅방인 경우
                for (ChatRoomMember user : users) {
                    if (!user.getUid().equals(myAccount.getUserId())) {
                        // profilestatus
                        User anotherUser = realm.where(User.class).equalTo("userId", user.getUid()).findFirst();
                        switch (anotherUser.getAppStatus()) {
                            case "1":
                                aq.view(statusImg).image(R.drawable.baseline_fiber_manual_record_yellow_50_24dp);
                                break;
                            case "2":
                                aq.view(statusImg).image(R.drawable.baseline_fiber_manual_record_red_800_24dp);
                                break;
                            default: // 0과 기본으로 되어있는 설정
                                aq.view(statusImg).image(R.drawable.baseline_fiber_manual_record_teal_a400_24dp);
                                break;
                        }
                    }
                }
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
            chatContents = realm.where(ChatContent.class).equalTo("rid", chatRoom.getRid()).findAll();

            for (ChatContent chatContent : chatContents) {
                if (!chatContent.getIsRead()) {
                    unreadMessages += 1;
                }
            }

            if (unreadMessages == 0) {
                remain.setText("");
                remain.setVisibility(View.INVISIBLE);
            } else {
                remain.setText(Integer.toString(unreadMessages));
                remain.setVisibility(View.VISIBLE);
            }

        }
    }

    public class ViewItemHolderRadio extends RecyclerView.ViewHolder {

        public TextView lastMsg;
        public TextView chatRoomName;

        public CircleImageView chatRoomImg;
        public ImageView statusImg;
        public IconButton notifyImg1;
        public IconButton notifyImg2;
        public View chatLayout;

        public RadioButton radioButton;
        public ViewItemHolderRadio(View itemView) {
            super(itemView);
            lastMsg = itemView.findViewById(R.id.chat_list_last_message);
            chatRoomName = itemView.findViewById(R.id.chat_list_friend_name);

            chatRoomImg = itemView.findViewById(R.id.chat_list_chat_picture);
            statusImg = itemView.findViewById(R.id.chat_list_friend_status);
            notifyImg1 = itemView.findViewById(R.id.chat_list_notify1);
            notifyImg2 = itemView.findViewById(R.id.chat_list_notify2);
            chatLayout = itemView.findViewById(R.id.chat_list_layout);

            radioButton = itemView.findViewById(R.id.select_chat);
        }


        /**
         * 채팅방 목록 알림 및 상단고정 아이콘 설정 함수
         *
         * @param chatRoom
         */
        public void setNotify(ChatRoom chatRoom) {
            boolean fixTop;
            boolean alarm;
            String fixTopIcon = "{fas-map-pin 16dp}";
            String alarmOff = "{far-bell-slash 16dp}";

            fixTop = chatRoom.getSettingFixTop(); //default false
            alarm = chatRoom.getSettingAlarm(); //default true

            if (fixTop && alarm) { // 상단 고정만인 경우
                notifyImg1.setText(fixTopIcon);
                notifyImg2.setText("");
            } else if (!fixTop && !alarm) { // 알림 해제만 한 경우
                notifyImg1.setText(alarmOff);
                notifyImg2.setText("");
            } else if (fixTop && !alarm) { // 상단 고정 + 알림 해제 한 경우
                notifyImg1.setText(fixTopIcon);
                notifyImg2.setText(alarmOff);
            } else { //기본 상태일 때
                notifyImg1.setText("");
                notifyImg2.setText("");
            }
        }

        /**
         * 채팅방 타입이 1대1 채팅방인 경우에는 대화 상대방의 상태가 보여야하는데 내가 누구인지 알아야 상대방 상태 표시 가
         * 채팅방 타입이 단체방인 경우에는 어떻게 하지?
         **/
        public void setStatusImg(ChatRoom chatRoom) {
            String rid = chatRoom.getRid();
            User myAccount = (User) User.getMyAccountInfo(realm);
            RealmResults<ChatRoomMember> users = ChatRoom.getChatRoomUsers(realm, rid);
            if (users.size() == 2) { // 1 대 1 채팅방인 경우
                for (ChatRoomMember user : users) {
                    if (!user.getUid().equals(myAccount.getUserId())) {
                        // profilestatus
                        User anotherUser = realm.where(User.class).equalTo("userId", user.getUid()).findFirst();
                        switch (anotherUser.getAppStatus()) {
                            case "1":
                                aq.view(statusImg).image(R.drawable.baseline_fiber_manual_record_yellow_50_24dp);
                                break;
                            case "2":
                                aq.view(statusImg).image(R.drawable.baseline_fiber_manual_record_red_800_24dp);
                                break;
                            default: // 0과 기본으로 되어있는 설정
                                aq.view(statusImg).image(R.drawable.baseline_fiber_manual_record_teal_a400_24dp);
                                break;
                        }
                    }
                }
            }

        }

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

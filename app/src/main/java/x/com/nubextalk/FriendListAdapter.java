package x.com.nubextalk;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

import okhttp3.internal.http2.Header;
import x.com.nubextalk.item.Profile;

public class FriendListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public interface OnItemClickListenerInterface {
        void onItemClick(View v, int pos);
    }
    Context context;
    private final LinkedList<Profile> friendList;

    private OnItemClickListenerInterface mListener;

    public FriendListAdapter(Context context,
                             LinkedList<Profile> friendList,
                             OnItemClickListenerInterface listener) {
        this.context = context;
        this.friendList = friendList;
        this.mListener = listener;
    }




    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView;
        if(viewType == 0) {
            // 헤더라면
            mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
            Log.e("Adpater", "header onCreate");
            return new HeaderHolder(mItemView, this);
        } else {
            // 그 외 친구목록이라면
            mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend,parent,false);
            Log.e("Adapter", "friendlist onCreate");
            return new FriendViewHolder(mItemView, this);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // LinkedList에서 하나씩.
        Profile mCurrent = friendList.get(position);
        if(mCurrent.getProfileImage()==0) {
            Log.e("Adpater", "header");
            HeaderHolder headerHolder = (HeaderHolder) holder;
            headerHolder.header.setText(mCurrent.getName());
        } else {
            // Header가 아니고 친구라면
            // holder가 RecyclerViewHolder이므로 FriendViewHolder 클래스로 변환해준다.
            Log.e("Adapter", "friendlist");
            Log.e("Adapter", mCurrent.getName());
            FriendViewHolder friendViewHolder = (FriendViewHolder) holder;
            friendViewHolder.profileName.setText(mCurrent.getName());
            friendViewHolder.profileImage.setImageResource(mCurrent.getProfileImage());
            friendViewHolder.profileStatus.setImageResource(mCurrent.getStatus());
        }
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    // 해당 Adapter에서 사용하고 싶은 item_view가 2개이므로 ViewType을 지정해주어야 한다.
    @Override
    public int getItemViewType(int position) {
        Profile profile = friendList.get(position);
        // Header는 이미지를 가지고 있지 않으므로
        if (profile.getProfileImage() == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        public final TextView profileName;
        public final ImageView profileImage;
        public final ImageView profileStatus;
        final FriendListAdapter mAdapter;
        public FriendViewHolder(@NonNull View itemView, FriendListAdapter mAdapter) {
            super(itemView);
            // item_friend.xml에서 불러온다.
            profileName = itemView.findViewById(R.id.profileName);

            profileImage = itemView.findViewById(R.id.profileImage);
            profileStatus = itemView.findViewById(R.id.profileStatus);

            // 이미지 동그랗게 만드는 방법.
            profileImage.setBackground(new ShapeDrawable(new OvalShape()));
            profileImage.setClipToOutline(true);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(v, pos);
                    }
                }
            });
            this.mAdapter = mAdapter;
        }
    }
    public class HeaderHolder extends RecyclerView.ViewHolder {
        public final TextView header;
        final FriendListAdapter mAdapter;
        public HeaderHolder(@NonNull View itemView, FriendListAdapter mAdapter) {
           super(itemView);
            header = itemView.findViewById(R.id.separateName);
            this.mAdapter = mAdapter;
        }
    }
}

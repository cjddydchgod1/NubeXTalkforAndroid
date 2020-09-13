package x.com.nubextalk;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

import x.com.nubextalk.item.Profile;

public class FriendListAdapter extends RecyclerView.Adapter {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final LinkedList<Profile> friendList;
    private LayoutInflater mInflater;

    private static final int FRIEND_HEADER = 0;
    public FriendListAdapter(Context context, LinkedList<Profile> friendList) {
        mInflater = LayoutInflater.from(context);
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.item_friend,parent,false);
        return new FriendViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // LinkedList에서 하나씩.
        // Header가 아니고 친구라면
        // holder가 RecyclerViewHolder이므로 FriendViewHolder 클래스로 변환해준다.
        FriendViewHolder friendViewHolder = (FriendViewHolder) holder;
        Log.d(TAG, "밑이다~~~~~~~~~~~~~~~~~~~~~~~~~~ ");
        Profile mCurrent = friendList.get(position);
        friendViewHolder.profileName.setText(mCurrent.getName());
        friendViewHolder.profileImage.setImageResource(mCurrent.getProfileImage());
        friendViewHolder.profileStatus.setImageResource(mCurrent.getStatus());
    }

    @Override
    public int getItemCount() {
        return friendList.size();
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

            this.mAdapter = mAdapter;
        }
    }
}

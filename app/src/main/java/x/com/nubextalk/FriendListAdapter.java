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

import com.bumptech.glide.Glide;

import java.util.LinkedList;

import io.realm.RealmResults;
import okhttp3.internal.http2.Header;
import x.com.nubextalk.Model.User;

public class FriendListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private RealmResults<User> mDataSet;
    private Context mContext;

    private onItemSelectedListener listener;
    public interface onItemSelectedListener{
        void onSelected(User address);
    }
    public void setOnItemSelectedListener(onItemSelectedListener listener){
        this.listener = listener;
    }

    public FriendListAdapter(Context context, RealmResults<User> data) {
        this.mDataSet = data;
        this.mContext = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView;
        if(viewType == 0) {
            // 헤더라면
            mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
            Log.e("Adpater", "header onCreate");
            return new HeaderHolder(mItemView);
        } else {
            // 그 외 친구목록이라면
            mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend,parent,false);
            Log.e("Adapter", "friendlist onCreate");
            return new FriendViewHolder(mItemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // LinkedList에서 하나씩.
        User mCurrent = mDataSet.get(position);
        if(mCurrent.getProfileImg() == "0") { // 리스트는 어떻게 나눌건지 생각해봐야대
            Log.e("Adpater", "header");
            HeaderHolder headerHolder = (HeaderHolder) holder;
            headerHolder.header.setText(mCurrent.getName());
        } else {
            // Header가 아니고 친구라면
            Log.e("Adapter", "friendlist");
            Log.e("Adapter", mCurrent.getName());
            FriendViewHolder friendViewHolder = (FriendViewHolder) holder;

            friendViewHolder.bintTo(mCurrent);

            friendViewHolder.itemView.setOnClickListener(v -> {
               if(listener != null){
                   listener.onSelected(mDataSet.get(position));
               }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    // 해당 Adapter에서 사용하고 싶은 item_view가 2개이므로 ViewType을 지정해주어야 한다.
    @Override
    public int getItemViewType(int position) {
        User profile = mDataSet.get(position);
        // Header는 이미지를 가지고 있지 않으므로
        if (profile.getProfileImg() == "0") {
            return 0;
        } else {
            return 1;
        }
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        public final TextView profileName;
        public final ImageView profileImage;
        public final ImageView profileStatus;
        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            // item_friend.xml에서 불러온다.
            profileName = itemView.findViewById(R.id.profileName);
            profileImage = itemView.findViewById(R.id.profileImage);
            profileStatus = itemView.findViewById(R.id.profileStatus);

            // 이미지 동그랗게 만드는 방법.
            profileImage.setBackground(new ShapeDrawable(new OvalShape()));
            profileImage.setClipToOutline(true);

        }
        public void bintTo(User user) {
            profileName.setText(user.getName());
            Glide.with(mContext).load(user.getProfileImg()).into(profileImage);
            if(user.getStatus() == 0) { // 초록
                profileStatus.setImageResource(R.drawable.baseline_fiber_manual_record_teal_a400_24dp);
            } else if(user.getStatus() == 1) { // 빨강
                profileStatus.setImageResource(R.drawable.baseline_fiber_manual_record_red_800_24dp);
            } else { // 노랑
                profileStatus.setImageResource(R.drawable.baseline_fiber_manual_record_yellow_50_24dp);
            }
        }
    }
    public class HeaderHolder extends RecyclerView.ViewHolder {
        public final TextView header;
        public HeaderHolder(@NonNull View itemView) {
           super(itemView);
            header = itemView.findViewById(R.id.separateName);
        }
    }
}

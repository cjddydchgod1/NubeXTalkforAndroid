/*
 * Created By Jong Ho, Lee on  2020.
 * Copyright 테크하임(주). All rights reserved.
 */

package x.com.nubextalk.Module.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import x.com.nubextalk.Manager.UtilityManager;
import x.com.nubextalk.Model.Example_Model_Address;
import x.com.nubextalk.R;


public class Example_AddressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int TYPE_ITEM = 0;

    private RealmResults<Example_Model_Address> mDataSet;

    private onItemSelectedListener listener;
    public interface onItemSelectedListener{
        void onSelected(Example_Model_Address address);
    }
    public void setOnItemSelectedListener(onItemSelectedListener listener){
        this.listener = listener;
    }

    public Example_AddressAdapter(RealmResults<Example_Model_Address> data) {
        this.mDataSet = data;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_address_item, parent, false);
        return new ViewItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewItemHolder){
            ViewItemHolder mHolder = (ViewItemHolder) holder;
            String group = getTypeString(mDataSet.get(position));
            mHolder.txtType.setText(group);
            mHolder.txtRating.setText("{fas-star} "+mDataSet.get(position).getRating());
            mHolder.txtName.setText(mDataSet.get(position).getName());
            if(UtilityManager.checkString(mDataSet.get(position).getDesc())){
                mHolder.txtAddress.setText(mDataSet.get(position).getDesc());
            }
            else{
                mHolder.txtAddress.setText(mDataSet.get(position).getAddress());
            }
            mHolder.itemView.setOnClickListener(v -> {
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

    public void update(RealmResults<Example_Model_Address> data) {
        this.mDataSet = data;
        notifyDataSetChanged();
    }


    private class ViewItemHolder extends RecyclerView.ViewHolder {
        IconTextView txtType, txtRating;
        TextView txtName, txtAddress;
        public ViewItemHolder(View itemView) {
            super(itemView);
            txtType = itemView.findViewById(R.id.txtType);
            txtRating = itemView.findViewById(R.id.txtRating);
            txtName = itemView.findViewById(R.id.txtName);
            txtAddress = itemView.findViewById(R.id.txtAddress);
        }
    }

    private String getTypeString(Example_Model_Address address){
        return address.getTypeFont().replaceAll("\\}"," 28sp}") + "\n" + address.getTypeText();
    }
}

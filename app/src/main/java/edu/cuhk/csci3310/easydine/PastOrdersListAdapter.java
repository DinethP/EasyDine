package edu.cuhk.csci3310.easydine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class PastOrdersListAdapter extends RecyclerView.Adapter<PastOrdersListAdapter.PastOrdersViewHolder> {
    private Context context;
    private LayoutInflater mInflater;

    private final LinkedList<String> mRestaurantNameList;
    private final LinkedList<String> mDateList;
    private final LinkedList<Integer> mFriendsList;

    // Implement OnClickListener to allow for master-detail navigation
    class PastOrdersViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView restaurantImageView;
        TextView nameTextView, dateTextView, friendsTextView;

        final PastOrdersListAdapter mAdapter;

        public PastOrdersViewHolder(View itemView, PastOrdersListAdapter adapter) {
            super(itemView);

            restaurantImageView = itemView.findViewById(R.id.pastOrdersImageView);
            nameTextView = itemView.findViewById(R.id.pastOrdersRestaurantName);
            dateTextView = itemView.findViewById(R.id.pastOrdersDate);
            friendsTextView = itemView.findViewById(R.id.pastOrdersFriends);
            this.mAdapter = adapter;

            // Event handling registration, page navigation goes here
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Use Explicit intent with needed extras to start UpdateFlowerDetails Activity
            Log.d("PastOrdersListAdapter", "We here");
        }
    }


    public PastOrdersListAdapter(Context context, LinkedList<String> restaurantNameList,
                                 LinkedList<String> dateList, LinkedList<Integer> friendsList) {

        mInflater = LayoutInflater.from(context);
        this.mRestaurantNameList = restaurantNameList;
        this.mDateList = dateList;
        this.mFriendsList = friendsList;
    }

    // Called when the UpdatePastOrders Activity returns
    public void updateAdapter(String name, String genus, int richness, int pos) {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PastOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.pastorderslist_item, parent, false);
        return new PastOrdersViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull PastOrdersViewHolder holder, int position) {
        // Update the following to display correct information based on the given position
        String mRestaurantName = mRestaurantNameList.get(position);
        String mDate = mDateList.get(position);
        int mFriends = mFriendsList.get(position);

        // Set up View items for this row (position)
        holder.nameTextView.setText(mRestaurantName);
        holder.dateTextView.setText(mDate);
        holder.friendsTextView.setText(Integer.toString(mFriends));
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mRestaurantNameList.size();
    }

}

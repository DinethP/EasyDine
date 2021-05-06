package edu.cuhk.csci3310.easydine;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class UserListAdapter extends RecyclerView.Adapter {
    private Context context;
    private LayoutInflater mInflater;
    private LinkedList<String> userNames;
    private static final String TAG = "UserListAdapter";

    class UserListViewHolder extends RecyclerView.ViewHolder {
        TextView userName;

        public UserListViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "In UserListViewHolder constructor");
            userName = (TextView) itemView.findViewById(R.id.text_name);
        }
    }

    public UserListAdapter(Context context, LinkedList<String> userNames){
        Log.d(TAG, "In UserListAdapterconstructor");
        this.mInflater = LayoutInflater.from(context);
        this.userNames = userNames;
        this.context = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "In UserListViewHolder OnCreateViewHolder");
//        View mItemView = mInflater.inflate(R.layout.userlist_item, parent, false);
        View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.userlist_item, parent, false);
        return new UserListViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserListViewHolder userListViewHolder = (UserListViewHolder) holder;
        Log.d(TAG, "Setting username in view holder: "+ userNames.get(position));
        userListViewHolder.userName.setText(userNames.get(position));
    }

    @Override
    public int getItemCount() {
        return userNames.size();
    }
}

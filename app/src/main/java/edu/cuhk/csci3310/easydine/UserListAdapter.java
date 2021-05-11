package edu.cuhk.csci3310.easydine;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class UserListAdapter extends RecyclerView.Adapter {
    private Context context;
    private LayoutInflater mInflater;
    private LinkedList<User> userNames;
    private LinkedList<User> selectedParticpants = new LinkedList<User>();
    private static final String TAG = "UserListAdapter";

    class UserListViewHolder extends RecyclerView.ViewHolder {
        CheckedTextView userName;
        ConstraintLayout userView;

        public UserListViewHolder(@NonNull View itemView) {
            super(itemView);
            Boolean isSelected = false;
            Log.d(TAG, "In UserListViewHolder constructor");
            userName = (CheckedTextView) itemView.findViewById(R.id.checkedTextView);
            userView = (ConstraintLayout) itemView.findViewById(R.id.user_layout);
            userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(userName.isChecked()){
                        selectedParticpants.remove(userNames.get(getLayoutPosition()));
                        userName.setChecked(false);
                    }
                    else{
                        selectedParticpants.addLast(userNames.get(getLayoutPosition()));
                        userName.setChecked(true);
                    }
                }
            });
        }
    }

    public UserListAdapter(Context context, LinkedList<User> userNames){
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
        userListViewHolder.userName.setText(userNames.get(position).getUserName());
    }

    @Override
    public int getItemCount() {
        return userNames.size();
    }

    // send selectedParticipants linked list to AddParticipants Fragment
    public LinkedList<User> getSelectedParticpants(){
        return selectedParticpants;
    }
}

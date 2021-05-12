package edu.cuhk.csci3310.easydine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.perfmark.Link;

public class PastOrdersListAdapter extends RecyclerView.Adapter<PastOrdersListAdapter.PastOrdersViewHolder> {
    private Context context;
    private LayoutInflater mInflater;
    private String TAG = "PastOrdersAdapter";
    private final LinkedList<String> mOrderIdList;
    private final LinkedList<String> mRestaurantImageList;
    private final LinkedList<String> mRestaurantNameList;
    private final LinkedList<String> mDateList;
    private final LinkedList<Integer> mFriendsList;
    private final LinkedList<Boolean> mPayedList;

    // private double amount;
    // private ArrayList<String> friendsList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Implement OnClickListener to allow for master-detail navigation
    class PastOrdersViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        LinearLayout cardItem;
        ImageView restaurantImageView;
        TextView nameTextView, dateTextView, friendsTextView;
        Button isPayedButton;

        final PastOrdersListAdapter mAdapter;

        public PastOrdersViewHolder(View itemView, PastOrdersListAdapter adapter) {
            super(itemView);
            restaurantImageView = itemView.findViewById(R.id.pastOrdersImageView);
            nameTextView = itemView.findViewById(R.id.pastOrdersRestaurantName);
            dateTextView = itemView.findViewById(R.id.pastOrdersDate);
            friendsTextView = itemView.findViewById(R.id.pastOrdersFriends);
            isPayedButton = itemView.findViewById(R.id.isPayedBtn);
            cardItem = itemView.findViewById(R.id.cardItem);
            this.mAdapter = adapter;

            // Event handling registration, page navigation goes here
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // TODO: Maybe add a fragment to include additional order details
            Log.d("PastOrdersListAdapter", "We here");
        }
    }

    public PastOrdersListAdapter(Context context, LinkedList<String> orderIdList, LinkedList<String> restaurantImageList, LinkedList<String> restaurantNameList,
                                 LinkedList<String> dateList, LinkedList<Integer> friendsList, LinkedList<Boolean> payedList) {

        mInflater = LayoutInflater.from(context);
        this.mOrderIdList = orderIdList;
        this.mRestaurantImageList = restaurantImageList;
        this.mRestaurantNameList = restaurantNameList;
        this.mDateList = dateList;
        this.mFriendsList = friendsList;
        this.mPayedList = payedList;
        this.context = context;

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
        String orderId = mOrderIdList.get(position);
        String mImageURL = mRestaurantImageList.get(position);
        String mRestaurantName = mRestaurantNameList.get(position);
        String mDate = mDateList.get(position);
        int mFriends = mFriendsList.get(position);
        Boolean mIsPayed = mPayedList.get(position);
        double[] amount = new double[]{0};
        ArrayList<String> friendsList = new ArrayList<>();

        db.collection("orders").document(orderId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    amount[0] = document.getDouble("amount");

                    // get the names of the friends
                    List<Map> friends = (List<Map>) document.get("friends");
                    if (friends != null){
                        friendsList.clear();
                        for (Map map: friends){
                            String s = map.values().toString();
                            s = s.replaceAll("[\\[\\]]","");
                            friendsList.add(s);
                        }
                        Log.d("MAP_CHECK", String.valueOf(friendsList));
                    }

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        // Set up View items for this row (position)
        new DownloadImageTask(holder.restaurantImageView).execute(mImageURL);
        holder.nameTextView.setText(mRestaurantName);
        holder.dateTextView.setText(mDate);
//        holder.friendsTextView.setText(R.string.friends_label + Integer.toString(mFriends));
        holder.friendsTextView.setText(String.format("# of Friends: %s", Integer.toString(mFriends)));
        if (mIsPayed) {
            holder.isPayedButton.setText("Already Paid");
            holder.isPayedButton.setEnabled(false);
        }
        else {
            holder.isPayedButton.setEnabled(true);
        }

        holder.isPayedButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("World", "Order is payed");
                db.collection("orders").document(orderId).update("isPayed", true);
                mPayedList.set(position, true);
                notifyDataSetChanged();
            }
        });
        // when a card is clicked
        holder.cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PastOrderDetail.class);
                // get record with orderId

                intent.putExtra("IMAGE", mImageURL);
                intent.putExtra("NAME", mRestaurantName);
                intent.putExtra("DATE", mDate);
                intent.putExtra("AMOUNT", amount[0]);
                intent.putExtra("COUNT_PEOPLE", mFriends);
                intent.putStringArrayListExtra("FRIENDS", friendsList);

                holder.cardItem.getContext().startActivity(intent);
                ((Activity)holder.cardItem.getContext()).finish();
            }
        });
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mRestaurantNameList.size();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}

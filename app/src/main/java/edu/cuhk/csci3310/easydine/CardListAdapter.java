package edu.cuhk.csci3310.easydine;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.CardViewHolder> {

    private LayoutInflater mInflater;
    private final LinkedList<String> mCardNameList;
    // intent for card item clicks
    Intent intent;
    String accountName;

    class CardViewHolder extends RecyclerView.ViewHolder {

        // ImageView imageView1, imageView2;
        TextView textView;
        ImageView imageView;
        final CardListAdapter mAdapter;

        public CardViewHolder(@NonNull View itemView, CardListAdapter mAdapter) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
            this.mAdapter = mAdapter;
        }
    }

    public CardListAdapter(Context context, LinkedList<String> cardNameList, String accountDisplayName) {
        mInflater = LayoutInflater.from(context);
        this.mCardNameList = cardNameList;
        this.accountName = accountDisplayName;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.cardlist_item, parent, false);
        return new CardViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        String cardName = mCardNameList.get(position);
        holder.textView.setText(cardName);
        switch (position) {
            case 0:
                holder.imageView.setImageResource(R.drawable.new_order);
                break;
            case 1:
                holder.imageView.setImageResource(R.drawable.past_order);
                break;
            case 2:
                holder.imageView.setImageResource(R.drawable.ic_outline_receipt_long_24);
                break;
            case 3:
                holder.imageView.setImageResource(R.drawable.analytics);
                break;
            case 4:
                holder.imageView.setImageResource(R.drawable.ic_outline_info_24);
                break;
            case 5:
                holder.imageView.setImageResource(R.drawable.ic_baseline_access_time_24);
                break;
        }

        // make the cards clickable
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (position) {
                    // 1st card: new order
                    case 0:
                        intent = new Intent(view.getContext(), PlacesActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        view.getContext().startActivity(intent);
                        break;
                    // 2nd card: past orders
                    case 1:
                        intent = new Intent(view.getContext(), PastOrdersActivity.class);
                        intent.putExtra("accountName", accountName);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        view.getContext().startActivity(intent);
                        break;
                    // 3rd card: split bill
                    case 2:
                        intent = new Intent(view.getContext(), PayActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        view.getContext().startActivity(intent);
                        break;
                    // 4th card: analytics
                    case 3:
                        intent = new Intent(view.getContext(), AnalyticsActivity.class);
                        intent.putExtra("accountName", accountName);
                        intent.putExtra("graph", 1);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        view.getContext().startActivity(intent);
                        break;
                }

            }
        });
    }


    public long getItemId(int position) {
        return position;
    }

    public int getItemCount() {
        return mCardNameList.size();
    }
}

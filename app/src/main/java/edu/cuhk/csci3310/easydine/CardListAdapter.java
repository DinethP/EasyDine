package edu.cuhk.csci3310.easydine;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.CardViewHolder> {

    private LayoutInflater mInflater;
    private LinkedList<String> mCardNameList;

    class CardViewHolder extends RecyclerView.ViewHolder{

        // ImageView imageView1, imageView2;
        TextView textView;

        final CardListAdapter mAdapter;

        public CardViewHolder(@NonNull View itemView, CardListAdapter mAdapter) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);

            this.mAdapter = mAdapter;
        }
    }

    public CardListAdapter(Context context, LinkedList<String> cardNameList){
        mInflater = LayoutInflater.from(context);
        this.mCardNameList = cardNameList;
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

        // make the cards clickable
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (position){

                    // 3rd card: split bill function
                    case 2:
                        Intent intent = new Intent(view.getContext(), PayActivity.class);
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

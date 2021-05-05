package edu.cuhk.csci3310.easydine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class FoodListAdapter extends RecyclerView.Adapter {
    private Context context;
    private LayoutInflater mInflater;
    private LinkedList<String> foodNames;
    private LinkedList<Double> foodPrices;
    private static final String TAG = "DishesListAdapter";

    class FoodListViewHolder extends RecyclerView.ViewHolder {
        TextView foodName;
        TextView foodPrice;

        public FoodListViewHolder(@NonNull View itemView, FoodListAdapter foodListAdapter) {
            super(itemView);
            foodName = itemView.findViewById(R.id.food_name);
            foodPrice = itemView.findViewById(R.id.food_price);
        }
    }

    public FoodListAdapter(Context context, LinkedList<String> foodNames, LinkedList<Double> foodPrices){
        this.mInflater = LayoutInflater.from(context);
        this.foodNames = foodNames;
        this.foodPrices = foodPrices;
        this.context = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.foodlist_item, parent, false);
        return new FoodListViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FoodListViewHolder foodListViewHolder = (FoodListViewHolder) holder;
        foodListViewHolder.foodName.setText(foodNames.get(position));
        foodListViewHolder.foodPrice.setText(String.valueOf(foodPrices.get(position)));
    }

    @Override
    public int getItemCount() {
        return foodNames.size();
    }
}

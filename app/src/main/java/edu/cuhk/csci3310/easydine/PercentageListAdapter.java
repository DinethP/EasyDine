package edu.cuhk.csci3310.easydine;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PercentageListAdapter extends RecyclerView.Adapter<PercentageListAdapter.PercentageViewHolder>{

    private LayoutInflater mInflater;

    private LinkedList<String> nameList = new LinkedList<>();
    private LinkedList<Double> percentageList = new LinkedList<>();

    private double total;

    class PercentageViewHolder extends RecyclerView.ViewHolder{

//        EditText name, percentage;
//        Button addButton;
        EditText percentage;
        TextView amount;

        PercentageListAdapter percentageListAdapter;

        public PercentageViewHolder(@NonNull View itemView, PercentageListAdapter percentageListAdapter) {
            super(itemView);
            this.percentageListAdapter = percentageListAdapter;
            percentage = itemView.findViewById(R.id.percentage);
            amount = itemView.findViewById(R.id.amount);
//            name = itemView.findViewById(R.id.name);
//            percentage = itemView.findViewById(R.id.percentage);
//            addButton = itemView.findViewById(R.id.add_button);

        }
    }

//    public PercentageListAdapter(Context context, LinkedList<String> nameList, LinkedList<Double> percentageList){
//        mInflater = LayoutInflater.from(context);
//        this.nameList = nameList;
//        this.percentageList = percentageList;
//    }

    public PercentageListAdapter(Context context, double total){
        mInflater = LayoutInflater.from(context);
        this.total = total;
    }

    @NonNull
    @Override
    public PercentageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.percentagelist_item, parent, false);
        return new PercentageViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull PercentageViewHolder holder, int position) {
        holder.percentage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    String s = charSequence.toString();
                    double value = Double.parseDouble(s) / 100 * total;
                    holder.amount.setText(String.format("%.1f", value));
                }catch (Exception e){
                    holder.amount.setText(String.format("%.1f", 0.0));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
//        holder.addButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });


    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return 6;
    }
}

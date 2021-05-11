package edu.cuhk.csci3310.easydine;

import android.content.Context;
import android.content.Intent;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

public class PercentageListAdapter extends RecyclerView.Adapter<PercentageListAdapter.PercentageViewHolder>{

    private LayoutInflater mInflater;

    private LinkedList<String> nameList = new LinkedList<>();
    private LinkedList<Double> percentageList = new LinkedList<>();

    private double total;
    private double previous;
    private int persons;
    private double userToPay;

    class PercentageViewHolder extends RecyclerView.ViewHolder{

        EditText percentage;
        TextView amount;

        PercentageListAdapter percentageListAdapter;

        public PercentageViewHolder(@NonNull View itemView, PercentageListAdapter percentageListAdapter) {
            super(itemView);
            this.percentageListAdapter = percentageListAdapter;
            percentage = itemView.findViewById(R.id.percentage);
            amount = itemView.findViewById(R.id.amount);

        }
    }


    public PercentageListAdapter(Context context, double total, int persons){
        mInflater = LayoutInflater.from(context);
        this.total = total;
        this.persons = persons;
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
            final Context context = holder.percentage.getContext();
            @Override
            // store the previous value
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();
                try{
                    previous = Double.parseDouble(s);
                }catch (Exception e){
                    previous = 0;
                }

            }
            // the amount each person needs to pay will be updated instantly according to the input percentage
            // print 0 if error is encountered
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            // update the amount field
            // and pass the previous and current values to fragment for calculating the total value
            @Override
            public void afterTextChanged(Editable editable) {
                Intent intent = new Intent("update_percentage");
                try {
                    String s = editable.toString();
                    Double value = Double.parseDouble(s) / 100 * total;
                    holder.amount.setText(String.format("%.1f", value));
                    // current user is the first person on the list. So get that value for notification
                    if(position == 0){
                        userToPay = value;
                    }
                    intent.putExtra("PERCENTAGE", Double.parseDouble(s));
                    intent.putExtra("PREVIOUS", previous);
                }catch (Exception e){
                    holder.amount.setText(String.format("%.1f", 0.0));
                    intent.putExtra("PERCENTAGE", 0.0);
                    intent.putExtra("PREVIOUS", previous);
                    userToPay = 0.0;
                }
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });


    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return persons == 0 ? 6 : persons+1;
    }

    public double getUserToPayValue (){
        return userToPay;
    }
}

package edu.cuhk.csci3310.easydine;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

public class ValueListAdapter extends RecyclerView.Adapter<ValueListAdapter.ValueViewHolder> {

    private LayoutInflater inflater;

    private double total;
    private double previous;
    private int persons;

    class ValueViewHolder extends RecyclerView.ViewHolder {
        EditText value;
        ValueListAdapter valueListAdapter;

        public ValueViewHolder(@NonNull View itemView, ValueListAdapter valueListAdapter) {
            super(itemView);
            this.valueListAdapter = valueListAdapter;
            value = itemView.findViewById(R.id.value);
        }

    }

    public ValueListAdapter(Context context, int persons) {
        inflater = LayoutInflater.from(context);
        this.persons = persons;
    }

    @NonNull
    @Override
    public ValueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.valuelist_item, parent, false);
        return new ValueViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ValueViewHolder holder, int position) {

        holder.value.addTextChangedListener(new TextWatcher() {
            final Context context = holder.value.getContext();

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();
                try{
                    previous = Double.parseDouble(s);
                }catch (Exception e){
                    previous = 0;
                }
            }
            // pass the amount to the fragment when the field is updated
            // return 0 if error is encountered
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                Intent intent = new Intent("update_value");

                try {
                    intent.putExtra("value", Double.parseDouble(s));
                    intent.putExtra("PREVIOUS", previous);
                } catch (Exception e) {
                    intent.putExtra("value", 0.0);
                    intent.putExtra("PREVIOUS", previous);
                }

                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return persons+1;
    }


}

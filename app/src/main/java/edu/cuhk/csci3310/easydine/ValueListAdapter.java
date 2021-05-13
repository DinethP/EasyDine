package edu.cuhk.csci3310.easydine;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

public class ValueListAdapter extends RecyclerView.Adapter<ValueListAdapter.ValueViewHolder> {

    private LayoutInflater inflater;

    private double total;
    private double previous;
    private ArrayList<User> persons;
    private double userToPay;

    class ValueViewHolder extends RecyclerView.ViewHolder {
        EditText value;
        TextView name;
        ValueListAdapter valueListAdapter;

        public ValueViewHolder(@NonNull View itemView, ValueListAdapter valueListAdapter) {
            super(itemView);
            this.valueListAdapter = valueListAdapter;
            value = itemView.findViewById(R.id.value);
            name = itemView.findViewById(R.id.name);
        }

    }

    public ValueListAdapter(Context context, ArrayList<User> persons) {
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

        if (persons != null) {
            if (position == 0) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userName = user.getDisplayName();
                holder.name.setText(userName);
            } else
                holder.name.setText(persons.get(position - 1).getUserName());
        }

        holder.value.addTextChangedListener(new TextWatcher() {
            final Context context = holder.value.getContext();

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();
                try {
                    previous = Double.parseDouble(s);
                } catch (Exception e) {
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
                Intent intent1 = new Intent("PASS_AMOUNT");
                double value;
                try {
                    intent.putExtra("value", Double.parseDouble(s));
                    // current user is the first person on the list. So get that value for notification
                    if (position == 0) {
                        userToPay = Double.parseDouble(s);
                    }
                    intent.putExtra("PREVIOUS", previous);
                } catch (Exception e) {
                    intent.putExtra("value", 0.0);
                    intent.putExtra("PREVIOUS", previous);
                    userToPay = 0.0;
                }

                try {
                    value = Double.parseDouble(holder.value.getText().toString());
                } catch (Exception e) {
                    value = 0;
                }

                intent1.putExtra("AMOUNT", value);
                intent1.putExtra("POSITION", position);

                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);
            }
        });

    }

    @Override
    public int getItemCount() {
        return persons == null ? 4 : persons.size() + 1;
    }

    public double getUserToPayValue() {
        return userToPay;
    }
}

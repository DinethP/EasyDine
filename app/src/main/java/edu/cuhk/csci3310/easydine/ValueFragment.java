package edu.cuhk.csci3310.easydine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class ValueFragment extends Fragment {

    private RecyclerView recyclerView;
    private ValueListAdapter valueListAdapter;

    private TextView remaining;
    private double total;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(broadcastReceiver, new IntentFilter("update_value"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_value, container, false);

        EditText amount = view.findViewById(R.id.amount);
        remaining = view.findViewById(R.id.remaining);

        //set up recyclerview
        recyclerView = view.findViewById(R.id.value_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        valueListAdapter = new ValueListAdapter(view.getContext());
        recyclerView.setAdapter(valueListAdapter);

        amount.setHint("Amount");
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();

                if (s.isEmpty()){
                    total = 0.0;
                    String v = "Remaining amount: " + total;
                    remaining.setText(v);
                }
                try {
                    total = Double.parseDouble(s);
                    String v = "Remaining amount: " + total;
                    remaining.setText(v);
                }catch (Exception e){
                    total = 0.0;
                    String v = "Remaining amount: " + total;
                    remaining.setText(v);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return view;
    }

    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            double value = intent.getDoubleExtra("value", 0);
            double r = total - value;
            String v = "Remaining amount: " + r;
            remaining.setText(v);

        }
    };

}


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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ValueFragment extends Fragment {

    private RecyclerView recyclerView;
    private ValueListAdapter valueListAdapter;

    private TextView remaining;

    private double total;
    private int persons;

    private String SPILT_AMOUNT_TAG = "SPILT_AMOUNT";
    private String SPILT_COUNT_TAG = "SPILT_COUNT";

    Button calButton;

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

        if (getArguments() != null){
            persons = getArguments().getInt(SPILT_COUNT_TAG, 1);
            total = getArguments().getDouble(SPILT_AMOUNT_TAG, 0.0);
        }
        else{
            total = 0;
            persons = 1;
        }

        EditText amount = view.findViewById(R.id.amount);
        remaining = view.findViewById(R.id.remaining);
        calButton = view.findViewById(R.id.cal_button);

        amount.setText(String.valueOf(total));

        //set up recyclerview
        recyclerView = view.findViewById(R.id.value_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        valueListAdapter = new ValueListAdapter(view.getContext(), persons);
        recyclerView.setAdapter(valueListAdapter);

        amount.setHint("Amount");
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            // update the remaining amount when the edit text field is updated
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

        calButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (total != 0){
                    Toast toast =  Toast.makeText(getContext(), "Remaining value not equal to 0!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        return view;
    }
    // get the updated amount from the recycler view
    // and update the remaining amount
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            double value = intent.getDoubleExtra("value", 0.0);
            double previous = intent.getDoubleExtra("PREVIOUS", 0.0);
            total += previous;
            total -= value;
            String v = "Remaining amount: " + total;
            remaining.setText(v);

        }
    };

}


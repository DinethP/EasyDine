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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.LinkedList;

public class PercentageFragment extends Fragment {

    private RecyclerView recyclerView;
    private PercentageListAdapter percentageListAdapter;

    private LinkedList<String> nameList = new LinkedList<>();
    private LinkedList<Double> percentageList = new LinkedList<>();

    private double total;
    private double percentage;
    private int persons;
    private String SPILT_AMOUNT_TAG = "SPILT_AMOUNT";
    private String SPILT_COUNT_TAG = "SPILT_COUNT";

    TextView textView;
    Button calButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(broadcastReceiver, new IntentFilter("update_percentage"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        percentage = 0;
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_percentage, container, false);
        // get the number of people and the total amount from the PayActivity
        if (getArguments() != null){
            persons = getArguments().getInt(SPILT_COUNT_TAG, 1);
            total = getArguments().getDouble(SPILT_AMOUNT_TAG, 0.0);
        }
        else{
            total = 0;
            persons = 1;
        }
        //set up textview and cal button
        textView = view.findViewById(R.id.total_percentage);
        calButton = view.findViewById(R.id.cal_button);

        //set up recyclerView
        recyclerView = view.findViewById(R.id.percentage_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        percentageListAdapter = new PercentageListAdapter(view.getContext(), total, persons);
        recyclerView.setAdapter(percentageListAdapter);

        // get the amount users need to pay
        EditText amount = view.findViewById(R.id.amount);
        amount.setHint("Amount");
        amount.setText(String.valueOf(total));

        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();
                // when the amount is changed, set up a new recyclerview

                // if the field is empty, pass 0 to the recyclerview adapter
                if(s.isEmpty()){
                    percentageListAdapter = new PercentageListAdapter(view.getContext(), 0.0, persons);
                    recyclerView.setAdapter(percentageListAdapter);
                }
                // if the field is not empty, pass the value to the adapter
                try{
                    total = Double.parseDouble(s);
                    percentageListAdapter = new PercentageListAdapter(view.getContext(), total, persons);
                    recyclerView.setAdapter(percentageListAdapter);
                // if error is encountered, pass 0 to the adapter
                }catch (Exception e){
                    percentageListAdapter = new PercentageListAdapter(view.getContext(), 0.0, persons);
                    recyclerView.setAdapter(percentageListAdapter);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        // check if the percentage equals to 100
        // remind user if it is not
        // go to pastOrderActivity if yes
        calButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (percentage != 100){
                    Toast toast =  Toast.makeText(getContext(), "Percentage not equal to 100%!", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    Intent intent = new Intent(getActivity(), PastOrdersActivity.class);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String email = user.getEmail();
                    intent.putExtra("accountName", email);
                    startActivity(intent);
                }

            }
        });

        return view;

    }
    // receive updates from the list adapter
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            double value = intent.getDoubleExtra("PERCENTAGE", 0);
            double previous = intent.getDoubleExtra("PREVIOUS", 0);
            percentage -= previous;
            percentage += value;
            String v = "Total Percentage: " + percentage;
            textView.setText(v);

        }
    };
}
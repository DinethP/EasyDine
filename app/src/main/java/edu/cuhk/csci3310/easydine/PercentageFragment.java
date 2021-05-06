package edu.cuhk.csci3310.easydine;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.LinkedList;

public class PercentageFragment extends Fragment {

    private RecyclerView recyclerView;
    private PercentageListAdapter percentageListAdapter;

    private LinkedList<String> nameList = new LinkedList<>();
    private LinkedList<Double> percentageList = new LinkedList<>();

    private double total;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_percentage, container, false);

        //set up recyclerView
        recyclerView = view.findViewById(R.id.percentage_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        percentageListAdapter = new PercentageListAdapter(view.getContext(), total);
        recyclerView.setAdapter(percentageListAdapter);

        // get the amount users need to pay
        EditText amount = view.findViewById(R.id.amount);
        amount.setHint("Amount");

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
                    percentageListAdapter = new PercentageListAdapter(view.getContext(), 0.0);
                    recyclerView.setAdapter(percentageListAdapter);
                }
                // if the field is not empty, pass the value to the adapter
                try{
                    total = Double.parseDouble(s);
                    percentageListAdapter = new PercentageListAdapter(view.getContext(), total);
                    recyclerView.setAdapter(percentageListAdapter);
                // if error is encountered, pass 0 to the adapter
                }catch (Exception e){
                    percentageListAdapter = new PercentageListAdapter(view.getContext(), 0.0);
                    recyclerView.setAdapter(percentageListAdapter);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;

    }
}
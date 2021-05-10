package edu.cuhk.csci3310.easydine;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class EqualFragment extends Fragment {

    private int persons;
    private String description;
    private double amount;

    private String SPILT_AMOUNT_TAG = "SPILT_AMOUNT";
    private String SPILT_COUNT_TAG = "SPILT_COUNT";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (getArguments() != null){
            amount = getArguments().getDouble(SPILT_AMOUNT_TAG, 0.0);
            persons = getArguments().getInt(SPILT_COUNT_TAG, 1);
        }
        else{
            amount = 0;
            persons = 1;
        }

        final View view = inflater.inflate(R.layout.fragment_equal, container, false);

        EditText editText1 = (EditText) view.findViewById(R.id.number_of_customers);
        EditText editText2 = (EditText) view.findViewById(R.id.description);
        EditText editText3 = (EditText) view.findViewById(R.id.amount);
        TextView textView = (TextView) view.findViewById(R.id.test_amount);
        Button cal_button = (Button) view.findViewById(R.id.cal_button);

        editText1.setHint("No. of people");
        editText2.setHint("Description");
        editText3.setHint("Amount");

        editText1.setText(String.valueOf(persons+1));
        editText3.setText(String.valueOf(amount));

        // calculate the amount equally
        cal_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                persons = Integer.parseInt(editText1.getText().toString());
                description = editText2.getText().toString();
                amount = Double.parseDouble(editText3.getText().toString());
                textView.setText(String.valueOf(amount / persons));
            }
        });

        return view;

    }
}
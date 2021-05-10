package edu.cuhk.csci3310.easydine;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        EditText editText3 = (EditText) view.findViewById(R.id.amount);
        TextView textView = (TextView) view.findViewById(R.id.test_amount);
        Button cal_button = (Button) view.findViewById(R.id.cal_button);

        editText1.setHint("No. of people");
        editText3.setHint("Amount");

        editText1.setText(String.valueOf(persons+1));
        editText3.setText(String.valueOf(amount));
        textView.setText(String.valueOf( amount / (persons+1) ));

        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                persons = Integer.parseInt(s);
                textView.setText(String.valueOf( amount / (persons+1) ));
            }
        });

        editText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                amount = Double.parseDouble(s);
                textView.setText(String.valueOf( amount / (persons+1) ));
            }
        });

        // go the pastOrderActivity
        cal_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PastOrdersActivity.class);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String email = user.getEmail();
                intent.putExtra("accountName", email);
                startActivity(intent);
            }
        });

        return view;

    }
}
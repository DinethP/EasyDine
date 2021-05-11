package edu.cuhk.csci3310.easydine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.libraries.places.api.model.Place;

import java.util.ArrayList;

public class PayActivity extends AppCompatActivity {
    private String TAG = "PayActivity";
    private RadioButton equalButton;
    private String AMOUNT_TAG = "AMOUNT";
    private String COUNT_TAG = "COUNT";
    private String SPILT_AMOUNT_TAG = "SPILT_AMOUNT";
    private String SPILT_COUNT_TAG = "SPILT_COUNT";
    private String PARTICIPANTS = "PARTICIPANTS";

    private double amount;
    private int persons;
    private String orderId;
    private ArrayList<User> selectedUsers = new ArrayList<>();
    private Place place;
    private OrderSummary orderSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        if (savedInstanceState == null) {
            equalButton = findViewById(R.id.equal);
            equalButton.setChecked(true);

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, EqualFragment.class, null)
                    .commit();
        }

        final RadioGroup radioGroup = findViewById(R.id.radio_group);
        try{
            Bundle extras = getIntent().getExtras();
            amount = extras.getDouble(AMOUNT_TAG, 1.0);
            persons = extras.getInt(COUNT_TAG, 1);
            orderSummary = (OrderSummary) extras.getSerializable("ORDER");
            Log.d(TAG, "Order Summary in Pay Activity: " + orderSummary.userID);
//            place = extras.getParcelable("PLACE");
//            selectedUsers = (ArrayList<User>) extras.getSerializable(PARTICIPANTS);
//            orderId = extras.getString("ORDER_ID");

        }catch(Exception e){
            amount = 0;
            persons = 0;
        }


        Log.d("spilt", String.valueOf(amount));
        Log.d("spilt", String.valueOf(persons));

        Bundle bundle = new Bundle();
        bundle.putDouble(SPILT_AMOUNT_TAG, amount);
        bundle.putInt(SPILT_COUNT_TAG, persons);
        bundle.putSerializable("ORDER", orderSummary);

        EqualFragment equalFragment = new EqualFragment();

        equalFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_view, equalFragment)
                .commit();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                int index = radioGroup.indexOfChild(radioButton);
                switch (index){
                    // equal pay
                    case 0:
                        EqualFragment equalFragment = new EqualFragment();
                        equalFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container_view, equalFragment)
                                .commit();
                        break;
                    // by percentage
                    case 1:
                        PercentageFragment percentageFragment = new PercentageFragment();
                        percentageFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container_view, percentageFragment, null)
                                .commit();
                        break;
                    // by exact value
                    case 2:
                        ValueFragment valueFragment = new ValueFragment();
                        valueFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container_view, valueFragment, null)
                                .commit();
                        break;
                }

            }
        });

    }

}
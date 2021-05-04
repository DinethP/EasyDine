package edu.cuhk.csci3310.easydine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class PayActivity extends AppCompatActivity {

    private RadioButton equalButton;
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

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                int index = radioGroup.indexOfChild(radioButton);
                switch (index){
                    case 0:
                        EqualFragment equalFragment = new EqualFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container_view, equalFragment, null)
                                .setReorderingAllowed(true)
                                .commit();
                        break;
                    case 1:
                        PercentageFragment percentageFragment = new PercentageFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container_view, percentageFragment, null)
                                .setReorderingAllowed(true)
                                .commit();
                        break;
                    case 2:
                        ValueFragment valueFragment = new ValueFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container_view, valueFragment, null)
                                .setReorderingAllowed(true)
                                .commit();
                        break;
                }

            }
        });

    }

}
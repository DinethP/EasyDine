package edu.cuhk.csci3310.easydine;

import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class AnalyticsActivity extends AppCompatActivity {
    String userEmail;
    int graphType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        userEmail = getIntent().getExtras().getString("accountName");
        graphType = getIntent().getExtras().getInt("graph");


        // Show graph 1 (Social Habits) (Pie Chart)
        if (graphType == 1) {
            PieChartFragment pieFrag = new PieChartFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.bottomFragmentContainer, pieFrag);
            transaction.commit();

            // Set radio buttons
            RadioButton graph1Btn = findViewById(R.id.analyticsGraphBtn1);
            RadioButton graph2Btn = findViewById(R.id.analyticsGraphBtn2);
            graph1Btn.setChecked(true);
            graph2Btn.setChecked(false);

            // Show graph 2 (Spending Habits) (ColumnChart)
        } else if (graphType == 2) {
            ColumnChartFragment colFrag = new ColumnChartFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.bottomFragmentContainer, colFrag);
            transaction.commit();

            // Set radio buttons
            RadioButton graph1Btn = findViewById(R.id.analyticsGraphBtn1);
            RadioButton graph2Btn = findViewById(R.id.analyticsGraphBtn2);
            graph1Btn.setChecked(false);
            graph2Btn.setChecked(true);
        }


        RadioGroup rGroup = findViewById(R.id.analyticsRadioGroup);
        RadioButton checkedRadioButton = rGroup.findViewById(rGroup.getCheckedRadioButtonId());

        // Add event listener for radio button change (to update fragment being shown)
        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();

                // If this is the radio button in the group that is actively checked
                if (isChecked) {
                    if (checkedRadioButton.getText().equals("Social Habits")) {
                        getIntent().putExtra("graph", 1);
                    } else if (checkedRadioButton.getText().equals("Spending Habits")) {
                        getIntent().putExtra("graph", 2);
                    }
                }
                recreate();
            }
        });
    }


}

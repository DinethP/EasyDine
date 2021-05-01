package edu.cuhk.csci3310.easydine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PayActivity extends AppCompatActivity {

    private int persons;
    private double amount;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        TextView textView = (TextView) findViewById(R.id.number_of_customers);
        TextView textView1 = (TextView) findViewById(R.id.description);
        TextView textView2 = (TextView) findViewById(R.id.amount);
        TextView textView3 = (TextView) findViewById(R.id.test_amount);
        Button cal_button = (Button) findViewById(R.id.cal_button);

        cal_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                persons = Integer.parseInt(textView.getText().toString());
                description = textView1.getText().toString();
                amount = Double.parseDouble(textView2.getText().toString());
                textView3.setText(String.valueOf(amount / persons));
            }
        });



    }
}
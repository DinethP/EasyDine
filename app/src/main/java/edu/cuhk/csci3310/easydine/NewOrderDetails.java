package edu.cuhk.csci3310.easydine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.libraries.places.api.model.Place;

public class NewOrderDetails extends AppCompatActivity {
    private String TAG = "NewOrderActivity";
    private Place place;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order_details);
        place = getIntent().getParcelableExtra("PLACE");
        Log.d(TAG, "Name: "+place.getName());
    }
}
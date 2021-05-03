package edu.cuhk.csci3310.easydine;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class PlacesActivity extends AppCompatActivity {
    private String apikey = "AIzaSyA4A0EkXxHGQ_0qTMcKvrcwhuQaJJBklPc";
    private String TAG = "PlacesActivity";
    EditText editText;
    TextView textView1;
    TextView textView2;
    Button cancel_btn;
    Button next_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        // Initialize the  Places SDK
        Places.initialize(getApplicationContext(), apikey);
        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        editText = findViewById(R.id.edit_text);
        textView1 = findViewById(R.id.text_view1);
        textView2 = findViewById(R.id.text_view2);
        cancel_btn = findViewById(R.id.cancel_btn);
        next_btn = findViewById(R.id.next_btn);
        editText.setFocusable(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intialise Place field list
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,
                        Place.Field.LAT_LNG, Place.Field.NAME);
                //  create intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList)
                        .build(PlacesActivity.this);
                // Start activity for result
                startActivityForResult(intent, 100);
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlacesActivity.this, NewOrderDetails.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK){
            // Initialise place
            Place place = Autocomplete.getPlaceFromIntent(data);
            // Set address on editText
            editText.setText((place.getAddress()));
            Log.d(TAG, "Address: " + place.getAddress());
            // Set locality name
            textView1.setText(String.format("Locality Namr: %s", place.getName()));
            Log.d(TAG, "Name: " + place.getName());
            // set lat and lng
            textView2.setText(String.valueOf(place.getLatLng()));
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            // display toast
            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
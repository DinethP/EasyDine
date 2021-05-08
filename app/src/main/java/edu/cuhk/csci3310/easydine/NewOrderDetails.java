package edu.cuhk.csci3310.easydine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class NewOrderDetails extends AppCompatActivity implements AddFoodDialog.AddFoodDialogListener{
    private String TAG = "NewOrderActivity";
    private Place place;
    private FoodListAdapter foodListAdapter;
    private RecyclerView recyclerView;
    private LinkedList<Double> foodPrices = new LinkedList<Double>();
    private LinkedList<String> foodNames = new LinkedList<String>();
    private ArrayList<User> selectedParticipants;
    private ArrayList<String> participantNames = new ArrayList<String>();
    Button add_food_button;
    Button submit_button;
    ListView particpantsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order_details);
//        selectedParticipants = (LinkedList<User>) getIntent().getExtras().get("PARTICIPANTS");
        selectedParticipants = (ArrayList<User>) getIntent().getSerializableExtra("PARTICIPANTS");
        Log.d(TAG, "Selected participants in NewOrderDetails: " + selectedParticipants.toString());

        // extract names from User objects
        for(User user : selectedParticipants){
            participantNames.add(user.getUserName());
        }

        particpantsList = findViewById(R.id.participants_view);
        // convert arraylist to string
        String[] namesArray = (String[]) participantNames.toArray(new String[0]);
        particpantsList.setAdapter(new ArrayAdapter<String>(NewOrderDetails.this, R.layout.participantslist_item, R.id.text_name, namesArray));
        add_food_button = findViewById(R.id.add_food_button);
        submit_button = findViewById(R.id.submit_button);
        // connect recyclerview to adapter
        recyclerView = findViewById(R.id.recyclerview);
        foodListAdapter = new FoodListAdapter(this, foodNames, foodPrices);
        recyclerView.setAdapter(foodListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        add_food_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "In submit click");
            }
        });

    }

    private void openDialog() {
        AddFoodDialog addFoodDialog = new AddFoodDialog();
        addFoodDialog.show(getSupportFragmentManager(), "Add food dialog");
    }

    @Override
    // receive food details from AddFoodDialog
    public void applyFoodDetails(String foodName, Double foodPrice) {
        foodNames.add(foodName);
        foodPrices.add(foodPrice);
        // notify recyclerview to update
        foodListAdapter.notifyDataSetChanged();
    }
}
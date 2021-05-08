package edu.cuhk.csci3310.easydine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.DocumentTransform;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NewOrderDetails extends AppCompatActivity implements AddFoodDialog.AddFoodDialogListener{
    private String TAG = "NewOrderActivity";
    private Place place;
    private FoodListAdapter foodListAdapter;
    private RecyclerView recyclerView;
    private LinkedList<Double> foodPrices = new LinkedList<Double>();
    private LinkedList<String> foodNames = new LinkedList<String>();
    private FirebaseFirestore mDatabase;

    Button add_food_button;
    Button submit_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order_details);
        add_food_button = findViewById(R.id.add_food_button);
        submit_button = findViewById(R.id.submit_button);
        // connect recyclerview to adapter
        recyclerView = findViewById(R.id.recyclerview);
        foodListAdapter = new FoodListAdapter(this, foodNames, foodPrices);
        recyclerView.setAdapter(foodListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();
        place = bundle.getParcelable("PLACE");

        add_food_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase = FirebaseFirestore.getInstance();
                CollectionReference orders = mDatabase.collection("orders");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                String restaurantName = place.getName();
                String userID = user.getEmail();
                double sum = getSum(foodPrices);
                LinkedList<String> friends = new LinkedList<String>(Arrays.asList("Alex", "Bob"));
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                Order order = new Order(userID, restaurantName, sum, timeStamp, friends);
                orders.add(order);
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

    public double getSum(LinkedList<Double> foodPrices){
        double sum = 0.0;
        for(Double price : foodPrices){
            sum += price;
        }
        return sum;
    }
}
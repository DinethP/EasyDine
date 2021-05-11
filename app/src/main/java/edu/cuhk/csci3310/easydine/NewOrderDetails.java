package edu.cuhk.csci3310.easydine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.DocumentTransform;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.max;

public class NewOrderDetails extends AppCompatActivity implements AddFoodDialog.AddFoodDialogListener{
    private String TAG = "NewOrderActivity";
    private Place place;
    private FoodListAdapter foodListAdapter;
    private RecyclerView recyclerView;
    private LinkedList<Double> foodPrices = new LinkedList<Double>();
    private LinkedList<String> foodNames = new LinkedList<String>();
    private ArrayList<User> selectedParticipants;
    private ArrayList<String> participantNames = new ArrayList<String>();
    private ArrayList<User> selectedUser = new ArrayList<>();
    private ListView particpantsList;
    private FirebaseFirestore mDatabase;
    private String AMOUNT_TAG = "AMOUNT";
    private String COUNT_TAG = "COUNT";
    private boolean isSingle = true;
    private String firestoreOrderId;

    Button add_food_button;
    Button submit_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order_details);
        if(!getIntent().getBooleanExtra("FLAG", false)){
            selectedParticipants = (ArrayList<User>) getIntent().getSerializableExtra("PARTICIPANTS");
            Log.d(TAG, "Selected participants in NewOrderDetails: " + selectedParticipants.toString());
            // extract names from User objects
            for(User user : selectedParticipants){
                participantNames.add(user.getUserName());
            }
            isSingle = false;
        }

        particpantsList = findViewById(R.id.participants_view);
        // convert arraylist to string
        String[] namesArray = (String[]) participantNames.toArray(new String[0]);
        particpantsList.setAdapter(new ArrayAdapter<String>(NewOrderDetails.this, R.layout.participantslist_item, R.id.text_name, namesArray));
        add_food_button = findViewById(R.id.add_food_button);
        submit_button = findViewById(R.id.submit_button);

        add_food_button = findViewById(R.id.add_food_button);
        submit_button = findViewById(R.id.submit_button);
        // connect recyclerview to adapter
        recyclerView = findViewById(R.id.recyclerview);
        foodListAdapter = new FoodListAdapter(this, foodNames, foodPrices);
        recyclerView.setAdapter(foodListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();
        place = bundle.getParcelable("PLACE");
        selectedUser = (ArrayList<User>) bundle.getSerializable("PARTICIPANTS");

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
                List<PhotoMetadata> metadata = place.getPhotoMetadatas();
                final PhotoMetadata photoMetadata = metadata.get(0);
                // Log.d("photoMetadata",photoMetadata.toString());
                // Log.d("photoMetadata", getPhotoRef(photoMetadata.toString()));

                // get photo url from place api
                String imageURL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + getPhotoRef(photoMetadata.toString()) + "&key=AIzaSyA4A0EkXxHGQ_0qTMcKvrcwhuQaJJBklPc";
                String userID = user.getEmail();
                double sum = getSum(foodPrices);
                // LinkedList<String> friends = new LinkedList<String>(Arrays.asList("Alex", "Bob"));

                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());




                if(isSingle){
                    Intent intent = new Intent(NewOrderDetails.this, PastOrdersActivity.class);
                    Order order = new Order(userID, restaurantName, sum, timeStamp, selectedUser, foodNames, foodPrices, imageURL, true);
                    orders.add(order);
                    intent.putExtra("accountName", userID);
                    startActivity(intent);
                } else{
                    Intent intent = new Intent(NewOrderDetails.this, PayActivity.class);
                    Order order = new Order(userID, restaurantName, sum, timeStamp, selectedUser, foodNames, foodPrices, imageURL, false);
                    orders.add(order).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()){
                                Log.d(TAG, "Group order successfully saved");
                                firestoreOrderId = task.getResult().getId();
                                Log.d(TAG, "OrderID from group: " + firestoreOrderId);

//                                intent.putExtra("PARTICIPANTS", (Serializable) selectedUser);
//                                intent.putExtra("PLACE", place);
//                                intent.putExtra("ORDER_ID", firestoreOrderId);
                                OrderSummary orderSummary = new OrderSummary(firestoreOrderId, userID, restaurantName, sum, timeStamp, selectedUser, foodNames, foodPrices, imageURL, false);
                                intent.putExtra(COUNT_TAG, selectedUser.size());
                                intent.putExtra(AMOUNT_TAG, sum);
                                intent.putExtra("ORDER", orderSummary);
                                startActivity(intent);
                            } else {
                                Log.d(TAG, "Error saving order: ", task.getException());
                            }
                        }
                    });
                }

//                Toast toast =  Toast.makeText(getApplicationContext(), "Order submitted", Toast.LENGTH_SHORT);
//                toast.show();
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

    // get photo reference from regex
    public String getPhotoRef(String mataData){
        Pattern pattern = Pattern.compile("(?<=, photoReference=).*");
        Matcher matcher = pattern.matcher(mataData);
        matcher.find();
        String result = matcher.group(0);
        return result.substring(0, result.length()-1);
    }
}
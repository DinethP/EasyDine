package edu.cuhk.csci3310.easydine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class PastOrdersActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private PastOrdersListAdapter mAdapter;
    private String TAG = "PastOrdersActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final LinkedList<String> mOrderIdList = new LinkedList<>();
    private final LinkedList<String> mRestaurantImageList = new LinkedList<>();
    private final LinkedList<String> mRestaurantNameList = new LinkedList<>();
    private final LinkedList<String> mDateList = new LinkedList<>();
    private final LinkedList<Integer> mFriendsList = new LinkedList<>();
    private final LinkedList<Boolean> mPayedList = new LinkedList<>();
    DateFormat dfParse = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss"); //Format for parsing the Input string
    DateFormat dfOutput = new SimpleDateFormat("E d MMM, yyyy, hh.mm aa"); //Format for formatting the output

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_orders);

        String userEmail = getIntent().getExtras().getString("accountName");

        // Create a reference to the orders collection
        CollectionReference orders = db.collection("orders");

        // Filter query by userID
        Query query = orders.whereEqualTo("userID", userEmail);

        // Get order history
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        mOrderIdList.add(document.getId());
                        mRestaurantImageList.add(document.get("imageURL").toString());
                        mRestaurantNameList.add(document.get("restaurant").toString());
                        // convert date to a more human-readable format
                        mDateList.add(formatDate(document.get("orderTime").toString()));
                        List<String> friends = (List<String>) document.get("friends");
                        // handle single and group order 
                        if (friends == null)
                            mFriendsList.add(0);
                        else
                            mFriendsList.add(friends.size());
                        mPayedList.add((Boolean) document.get("isPayed"));
                    }

                    // Firestore takes time to load, so names get after view is created
                    // so notify adapter to show  updated names
                    mAdapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        // Get a handle to the RecyclerView.
        mRecyclerView = findViewById(R.id.pastOrdersRecyclerView);

        // Create an adapter and supply the data to be displayed
        mAdapter = new PastOrdersListAdapter(this, mOrderIdList, mRestaurantImageList, mRestaurantNameList, mDateList, mFriendsList, mPayedList);

        // Connect the adapter with the RecyclerView.
        mRecyclerView.setAdapter(mAdapter);

        // Give the RecyclerView a default layout manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public String formatDate(String date) {
        Date inputDate;
        // in case the conversion fails, it will still return the string of existing date format
        String formattedDateString = date;
        try {
            inputDate = dfParse.parse(date);
            formattedDateString = dfOutput.format(inputDate);
            Log.d(TAG, formattedDateString);
        } catch (ParseException e) {
            Log.d(TAG,  "Exception: " + e.getMessage());
            e.printStackTrace();
        }
        return formattedDateString;
    }
}

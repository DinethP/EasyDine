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

import java.util.LinkedList;
import java.util.List;


public class PastOrdersActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private PastOrdersListAdapter mAdapter;
    private String TAG = "PastOrdersActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final LinkedList<String> mRestaurantImageList = new LinkedList<>();
    private final LinkedList<String> mRestaurantNameList = new LinkedList<>();
    private final LinkedList<String> mDateList = new LinkedList<>();
    private final LinkedList<Integer> mFriendsList = new LinkedList<>();
    private final LinkedList<Boolean> mPayedList = new LinkedList<>();

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
                        mRestaurantImageList.add(document.get("imageURL").toString());
                        mRestaurantNameList.add(document.get("restaurant").toString());
                        mDateList.add(document.get("orderTime").toString());
                        List<String> friends = (List<String>) document.get("friends");
                        mFriendsList.add(friends.size());
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
        mAdapter = new PastOrdersListAdapter(this, mRestaurantImageList, mRestaurantNameList, mDateList, mFriendsList);

        // Connect the adapter with the RecyclerView.
        mRecyclerView.setAdapter(mAdapter);

        // Give the RecyclerView a default layout manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void markPayed(View view) {
        Log.d("TEST", "Whats poppin");
    }
}

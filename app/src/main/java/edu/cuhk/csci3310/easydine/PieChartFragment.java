package edu.cuhk.csci3310.easydine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class PieChartFragment extends Fragment {
    public AnalyticsActivity analytics_activity;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public PieChartFragment() {
        // Empty Public Constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chart_fragment, container, false);
        analytics_activity = (AnalyticsActivity) getActivity();
        Log.d("PieChart", analytics_activity.userEmail);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Create a reference to the orders collection
        CollectionReference orders = db.collection("orders");

        // Filter query by userID
        Query query = orders.whereEqualTo("userID", analytics_activity.userEmail);

        // Get order history
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    AnyChartView anyChartView = view.findViewById(R.id.chart_view);
                    Pie pie = AnyChart.pie();
                    List<DataEntry> data = new ArrayList<>();
                    HashMap<Integer, Integer> friendCounts = new HashMap<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        List<String> friends = (List<String>) document.get("friends");
                        if (friends == null){
                            friendCounts.put(0, 1);
                        }else{
                            // Increment count
                            if (friendCounts.containsKey(friends.size())) {
                                friendCounts.put(friends.size(), friendCounts.get(friends.size()) + 1);
                            } else {
                                friendCounts.put(friends.size(), 1);
                            }
                        }

                    }

                    for (Map.Entry<Integer, Integer> entry : friendCounts.entrySet()) {
                        int key = entry.getKey();
                        int val = entry.getValue();
                        data.add(new ValueDataEntry(Integer.toString(key), val));
                        Log.d("AnalyticsActivity", Integer.toString(key) + ": " + Integer.toString(val));
                    }

                    pie.data(data);

                    pie.title("How many friends you prefer to eat with");

                    pie.labels().position("outside");

                    pie.legend().title().enabled(true);
                    pie.legend().title()
                            .text("Number of friends")
                            .padding(0d, 0d, 10d, 0d);

                    pie.legend()
                            .position("center-bottom")
                            .itemsLayout(LegendLayout.HORIZONTAL)
                            .align(Align.CENTER);

                    anyChartView.setChart(pie);
                } else {
                    Log.d("AnalyticsActivity", "Error getting documents: ", task.getException());
                }
            }
        });

    }
}

package edu.cuhk.csci3310.easydine;

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
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Align;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.LegendLayout;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ColumnChartFragment extends Fragment {
    public AnalyticsActivity analytics_activity;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public ColumnChartFragment() {
        // Empty Public Constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chart_fragment, container, false);
        analytics_activity = (AnalyticsActivity) getActivity();
        Log.d("ColumnChart", analytics_activity.userEmail);
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
                    Cartesian cartesian = AnyChart.column();
                    List<DataEntry> data = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        data.add(new ValueDataEntry(document.get("orderTime").toString(), (double) document.get("amount")));
                        Log.d("AnalyticsActivity", document.get("orderTime").toString());
                        Log.d("Analytics", Double.toString((double) document.get("amount")));
                    }

                    Column column = cartesian.column(data);

                    column.tooltip()
                            .titleFormat("{%X}")
                            .position(Position.CENTER_BOTTOM)
                            .anchor(Anchor.CENTER_BOTTOM)
                            .offsetX(0d)
                            .offsetY(5d)
                            .format("${%Value}{groupsSeparator: }");

                    cartesian.animation(true);
                    cartesian.title("Meal Costs Over Time");

                    cartesian.yScale().minimum(0d);

                    cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");

                    cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                    cartesian.interactivity().hoverMode(HoverMode.BY_X);

                    cartesian.xAxis(0).title("Date");
                    cartesian.yAxis(0).title("Cost");

                    anyChartView.setChart(cartesian);
                } else {
                    Log.d("AnalyticsActivity", "Error getting documents: ", task.getException());
                }
            }
        });


    }

}

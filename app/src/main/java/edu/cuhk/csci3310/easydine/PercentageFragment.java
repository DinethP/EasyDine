package edu.cuhk.csci3310.easydine;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PercentageFragment extends Fragment {

    private RecyclerView recyclerView;
    private PercentageListAdapter percentageListAdapter;

    private LinkedList<String> nameList = new LinkedList<>();
    private LinkedList<Double> percentageList = new LinkedList<>();

    private double total;
    private double percentage;
    private ArrayList<User> persons;
    private String SPILT_AMOUNT_TAG = "SPILT_AMOUNT";
    private String SPILT_COUNT_TAG = "SPILT_COUNT";

    private String CHANNEL_ID = "channelId";
    private String CHANNEL_NAME = "channelName";
    private int NOTIFICATION_ID = 0;
    private PendingIntent pendingIntent;

    private OrderSummary orderSummary;
    private ArrayList<Double> moneyOwed = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean openedFromNewOrderDetailsActivity = false;

    private String userID, restaurant, orderTime, orderID;
    private double amountPaid, hostOwes;
    private ArrayList<User> friends;
    private LinkedList<String> dishes;
    private LinkedList<Double> prices;
    private String imageURL;
    private boolean isPayed;

    private FirebaseFirestore mDatabase;

    TextView textView;
    Button calButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(broadcastReceiver, new IntentFilter("update_percentage"));
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(broadcastReceiver2, new IntentFilter("PASS_AMOUNT"));
        // create notification channel
        createNotificationChannel();
        // create pending intent so that clicking the notification will open the activity
        Intent intent = new Intent(this.getContext(), LoginActivity.class);
        pendingIntent = TaskStackBuilder.create(this.getContext())
                .addNextIntentWithParentStack(intent)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        percentage = 0;
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_percentage, container, false);
        //set up textview and cal button
        textView = view.findViewById(R.id.total_percentage);
        calButton = view.findViewById(R.id.cal_button);
        calButton.setVisibility(View.GONE);

        // get the number of people and the total amount from the PayActivity
        if (getArguments() != null){
//            persons = getArguments().getInt(SPILT_COUNT_TAG, 1);
            total = getArguments().getDouble(SPILT_AMOUNT_TAG, 0.0);
            // check if we should store order summary to firestore
            if(getArguments().getSerializable("ORDER") != null){
                orderSummary = (OrderSummary) getArguments().getSerializable("ORDER");
                persons = orderSummary.friends;
                openedFromNewOrderDetailsActivity = true;
                calButton.setVisibility(View.VISIBLE);
            }
        }
        else{
            total = 0;
            persons = new ArrayList<User>();
        }

        // init moneyOwed list
        if (persons != null){
            for (int i = 0;i <persons.size()+1; i++){
                moneyOwed.add(i, 0.0);
            }
        }else{
            for (int i = 0;i < 7; i++){
                moneyOwed.add(i, 0.0);
            }
            calButton.setVisibility(View.VISIBLE);
        }


        //set up recyclerView
        recyclerView = view.findViewById(R.id.percentage_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        percentageListAdapter = new PercentageListAdapter(view.getContext(), total, persons);
        recyclerView.setAdapter(percentageListAdapter);

        // get the amount users need to pay
        EditText amount = view.findViewById(R.id.amount);
        amount.setHint("Amount");
        amount.setText(String.valueOf(total));

        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();
                // when the amount is changed, set up a new recyclerview

                // if the field is empty, pass 0 to the recyclerview adapter
                if(s.isEmpty()){
                    percentageListAdapter = new PercentageListAdapter(view.getContext(), 0.0, persons);
                    recyclerView.setAdapter(percentageListAdapter);
                }
                // if the field is not empty, pass the value to the adapter
                try{
                    total = Double.parseDouble(s);
                    percentageListAdapter = new PercentageListAdapter(view.getContext(), total, persons);
                    recyclerView.setAdapter(percentageListAdapter);
                // if error is encountered, pass 0 to the adapter
                }catch (Exception e){
                    percentageListAdapter = new PercentageListAdapter(view.getContext(), 0.0, persons);
                    recyclerView.setAdapter(percentageListAdapter);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (orderSummary != null){
            orderID = orderSummary.orderID;
            userID = orderSummary.userID;
            restaurant = orderSummary.restaurant;
            amountPaid = orderSummary.amount;
            orderTime = orderSummary.orderTime;
            friends = orderSummary.friends;
            dishes = orderSummary.dishes;
            prices = orderSummary.prices;
            imageURL = orderSummary.imageURL;
            isPayed = orderSummary.isPayed;
        }

        // check if the percentage equals to 100
        // remind user if it is not
        // go to pastOrderActivity if yes
        calButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (percentage != 100){
                    Toast toast =  Toast.makeText(getContext(), "Percentage not equal to 100%!", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String userName = user.getDisplayName();
                    // send orderSummary to firebase
                    mDatabase = FirebaseFirestore.getInstance();
                    CollectionReference orderSummaryRef = mDatabase.collection("orderSummary");
                    if (persons != null){
                        OrderSummary summary = new OrderSummary(orderID, userID, userName, restaurant, amountPaid, orderTime, friends, dishes, prices, imageURL, isPayed, moneyOwed.get(0), moneyOwed.subList(1, moneyOwed.size()));
                        //Log.d("MONEY_OWNED", String.valueOf(moneyOwed.subList(1, moneyOwed.size())));
                        orderSummaryRef.add(summary);
                        // to update the isConfirmed field so that the listener in main activity will get fired
                        orderSummaryRef.whereEqualTo("orderID", orderID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("isConfirmed", true);
//                                    complaintsRef.document(document.getId()).set(map, SetOptions.merge());
                                        orderSummaryRef.document(document.getId()).set(map, SetOptions.merge());
                                    }
                                }
                            }
                        });
                    }

                    // show notification on how much to pay
                    Notification notification = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                            .setContentTitle("Get ready to pay")
                            .setContentText(String.format("You need to pay $%s for the recent order at %s", String.format("%.2f", percentageListAdapter.getUserToPayValue()), restaurant))
                            .setSmallIcon(R.drawable.ic_notification)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setContentIntent(pendingIntent)
                            .build();
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
                    notificationManager.notify(NOTIFICATION_ID, notification);
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

            }
        });

        return view;

    }
    // receive updates from the list adapter
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            double value = intent.getDoubleExtra("PERCENTAGE", 0);
            double previous = intent.getDoubleExtra("PREVIOUS", 0);
            percentage -= previous;
            percentage += value;
            String v = "Total Percentage: " + percentage;
            textView.setText(v);

        }
    };

    public BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double amount = intent.getDoubleExtra("AMOUNT", 0);
            int position = intent.getIntExtra("POSITION", 0);
            moneyOwed.set(position, amount);
        }
    };

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
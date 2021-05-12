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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ValueFragment extends Fragment {
    private String TAG = "ValueFragment";
    private RecyclerView recyclerView;
    private ValueListAdapter valueListAdapter;

    private TextView remaining;

    private double total;
    private ArrayList<User> persons;

    private String SPILT_AMOUNT_TAG = "SPILT_AMOUNT";
    private String SPILT_COUNT_TAG = "SPILT_COUNT";

    private String CHANNEL_ID = "channelId";
    private String CHANNEL_NAME = "channelName";
    private int NOTIFICATION_ID = 0;
    private PendingIntent pendingIntent;

    private OrderSummary orderSummary;
    private boolean openedFromNewOrderDetailsActivity = false;

    private ArrayList<Double> moneyOwed = new ArrayList<>();

    private String userID, restaurant, orderTime, orderID;
    private double amountPaid, hostOwes;
    private ArrayList<User> friends;
    private LinkedList<String> dishes;
    private LinkedList<Double> prices;
    private String imageURL;
    private boolean isPayed;

    private FirebaseFirestore mDatabase;

    Button calButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this.getContext()).registerReceiver(broadcastReceiver, new IntentFilter("update_value"));
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_value, container, false);
        calButton = view.findViewById(R.id.cal_button);
        calButton.setVisibility(View.GONE);

        if (getArguments() != null){
//            persons = getArguments().getInt(SPILT_COUNT_TAG, 1);
            total = getArguments().getDouble(SPILT_AMOUNT_TAG, 0.0);

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
            // calButton.setVisibility(View.VISIBLE);
        }

        EditText amount = view.findViewById(R.id.amount);
        remaining = view.findViewById(R.id.remaining);
        calButton = view.findViewById(R.id.cal_button);

        amount.setText(String.valueOf(total));

        //set up recyclerview
        recyclerView = view.findViewById(R.id.value_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        valueListAdapter = new ValueListAdapter(view.getContext(), persons);
        recyclerView.setAdapter(valueListAdapter);

        amount.setHint("Amount");
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            // update the remaining amount when the edit text field is updated
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();

                if (s.isEmpty()){
                    total = 0.0;
                    String v = "Remaining amount: " + total;
                    remaining.setText(v);
                }
                try {
                    total = Double.parseDouble(s);
                    String v = "Remaining amount: " + total;
                    remaining.setText(v);
                }catch (Exception e){
                    total = 0.0;
                    String v = "Remaining amount: " + total;
                    remaining.setText(v);
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

        calButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (total != 0){
                    Toast toast =  Toast.makeText(getContext(), "Remaining value not equal to 0!", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String userName = user.getDisplayName();
                    // show notification on how much to pay
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
                    Notification notification = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                            .setContentTitle("Get ready to pay")
                            .setContentText(String.format("You need to pay $%s for the recent order at %s", String.format("%.2f", valueListAdapter.getUserToPayValue()), restaurant))
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
    // get the updated amount from the recycler view
    // and update the remaining amount
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            double value = intent.getDoubleExtra("value", 0.0);
            double previous = intent.getDoubleExtra("PREVIOUS", 0.0);
            total += previous;
            total -= value;
            String v = "Remaining amount: " + total;
            remaining.setText(v);

        }
    };
    // get individual amount from list
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


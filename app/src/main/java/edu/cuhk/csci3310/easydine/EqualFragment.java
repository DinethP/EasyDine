package edu.cuhk.csci3310.easydine;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

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
import java.util.List;
import java.util.Map;

public class EqualFragment extends Fragment {

    private ArrayList<User> persons;
    private double amount;
    private boolean modified = false;

    private String SPILT_AMOUNT_TAG = "SPILT_AMOUNT";
    private String SPILT_COUNT_TAG = "SPILT_COUNT";
    private String userToPay;

    private String CHANNEL_ID = "channelId";
    private String CHANNEL_NAME = "channelName";
    private int NOTIFICATION_ID = 0;
    private PendingIntent pendingIntent;

    private OrderSummary orderSummary;
    private ArrayList<Double> moneyOwed = new ArrayList<>();
    private boolean openedFromNewOrderDetailsActivity = false;

    private String userID, restaurant, orderTime, orderID;
    private double amountPaid, hostOwes;
    private ArrayList<User> friends;
    private LinkedList<String> dishes;
    private LinkedList<Double> prices;
    private String imageURL;
    private boolean isPayed;

    Button calButton;

    private FirebaseFirestore mDatabase;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        final View view = inflater.inflate(R.layout.fragment_equal, container, false);

        calButton = view.findViewById(R.id.cal_button);
        calButton.setVisibility(View.GONE);
        // Inflate the layout for this fragment
        if (getArguments() != null){
            amount = getArguments().getDouble(SPILT_AMOUNT_TAG, 0.0);
//            persons = getArguments().getInt(SPILT_COUNT_TAG, 1);

            // check if we should store order summary to firestore
            if(getArguments().getSerializable("ORDER") != null){
                orderSummary = (OrderSummary) getArguments().getSerializable("ORDER");
                persons = orderSummary.friends;
                openedFromNewOrderDetailsActivity = true;
                calButton.setVisibility(View.VISIBLE);
            }
        }
        else{
            amount = 0;
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


        EditText editText1 = (EditText) view.findViewById(R.id.number_of_customers);
        EditText editText3 = (EditText) view.findViewById(R.id.amount);
        TextView textView = (TextView) view.findViewById(R.id.test_amount);
        Button cal_button = (Button) view.findViewById(R.id.cal_button);

        editText1.setHint("No. of people");
        editText3.setHint("Amount");
        if (persons != null){
            editText1.setText(String.valueOf(persons.size()+1));
            textView.setText(String.valueOf( amount / (persons.size()+1) ));
            userToPay = String.valueOf( amount / (persons.size()+1) );
        } else{
            editText1.setText(String.valueOf(1));
            textView.setText(String.valueOf(0));
            userToPay = String.valueOf(0);
        }

        editText3.setText(String.valueOf(amount));



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

        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                int currentPersons;
                modified = true;
                if (s.isEmpty()){
                    currentPersons = 0;
                    textView.setText(String.valueOf(0));
                }

                try {
                    currentPersons = Integer.parseInt(s);
                    textView.setText(String.format("%.1f", amount / (currentPersons)));
                }catch (Exception e){
                    currentPersons = 0;
                    textView.setText(String.valueOf(0));
                }
                userToPay = String.valueOf( amount / (currentPersons) );
            }
        });

        editText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                int currentPersons = persons == null ? 1 : persons.size();
                if (s.isEmpty()){
                    amount = 0;
                    textView.setText(String.valueOf(0));
                }
                try {
                    amount = Double.parseDouble(s);
                    if (persons != null)
                        currentPersons = modified ? currentPersons : currentPersons+1;
                    else
                        currentPersons = 1;
                    textView.setText(String.valueOf( amount / currentPersons ));

                }catch(Exception e){
                    amount = 0;
                    textView.setText(String.valueOf(0));
                }

                userToPay = String.valueOf( amount / currentPersons );
            }
        });

        // go the pastOrderActivity
        cal_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userName = user.getDisplayName();
                // send orderSummary to firebase
                mDatabase = FirebaseFirestore.getInstance();
                CollectionReference orderSummaryRef = mDatabase.collection("orderSummary");

                if (persons != null){
                    // get equal amount paid
                    for (int i = 0; i < persons.size()+1; i++){
                        moneyOwed.set(i, Double.parseDouble(textView.getText().toString()));
                    }
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
                        .setContentText(String.format("You need to pay $%s for the recent order", userToPay))
                        .setSmallIcon(R.drawable.ic_notification)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .build();
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
                notificationManager.notify(NOTIFICATION_ID, notification);
                Intent intent = new Intent(getActivity(), PastOrdersActivity.class);
                String email = user.getEmail();
                intent.putExtra("accountName", email);
                startActivity(intent);
            }
        });
        return view;
    }
    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
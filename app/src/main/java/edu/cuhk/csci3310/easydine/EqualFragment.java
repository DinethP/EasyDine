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

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EqualFragment extends Fragment {

    private int persons;
    private double amount;
    private String TAG = "EqualFragment";
    private boolean modified = false;

    private String SPILT_AMOUNT_TAG = "SPILT_AMOUNT";
    private String SPILT_COUNT_TAG = "SPILT_COUNT";
    private String PARTICIPANTS = "PARTICIPANTS";
    private Double userToPay;

    private String CHANNEL_ID = "channelId";
    private String CHANNEL_NAME = "channelName";
    private int NOTIFICATION_ID = 0;
    private PendingIntent pendingIntent;

    private OrderSummary orderSummary;
    private ArrayList<Double> moneyOwed = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean openedFromNewOrderDetailsActivity = false;

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
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_equal, container, false);

        EditText editText1 = (EditText) view.findViewById(R.id.number_of_customers);
        EditText editText3 = (EditText) view.findViewById(R.id.amount);
        TextView textView = (TextView) view.findViewById(R.id.test_amount);
        Button cal_button = (Button) view.findViewById(R.id.cal_button);

        editText1.setHint("No. of people");
        editText3.setHint("Amount");
        cal_button.setVisibility(View.GONE);

        if (getArguments() != null){
            amount = getArguments().getDouble(SPILT_AMOUNT_TAG, 0.0);
            persons = getArguments().getInt(SPILT_COUNT_TAG, 1);
            // bit of a hack, because for some reason even if no arguments are set, getArguments is not null
            // check if we should store order summary to firestore
            if(getArguments().getSerializable("ORDER") != null){
                orderSummary = (OrderSummary) getArguments().getSerializable("ORDER");
                openedFromNewOrderDetailsActivity = true;
                cal_button.setVisibility(View.VISIBLE);
            }
//            Log.d(TAG, "Order summary not null: " + orderSummary.userID);
        }
        else{
            amount = 0;
            persons = 1;
        }
        editText1.setText(String.valueOf(persons+1));
        editText3.setText(String.valueOf(amount));
//        textView.setText(String.valueOf( amount / (persons+1) ));
        textView.setText(String.format("%.2f", amount / (persons+1) ));

        userToPay = ( amount / (persons+1) );
        if(openedFromNewOrderDetailsActivity){
            Log.d(TAG, "openedFormNewOrderDetails: " + openedFromNewOrderDetailsActivity);
            for(User user : orderSummary.friends){
                moneyOwed.add(userToPay);
            }
            orderSummary.moneyOwed = moneyOwed;
            orderSummary.hostOwes = userToPay;
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
                persons = Integer.parseInt(s);
//                textView.setText(String.valueOf( amount / (persons+1) ));
                textView.setText(String.format("%.2f", amount / (persons+1) ));

//                userToPay = String.valueOf( amount / (persons+1) );
                userToPay = ( amount / (persons+1) ) ;
                if(openedFromNewOrderDetailsActivity){
                    for(User user : orderSummary.friends){
                        moneyOwed.add(userToPay);
                    }
                    orderSummary.moneyOwed = moneyOwed;
                    orderSummary.hostOwes = userToPay;
                }
                modified = true;
                if (s.isEmpty()){
                    persons = 0;
                    textView.setText(String.valueOf(0));
                }

                try {
                    persons = Integer.parseInt(s);
                    textView.setText(String.valueOf( amount / (persons) ));
                }catch (Exception e){
                    persons = 0;
                    textView.setText(String.valueOf(0));
                }
                userToPay = String.valueOf( amount / (persons) );
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
                amount = Double.parseDouble(s);
//                textView.setText(String.valueOf( amount / (persons+1) ));
                textView.setText(String.format("%.2f", amount / (persons+1) ));
                userToPay = ( amount / (persons+1) );
                if(openedFromNewOrderDetailsActivity){
                    for(User user : orderSummary.friends){
                        moneyOwed.add(userToPay);
                    }
                    orderSummary.moneyOwed = moneyOwed;
                    orderSummary.hostOwes = userToPay;
                }
                int currentPersons = 0;
                if (s.isEmpty()){
                    amount = 0;
                    textView.setText(String.valueOf(0));
                }
                try {
                    amount = Double.parseDouble(s);
                    currentPersons = modified ? persons : persons+1;
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
                if(openedFromNewOrderDetailsActivity){
                    db.collection("orderSummary").add(orderSummary);
                }

                // show notification on how much to pay
                Notification notification = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                        .setContentTitle("Get ready to pay")
                        .setContentText(String.format("You need to pay $%.2f for the recent order", userToPay))
                        .setSmallIcon(R.drawable.ic_notification)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .build();
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getActivity());
                notificationManager.notify(NOTIFICATION_ID, notification);
                Intent intent = new Intent(getActivity(), PastOrdersActivity.class);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
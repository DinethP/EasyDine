package edu.cuhk.csci3310.easydine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private String TAG = "MainActivity";
    public String userDisplayName;
    public String userEmail;

    private RecyclerView mRecyclerView;
    private CardListAdapter mAdapter;

    private LinkedList<String> mCardName = new LinkedList<>(Arrays.asList("New Order", "Past Orders", "Quick Bill Split", "Analytics", "About Us", "Coming Soon!"));

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    private String CHANNEL_ID = "channelId";
    private String CHANNEL_NAME = "channelName";
    private int NOTIFICATION_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create notification channel
        createNotificationChannel();
        // create pending intent so that clicking the notification will open the activity
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(intent)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

//        FirebaseUser account = getIntent().getParcelableExtra("ACCOUNT");
        FirebaseUser account = FirebaseAuth.getInstance().getCurrentUser();
        userEmail = account.getEmail();
        userDisplayName = account.getDisplayName();
        User currentUser = new User(userDisplayName);
        ImageView profilePic = findViewById(R.id.profilePic);
        TextView title = findViewById(R.id.greeting);
        title.setText("Hello " + account.getDisplayName());
        if (account.getPhotoUrl() != null) {
            // TODO: photoURL is always null
            Log.d(TAG, "Photo URL: " + account.getPhotoUrl());
            profilePic.setImageURI(account.getPhotoUrl());
        }
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        // set toolbar as the actionbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // make nav_menu items clickable
        navigationView.bringToFront();
        //  This is what makes the nav_menu open and close
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        //navigation menu clickable items
        navigationView.setNavigationItemSelectedListener(this);
        //  set initial checked nav item
        // navigationView.setCheckedItem(R.id.nav_dashboard);

        // set up recyclerview
        mRecyclerView = findViewById(R.id.recyclerview);
        mAdapter = new CardListAdapter(this, mCardName, userEmail);
        mRecyclerView.setAdapter(mAdapter);
        // listen for orders collection changes
        db.collection("orders")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("TAG", "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "New document added");
                                    break;
                                case MODIFIED:
                                    // create notification
                                    // since the only time a order collection document will be modified is to mark the order as true, show a notification
                                    ArrayList<Map> friends = (ArrayList<Map>) dc.getDocument().get("friends");

                                    String userID = dc.getDocument().getString("userID");


                                    if (userID.equals(userEmail) || checkCurrUserInFriendsList(friends)) {
                                        Notification notification = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                                                .setContentTitle("Marked as paid")
                                                .setContentText(String.format("Order at %s was marked as paid by participant", dc.getDocument().get("restaurant")))
                                                .setSmallIcon(R.drawable.icon2)
                                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                .setContentIntent(pendingIntent)
                                                .build();

                                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                                        notificationManager.notify(NOTIFICATION_ID, notification);
                                    }
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed document");
                                    break;
                            }
                        }

                    }
                });

        db.collection("orderSummary")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("TAG", "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "New document added");
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Document modified");
                                    // create notification
                                    // since the only time a order collection document will be modified is to mark the order as true, show a notification
                                    ArrayList<Map> friends = (ArrayList<Map>) dc.getDocument().get("friends");
                                    ArrayList<Map> moneyOwed = (ArrayList<Map>) dc.getDocument().get("moneyOwed");
                                    String restaurant = (String) dc.getDocument().get("restaurant");
                                    String hostName = (String) dc.getDocument().get("hostName");
                                    String userID = dc.getDocument().getString("userID");
                                    // check if current user is a participant of the order
                                    if (checkCurrUserInFriendsList(friends)) {
                                        int userIndexInList = getCurrUserInFriendsListPos(friends);
                                        Notification notification = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                                                .setContentTitle(String.format("Order submitted by %s at %s", hostName, restaurant))
                                                .setContentText(String.format("You need to pay $%.2f for the recent order", moneyOwed.get(userIndexInList)))
                                                .setSmallIcon(R.drawable.icon2)
                                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                .setContentIntent(pendingIntent)
                                                .build();

                                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                                        notificationManager.notify(NOTIFICATION_ID, notification);
                                    }
                                    break;
                                case REMOVED:
                                    break;
                            }
                        }
                    }
                });

        Log.d(TAG, "photo url: " + account.getPhotoUrl());
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        switch (item.getItemId()) {
            case R.id.nav_new_orders:
                Intent newOrderintent = new Intent(MainActivity.this, PlacesActivity.class);
                newOrderintent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(newOrderintent);
                break;
            case R.id.nav_past_orders:
                Intent pastOrderIntent = new Intent(MainActivity.this, PastOrdersActivity.class);
                pastOrderIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pastOrderIntent.putExtra("accountName", email);
                startActivity(pastOrderIntent);
                break;
            case R.id.nav_split_bill:
                Intent splitBillIntent = new Intent(MainActivity.this, PayActivity.class);
                splitBillIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(splitBillIntent);
                break;
            case R.id.nav_analytics:
                Intent analyticsIntent = new Intent(MainActivity.this, AnalyticsActivity.class);
                analyticsIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                analyticsIntent.putExtra("accountName", email);
                startActivity(analyticsIntent);
                break;
            case R.id.nav_about_us:
                Intent aboutUsIntent = new Intent(MainActivity.this, AboutUsActivity.class);
                aboutUsIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(aboutUsIntent);
                break;
            case R.id.logout:
                // open loginActivity again to logout
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                loginIntent.putExtra("LOGOUT", true);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private boolean checkCurrUserInFriendsList(ArrayList<Map> friends) {
        Log.d(TAG, "Entered Check function");
        Log.d(TAG, "size of friends list: " + friends.size());

        for (Map entry : friends) {
            Log.d(TAG, "Friend name: " + entry.get("userName"));
            if (entry.get("userName").equals(userDisplayName)) {
                return true;
            }
        }
        return false;
    }

    private int getCurrUserInFriendsListPos(ArrayList<Map> friends) {
        Log.d(TAG, "Entered Check function");
        Log.d(TAG, "size of friends list: " + friends.size());
        int pos = -1;
        for (int i = 0; i < friends.size(); i++) {
            Map entry = friends.get(i);
            Log.d(TAG, "Friend name: " + entry.get("userName"));
            if (entry.get("userName").equals(userDisplayName)) {
                pos = i;
                break;
            }
        }
        return pos;
    }
}
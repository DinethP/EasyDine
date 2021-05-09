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
import android.content.SharedPreferences;
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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private String TAG = "MainActivity";
    private String KEY = "isLoggedIn";
    private String preferencesName = "UserDetails";
    public String accountDisplayName;

    private RecyclerView mRecyclerView;
    private CardListAdapter mAdapter;

    private LinkedList<String> mCardName = new LinkedList<>(Arrays.asList("New Order", "Past Orders", "Split Bill", "Analytics", "About Us!", "Coming Soon"));

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    private String CHANNEL_ID = "channelId";
    private String CHANNEL_NAME = "channelName";
    private int NOTIFICATION_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getApplicationContext().getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        setContentView(R.layout.activity_main);

        // create notification channel
        createNotificationChannel();
        // create pending intent so that clicking the notification will open the activity
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(intent)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Marked as paid")
                .setContentText("Context text")
                .setSmallIcon(R.drawable.ic_notification)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        TextView dashTitle = findViewById(R.id.textView);
        dashTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationManager.notify(NOTIFICATION_ID, notification);
            }
        });

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
                                    Log.d(TAG, "Modified document: " + dc.getDocument());
                                    // create notification
                                    // this is a hack solution because firestore doesm'y allow to listen for field changes, only whole document changes
                                    // since the only time a document will be modified is to mark as true, this is  correct
                                    Notification notification = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                                            .setContentTitle("Marked as paid")
                                            .setContentText(String.format("Order at %s was marked as paid by participant", dc.getDocument().get("restaurant")))
                                            .setSmallIcon(R.drawable.ic_notification)
                                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                                            .setContentIntent(pendingIntent)
                                            .build();

                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                                    notificationManager.notify(NOTIFICATION_ID, notification);
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed document");
                                    break;
                            }
                        }

                    }
                });

        FirebaseUser account = getIntent().getParcelableExtra("ACCOUNT");
        accountDisplayName = account.getEmail();
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
        navigationView.setCheckedItem(R.id.nav_dashboard);

        // set up recyclerview
        mRecyclerView = findViewById(R.id.recyclerview);
        mAdapter = new CardListAdapter(this, mCardName, accountDisplayName);
        mRecyclerView.setAdapter(mAdapter);


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
        switch (item.getItemId()){
            case R.id.nav_dashboard:
                break;
            case R.id.nav_profile:
                Intent profileIntent = new Intent(MainActivity.this, MyProfile.class);
                startActivity(profileIntent);
                break;
            case R.id.logout:
                // open loginActivity again to logout
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(KEY, false);
                editor.commit();
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
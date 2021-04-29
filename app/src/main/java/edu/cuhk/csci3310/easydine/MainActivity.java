package edu.cuhk.csci3310.easydine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private String TAG = "MainActivity";
    private String KEY = "isLoggedIn";
    private String preferencesName = "UserDetails";

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getApplicationContext().getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        GoogleSignInAccount account = getIntent().getParcelableExtra("ACCOUNT");
        ImageView profilePic = findViewById(R.id.profilePic);
        TextView title = findViewById(R.id.greeting);
        title.setText("Hello " + account.getGivenName());
        if (account.getPhotoUrl() != null) {
            profilePic.setImageURI(account.getPhotoUrl());
        }
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        // set toolbar as the actionbar
        setSupportActionBar(toolbar);
        // make nav_menu items clickable
        navigationView.bringToFront();
        //  This is what makes the nav_menu open and close
        //  TODO: For some reason, the hamburger menu icon is missing - it is set in xml file
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        //navigation menu clickable items
        navigationView.setNavigationItemSelectedListener(this);
        //  set initial checked nav item
        navigationView.setCheckedItem(R.id.nav_dashboard);
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
}
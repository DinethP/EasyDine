package edu.cuhk.csci3310.easydine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GoogleSignInAccount account =getIntent().getParcelableExtra("ACCOUNT");
        ImageView profilePic = findViewById(R.id.profilePic);
        TextView title = findViewById(R.id.greeting);
        title.setText("Hello " + account.getGivenName());
//        profilePic.setImageURI(account.getPhotoUrl());

        Log.d(TAG, "photo url: " + account.getPhotoUrl());
    }
}
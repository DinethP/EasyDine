package edu.cuhk.csci3310.easydine;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class PastOrderDetail extends AppCompatActivity {


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_order_detail);

        String s;

        //get data from intent
        String imageURL = getIntent().getStringExtra("IMAGE");
        String name = getIntent().getStringExtra("NAME");
        String date = getIntent().getStringExtra("DATE");
        double amount = getIntent().getDoubleExtra("AMOUNT", 0.0);
        int count = getIntent().getIntExtra("COUNT_PEOPLE", 0);
        ArrayList<String> friends = getIntent().getStringArrayListExtra("FRIENDS");

        ImageView icon = (ImageView) findViewById(R.id.restaurantIcon);
        TextView rname = (TextView) findViewById(R.id.pastOrdersRestaurantName);
        TextView timestamp = (TextView) findViewById(R.id.timestamp);
        TextView totalAmount = (TextView) findViewById(R.id.amount);
        TextView number = (TextView) findViewById(R.id.num_of_friends);
        TextView friendList = (TextView) findViewById(R.id.friends_list);

        // set up icon
        Picasso.get().load(imageURL).into(icon);
        // concat friend list
        if (friends.size() > 0) {
            String listString = friends.toString();
            s = listString.substring(1, listString.length() - 1);
        }else{
            s = "Individual Order";
        }

        rname.setText("Name: "+ name);
        timestamp.setText("Date: " + date);
        totalAmount.setText("Amount: $ " + amount);
        number.setText("# of friends: " + count);
        friendList.setText("List of friends: " + s);
    }
}
package edu.cuhk.csci3310.easydine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

public class Order {
    public String userID, restaurant, orderTime;
    public double amount;
    public ArrayList<User> friends;
    public LinkedList<String> dishes;
    public LinkedList<Double> prices;
    public String imageURL;
    public boolean isPayed;

    public Order(){
    }

    public Order(String userID, String restaurant, double amount, String orderTime, ArrayList<User> friends, LinkedList<String> dishes, LinkedList<Double> prices, String imageURL, boolean isPayed){
        this.amount = amount;
        this.orderTime = orderTime;
        this.restaurant = restaurant;
        this.userID = userID;
        this.friends = friends;
        this.dishes = dishes;
        this.prices = prices;
        this.imageURL = imageURL;
        this.isPayed = isPayed;
    }

}

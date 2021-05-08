package edu.cuhk.csci3310.easydine;

import java.util.LinkedList;
import java.util.Map;

public class Order {
    public String userID, restaurant, orderTime;
    public double amount;
    public LinkedList<String> friends;

    public Order(){
    }

    public Order(String userID, String restaurant, double amount, String orderTime, LinkedList<String> friends){
        this.amount = amount;
        this.orderTime = orderTime;
        this.restaurant = restaurant;
        this.userID = userID;
        this.friends = friends;
    }

}

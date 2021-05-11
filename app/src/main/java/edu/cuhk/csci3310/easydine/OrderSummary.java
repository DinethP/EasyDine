package edu.cuhk.csci3310.easydine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class OrderSummary implements Serializable {
    public String userID, restaurant, orderTime, orderID;
    public double amount, hostOwes;
    public ArrayList<User> friends;
    public LinkedList<String> dishes;
    public LinkedList<Double> prices;
    public String imageURL;
    public boolean isPayed;
    public ArrayList<Double> moneyOwed;

    public OrderSummary(){
    }

    public OrderSummary(String orderID, String userID, String restaurant, double amount, String orderTime, ArrayList<User> friends, LinkedList<String> dishes, LinkedList<Double> prices, String imageURL, boolean isPayed){
        this.amount = amount;
        this.orderTime = orderTime;
        this.restaurant = restaurant;
        this.userID = userID;
        this.friends = friends;
        this.dishes = dishes;
        this.prices = prices;
        this.imageURL = imageURL;
        this.isPayed = isPayed;
        this.orderID = orderID;

    }

    public void setMoneyOwed(ArrayList<Double> moneyOwed) {
        this.moneyOwed = moneyOwed;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }
}

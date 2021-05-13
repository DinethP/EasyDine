package edu.cuhk.csci3310.easydine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class OrderSummary implements Serializable {
    public String userID, restaurant, orderTime, orderID, hostName;
    public double amount, hostOwes;
    public ArrayList<User> friends;
    public LinkedList<String> dishes;
    public LinkedList<Double> prices;
    public String imageURL;
    public boolean isPayed;
    public boolean isConfirmed = false;
    public List<Double> moneyOwed;

    public OrderSummary() {
    }

    public OrderSummary(String orderID, String userID, String hostName, String restaurant, double amount, String orderTime, ArrayList<User> friends, LinkedList<String> dishes, LinkedList<Double> prices, String imageURL, boolean isPayed) {
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
        this.hostName = hostName;

    }

    public OrderSummary(String orderID, String userID, String hostName, String restaurant, double amount, String orderTime, ArrayList<User> friends, LinkedList<String> dishes, LinkedList<Double> prices, String imageURL, boolean isPayed, double hostOwes, List<Double> moneyOwed) {
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
        this.hostOwes = hostOwes;
        this.moneyOwed = moneyOwed;
        this.hostName = hostName;
    }

    public void setMoneyOwed(ArrayList<Double> moneyOwed) {
        this.moneyOwed = moneyOwed;
    }

    public void setHostOwes(Double hostOwes) {
        this.hostOwes = hostOwes;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }
}

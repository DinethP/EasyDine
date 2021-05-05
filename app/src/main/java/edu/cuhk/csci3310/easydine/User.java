package edu.cuhk.csci3310.easydine;

public class User {
    private String userName;

    //required default constructor
    public User() {
    }

    public User(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}

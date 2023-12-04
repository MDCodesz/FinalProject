package edu.uga.cs.finalproject;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class represents a single PurchasedList, including the name,
 * totalPrice, and date.
 */
public class PurchasedList {
    private String key;
    private String name;
    private String date;
    private double totalPrice;

    public PurchasedList()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy"); // Define the date format

        this.key = null;
        this.name = null;
        this.date = dateFormat.format(new Date()); // Set the current date
        this.totalPrice = 0.0;
    }

    public PurchasedList( String name, String date, double totalPrice) {
        this.key = null;
        this.name = name;
        this.date = date;
        this.totalPrice = totalPrice;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return name;
    }

    public void setDate(String name) {
        this.name = name;
    }

    public double getPrice() {
        return totalPrice;
    }

    public void setPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String toString() {
        return name + " " + date + " " + totalPrice;
    }
}

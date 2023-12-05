package edu.uga.cs.finalproject;


import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a single PurchasedList, including the name,
 * totalPrice, and date.
 */
public class PurchasedList {
    private String key;
    private String name;
    private String date;
    private double totalPrice;
    private Map<String, PurchasedItem> purchasedItems;

    public PurchasedList()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy"); // Define the date format

        this.key = null;
        this.name = null;
        this.date = dateFormat.format(new Date()); // Set the current date
        this.totalPrice = 0.0;
        this.purchasedItems = new HashMap<>();
    }

    public PurchasedList( String name, String date, double totalPrice) {
        this.key = null;
        this.name = name;
        this.date = date;
        this.totalPrice = totalPrice;
    }

    public PurchasedList( String name, String date, double totalPrice, Map<String, PurchasedItem> purchasedItems) {
        this.key = null;
        this.name = name;
        this.date = date;
        this.totalPrice = totalPrice;
        this.purchasedItems = purchasedItems != null ? new HashMap<>(purchasedItems) : new HashMap<>();
    }

    public PurchasedList(String key, String name, String date, double totalPrice, Map<String, PurchasedItem> purchasedItems) {
        this.key = key;
        this.name = name;
        this.date = date;
        this.totalPrice = totalPrice;
        this.purchasedItems = purchasedItems != null ? new HashMap<>(purchasedItems) : new HashMap<>();
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
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getPrice() {
        return totalPrice;
    }

    public void setPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Map<String, PurchasedItem> getPurchasedItems() {
        return purchasedItems;
    }

    public void setPurchasedItems(Map<String, PurchasedItem> purchasedItems) {
        this.purchasedItems = purchasedItems;
    }

    // Method to add purchased item to the list
    public void addPurchasedItem(String itemId, PurchasedItem item) {
        if (purchasedItems == null) {
            purchasedItems = new HashMap<>();
        }
        purchasedItems.put(itemId, item);
    }

    // Method to get a specific purchased item by its ID
    public PurchasedItem getPurchasedItem(String itemId) {
        if (purchasedItems != null) {
            return purchasedItems.get(itemId);
        }
        return null;
    }

    // Method to remove a purchased item by its ID
    public void removePurchasedItem(String itemId) {
        if (purchasedItems != null) {
            purchasedItems.remove(itemId);
        }
    }


    public String toString() {
        return name + " " + date + " " + totalPrice + " " + purchasedItems;
    }
}

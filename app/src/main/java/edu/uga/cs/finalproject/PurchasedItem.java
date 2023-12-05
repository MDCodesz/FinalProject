package edu.uga.cs.finalproject;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class represents a single PurchasedItem, including the itemName,
 * totalPrice, and date.
 */
public class PurchasedItem {
    private String key;
    private String itemName;

    public PurchasedItem()
    {
        this.key = null;
        this.itemName = null;
    }

    public PurchasedItem( String itemName) {
        this.key = null;
        this.itemName = itemName;
    }

    public PurchasedItem(String key, String itemName) {
        this.key = key;
        this.itemName = itemName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }


    public String toString() {
        return itemName;
    }
}

package edu.uga.cs.finalproject;


/**
 * This class represents a single list item, including the item name and price
 */
public class ListItem {
    private String key;
    private String itemName;
    private String user;
    private double price;

    public ListItem()
    {
        this.key = null;
        this.itemName = null;
        this.price = 0.0;
    }

    public ListItem( String itemName, double price) {
        this.key = null;
        this.itemName = itemName;
        this.price = price;
    }


    public ListItem( String itemName) {
        this.key = null;
        this.itemName = itemName;
        this.price = 0.0;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    public void setUser(String userIn) {this.user = userIn;}
    public String getUser() {return this.user;}
    public String toString() {
        return itemName + " " + price;
    }

}

package edu.uga.cs.finalproject;
/**
 * This class represents a single basket list item, including the company name,
 * price number, URL, and some comments.
 */
public class BasketListItem {
    private String key;
    private String itemName;
    private double price;

    public BasketListItem()
    {
        this.key = null;
        this.itemName = null;
        this.price = 0.0;
    }

    public BasketListItem( String itemName, double price) {
        this.key = null;
        this.itemName = itemName;
        this.price = price;
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

    public String toString() {
        return itemName + " " + price;
    }
}

package edu.utep.cs.cs4330.pricewatcher;

import java.util.ArrayList;
import java.util.List;

public class Item {

//--ATTRIBUTES--------------------------------------------------------------------------------------

    private static List<Item> allItems = new ArrayList<>();
    private String item;
    private double curr_price;
    private double init_price;
    private int price_change;
    private String url;
    //Database
    private int id;

//--CONSTRUCTORS------------------------------------------------------------------------------------

    public Item(int id, String item, double curr_price, double init_price, int price_change, String url) {
        this.id = id;
        this.item = item;
        this.curr_price = curr_price;
        this.init_price = init_price;
        this.price_change = price_change;
        this.url = url;
    }

    public Item(String item, double curr_price, double init_price, int price_change, String url) {
        this.item = item;
        this.curr_price = curr_price;
        this.init_price = init_price;
        this.price_change = price_change;
        this.url = url;
    }

//--SETTERS-----------------------------------------------------------------------------------------

    public void setItem(String item) { this.item = item; }
    public void setCurrentPrice(double curr_price) { this.curr_price = curr_price; }
    public void setInitialPrice(double init_price) { this.init_price = init_price; }
    public void setPriceChange(int price_change) { this.price_change = price_change; }
    public void setUrl(String url) { this.url = url; }
    //Database
    public void setId(int id) {this.id = id;}

//--GETTERS-----------------------------------------------------------------------------------------

    public String getItem() { return item; }
    public double getCurrentPrice() { return curr_price; }
    public double getInitialPrice() { return init_price; }
    public int getPriceChange() { return price_change; }
    public String getUrl() { return url; }
    public static List<Item> allItems() { return allItems; }
    //Database
    public int getId() { return id;}
}